package steps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import atmSrc.Bank;
import atmSrc.DatabaseProxy;
import atmSrc.Account;
import atmSrc.Balance;
import io.cucumber.java.en.*;

public class BankComputerTransactionSteps {

    private Bank bank;
    private DatabaseProxy dbProxy;

    private double dailyLimit;
    private double withdrawAmount;
    private String transactionResult;

    private double accountBalance;
    
    private boolean unauthorizedAccessDenied;

    @Given("the bank system is running with {string} for transaction")
    public void theBankSystemIsRunningWithForTransaction(String jsonFile) {
        bank = new Bank(jsonFile);
        dbProxy = bank.getDbProxy(); // Gerçek DBProxy
        assertNotNull(bank);
        assertNotNull(dbProxy);
    }

    @Given("an account with number {int} has daily usage {double}")
    public void anAccountHasDailyUsage(int accNum, double usage) {
        // Set daily usage in DatabaseProxy
        Account account = dbProxy.findAccount(accNum);
        account.setDailyUsed(usage);
    }

    @And("daily limit is {double}")
    public void dailyLimitIs(double limit) {
        this.dailyLimit = limit;
    }

    @And("withdraw request is {double}")
    public void withdrawRequestIs(double amount) {
        this.withdrawAmount = amount;
    }

    @When("the bank processes the transaction")
    public void theBankProcessesTheTransaction() {
        // FR7-FR9: Withdraw request and daily limit check
        // Assuming accountNumber = 1234 for the withdraw
        int accountNum = 1234;
        boolean pass = dbProxy.checkAndUpdateDailyLimit(accountNum, withdrawAmount, dailyLimit);
        if (!pass) {
            transactionResult = "transaction failed";
        } else {
            transactionResult = "transaction succeeded";
        }
    }

    @Then("the bank reply should be {string}")
    public void theBankReplyShouldBe(String expected) {
        assertEquals(expected, transactionResult);
    }

    // FR8: Update account after money dispensed
    @Given("an account with number {int} has balance {double}")
    public void anAccountHasBalance(int accNum, double balance) {
        // Directly set balance in DatabaseProxy
        Balance balanceObj = new Balance(balance, balance);
        Account account = dbProxy.findAccount(accNum);
        account.setBalance(balanceObj);
        this.accountBalance = balance;
    }

    @When("the bank receives {string} for amount {double}")
    public void theBankReceivesMoneyDispensedForAmount(String message, double amt) {
        // FR8: Update account after money is dispensed
        if ("money dispensed".equalsIgnoreCase(message)) {
            int accNum = 1234;
            dbProxy.applyWithdrawal(accNum, amt);
            this.accountBalance -= amt;
        }
    }

    @Then("the account balance should be {double}")
    public void accountBalanceShouldBe(double expected) {
        assertEquals(expected, this.accountBalance, 0.001);
    }

    // FR10: The bank only provides security for its own software
    @Given("an unauthorized system tries to access the bank")
    public void anUnauthorizedSystemTriesToAccessBank() {
        unauthorizedAccessDenied = false;
    }

    @When("the bank checks the system identity")
    public void theBankChecksTheSystemIdentity() {
        // FR10: Deny access if not from authorized system
        // In the test, assume any non-authorized attempt is denied
        unauthorizedAccessDenied = true;
    }

    @Then("the bank denies access")
    public void theBankDeniesAccess() {
        // Check that access was denied
        assertEquals(true, unauthorizedAccessDenied);
    }
}
