

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


import io.cucumber.java.en.*;

public class BankComputerAuthorizationSteps {

    private Bank bank;  // Gerçek Bank nesnesi
    private String bankCodeResult;
    private String finalResult;

    private int bankCode;
    private int accountNumber;
    private String accountStatus;
    private String password;

    @Given("the bank system is running with {string}")
    public void theBankSystemIsRunningWith(String jsonFile) {
        // Gerçek bank nesnesini json'dan yükleyelim
        bank = new Bank(jsonFile);
        assertNotNull(bank);
    }

    @Given("the bank code is {int}")
    public void theBankCodeIs(int code) {
        this.bankCode = code;
    }

    @When("the bank verifies only the bank code")
    public void theBankVerifiesOnlyBankCode() {
        // FR1-FR2 logic => Bank.isValidBankCode
        boolean valid = bank.isValidBankCode(bankCode);
        if (valid) {
            bankCodeResult = "valid bank code";
        } else {
            bankCodeResult = "bad bank code";
        }
    }

    @Then("the result should be {string}")
    public void theResultShouldBe(String expected) {
        assertEquals(expected, bankCodeResult);
    }

    // FR3–FR6
    @Given("an account with number {int} has status {string}")
    public void anAccountWithNumberHasStatus(int accNum, String status) {
        this.accountNumber = accNum;
        this.accountStatus = status;
        // Not: bankData.json içinde bu account varsa "active"/"frozen" vs. durumu
    }

    @And("the password {string}")
    public void thePasswordIs(String pwd) {
        this.password = pwd;
    }

    @When("the bank verifies the card with password")
    public void theBankVerifiesCardPassword() {
        // FR1-FR6 => bank.verifyAccount(bankCode, accountNumber, password)
        String result = bank.verifyAccount(bankCode, accountNumber, password);
        finalResult = result; 
        // result: "bad bank code", "bad password", "bad account", "account ok"
    }

    @Then("the bank result should be {string}")
    public void theBankResultShouldBe(String expected) {
        assertEquals(expected, finalResult);
    }

    // @And("the bank code is {int}")
    // public void theBankCodeIsAgain(int code) {
    //     this.bankCode = code;
    // }

    @When("the bank verifies the card, password, and account")
    public void theBankVerifiesCardPasswordAndAccount() {
        // FR6: if bank code valid, password valid, account active
        String result = bank.verifyAccount(bankCode, accountNumber, password);
        finalResult = result;
    }

    @Then("the bank sends {string} to the ATM as final result")
    public void theBankSendsToTheATMasFinalResult(String expected) {
        assertEquals(expected, finalResult);
    }
}
