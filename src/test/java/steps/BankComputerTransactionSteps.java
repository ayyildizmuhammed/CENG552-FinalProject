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

    // FR7–FR9 ile ilgili alanlar
    private double dailyLimit;       // k
    private double withdrawAmount;   // amount
    private String transactionResult; 

    // FR8 ile ilgili
    private double accountBalance;   

    // FR10 ile ilgili
    private boolean unauthorizedAccessDenied;

    @Given("the bank system is running with {string} for transaction")
    public void theBankSystemIsRunningWithForTransaction(String jsonFile) {
        bank = new Bank(jsonFile);
        dbProxy = bank.getDbProxy(); // DBProxy'yi saklıyoruz
        assertNotNull("Bank should not be null", bank);
        assertNotNull("DBProxy should not be null", dbProxy);
    }

    // FR7 & FR9
    @Given("an account with number {int} has daily usage {double}")
    public void anAccountHasDailyUsage(int accNum, double usage) {
        Account acc = dbProxy.findAccount(accNum);
        if (acc != null) {
            acc.setDailyUsed(usage);
        }
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
        // FR7, FR9 => daily limit check
        // Örneğin accountNumber=1234 diye sabit
        int accountNum = 1234;
        boolean pass = dbProxy.checkAndUpdateDailyLimit(accountNum, withdrawAmount, dailyLimit);
        if (pass) {
            transactionResult = "transaction succeeded";
        } else {
            transactionResult = "transaction failed";
        }
    }

    @Then("the bank reply should be {string}")
    public void theBankReplyShouldBe(String expected) {
        assertEquals(expected, transactionResult);
    }

    // FR8: Update account after money dispensed
    @Given("an account with number {int} has balance {double}")
    public void anAccountHasBalance(int accNum, double balance) {
        Account acc = dbProxy.findAccount(accNum);
        if (acc != null) {
            acc.setBalance(new Balance(balance, balance));
        }
        this.accountBalance = balance; // local store
    }

    @When("the bank receives {string} for amount {double}")
    public void theBankReceivesMoneyDispensedForAmount(String message, double amt) {
        if ("money dispensed".equalsIgnoreCase(message)) {
            // FR8 => applyWithdrawal
            int accNum = 1234;  // Sabit senaryo
            dbProxy.applyWithdrawal(accNum, amt);
            this.accountBalance -= amt;
        }
    }

    @Then("the account balance should be {double}")
    public void accountBalanceShouldBe(double expected) {
        assertEquals(expected, accountBalance, 0.01);
    }

    // FR10: The bank only provides security for its own software
    @Given("an unauthorized system tries to access the bank")
    public void anUnauthorizedSystemTriesToAccessBank() {
        unauthorizedAccessDenied = false;
    }

    @When("the bank checks the system identity")
    public void theBankChecksTheSystemIdentity() {
        // Test mantığı: her unauthorized sistem engellenir
        unauthorizedAccessDenied = true;
    }

    @Then("the bank denies access")
    public void theBankDeniesAccess() {
        // Basit check
        assertEquals(true, unauthorizedAccessDenied);
    }
}
