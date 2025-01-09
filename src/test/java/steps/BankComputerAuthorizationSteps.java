package steps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import atmSrc.Bank;
import atmSrc.Account;
import io.cucumber.java.en.*;

public class BankComputerAuthorizationSteps {

    private Bank bank;             // Bank nesnesi
    private String bankCodeResult; // "valid bank code" veya "bad bank code"
    private String finalResult;    // "account ok", "bad password", vs.

    private int bankCode;          // Testlerde girilen bank code
    private int accountNumber;     // Testlerde girilen account numarası
    private String accountStatus;  // "active", "frozen" vb.
    private String password;       // Kullanıcı PIN/password

    @Given("the bank system is running with {string}")
    public void theBankSystemIsRunningWith(String jsonFile) {
        bank = new Bank(jsonFile);
        assertNotNull("Bank object should not be null", bank);
    }

    // FR1 & FR2
    @Given("the bank code is {int}")
    public void theBankCodeIs(int code) {
        this.bankCode = code;
    }

    @When("the bank verifies only the bank code")
    public void theBankVerifiesOnlyBankCode() {
        boolean valid = bank.getDbProxy().isValidBankCode(bankCode);
        bankCodeResult = valid ? "valid bank code" : "bad bank code";
    }

    @Then("the result should be {string}")
    public void theResultShouldBe(String expected) {
        assertEquals(expected, bankCodeResult);
    }

    // FR3–FR6
    @Given("a bank code {int}")
    public void aBankCode(int code) {
        this.bankCode = code;
    }

    @Given("an account with number {int} has status {string}")
    public void anAccountWithNumberHasStatus(int accNum, String status) {
        // Test verisi: "1234", "active" vs.
        this.accountNumber = accNum;
        this.accountStatus = status;

        // Veritabanındaki account'ı bulup status'ü set edebiliriz (opsiyonel):
        Account acc = bank.getDbProxy().findAccount(accNum);
        if (acc != null) {
            acc.setStatus(status);
        }
    }

    @And("the password {string}")
    public void thePasswordIs(String pwd) {
        this.password = pwd;
    }

    @When("the bank verifies the card with password")
    public void theBankVerifiesCardPassword() {
        // bank.verifyAccount(bankCode, accountNumber, password)
        String result = bank.verifyAccount(bankCode, accountNumber, password);
        finalResult = result; // "bad bank code", "bad password", "bad account", "account ok"
    }

    @Then("the bank result should be {string}")
    public void theBankResultShouldBe(String expected) {
        assertEquals(expected, finalResult);
    }

}
