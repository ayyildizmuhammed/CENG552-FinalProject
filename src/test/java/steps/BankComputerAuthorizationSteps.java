package steps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import atmSrc.Bank;
import io.cucumber.java.en.*;

public class BankComputerAuthorizationSteps {

    private Bank bank;
    private String bankCodeResult;
    private String finalResult;

    private int bankCode;
    private int accountNumber;
    private String accountStatus;
    private String password;

    @Given("the bank system is running with {string}")
    public void theBankSystemIsRunningWith(String jsonFile) {
        bank = new Bank(jsonFile);
        assertNotNull(bank);
    }

    @Given("the bank code is {int}")
    public void theBankCodeIs(int code) {
        this.bankCode = code;
    }

    @When("the bank verifies only the bank code")
    public void theBankVerifiesOnlyBankCode() {
        boolean valid = bank.getDbProxy().isValidBankCode(bankCode);
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
}
