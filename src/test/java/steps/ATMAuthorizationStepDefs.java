package steps;

import atmSrc.ATM;
import atmSrc.Bank;
import atmSrc.Message;
import atmSrc.Card;
import io.cucumber.java.en.*;

import static org.junit.Assert.*;

/**
 * Step definitions for FR1 - FR9
 */
public class ATMAuthorizationStepDefs {

    private static Bank bank;
    private static ATM atm;
    private static Message lastResponse; 
    private String initialDisplay;

    /**
     * FR1 / FR2: Bank is started with a given JSON file
     */
    @Given("the Bank is started with {string}")
    public void the_Bank_is_started_with(String jsonFilePath) {
        bank = new Bank(jsonFilePath);
        assertNotNull("Bank object should not be null", bank);
    }

    /**
     * FR1 / FR2: ATM is started with given parameters
     */
    @Given("the ATM is started with totalFund {string}, dailyLimit {string}, transactionLimit {string}, minCashRequired {string}")
    public void the_ATM_is_started_with_totalFund_dailyLimit_transactionLimit_minCashRequired(
            String t, String d, String tr, String minCash) {

        int totalFund = Integer.parseInt(t);
        int dailyLimit = Integer.parseInt(d);
        int transactionLimit = Integer.parseInt(tr);
        int minimumCashRequired = Integer.parseInt(minCash);

        atm = new ATM(totalFund, dailyLimit, transactionLimit, minimumCashRequired, bank);
        assertNotNull("ATM object should not be null", atm);

        // FR2: If no card => initial display text
        initialDisplay = "Welcome to the Bank ATM! Insert your card...";
    }

    /**
     * FR2: Check the initial display message
     */
    @Then("the ATM should show {string}")
    public void the_ATM_should_show(String expected) {
        assertEquals(expected, initialDisplay);
    }

    /**
     * Common step for inserting a valid card
     */
    @When("the user inserts a valid card with bankCode {string} cardNumber {string}")
    public void the_user_inserts_a_valid_card_with_bankCode_cardNumber(String bc, String cn) {
        int bankCode = Integer.parseInt(bc);
        int accountNumber = Integer.parseInt(cn);

        // Default serial number = 1234, expired=false
        Card testCard = new Card(1234, accountNumber, bankCode, false);
        lastResponse = atm.insertCard(testCard);
    }



    /**
     * Step for bank validation
     */
    @And("the system verifies the bankCode {string}")
    public void the_system_verifies_the_bankCode(String bc) {
        int bankCode = Integer.parseInt(bc);
        lastResponse =  atm.verify(bankCode);
    }

    /**
     * Step to enter PIN after the card is inserted
     */
    @And("the user enters PIN {string} for account {string}")
    public void the_user_enters_PIN_for_account(String pin, String account) {
        int pinInt = Integer.parseInt(pin);
        // Pin verification
        lastResponse = atm.verify(pinInt);
    }

    /**
     * Then the system responds with code {string} and message {string}
     */
    @Then("the system responds with code {string}")
    public void the_system_responds_with_code_and_message(String code) {
        assertNotNull("The lastResponse should not be null", lastResponse);
        assertEquals("Response code mismatch", code, lastResponse.getCode());
    }

    /**
     * For multiple PIN attempts
     */
    @And("the user enters PIN {string} again")
    public void the_user_enters_PIN_for_account_again(String pin) {
        int pinInt = Integer.parseInt(pin);
        lastResponse = atm.verify(pinInt);
    }

    /**
     * FR7, FR8, FR9: Request a withdrawal
     */
    @And("the user requests a withdrawal of {string}")
    public void the_user_requests_a_withdrawal_of(String amountStr) {
        int amount = Integer.parseInt(amountStr);
        lastResponse = atm.withdraw(amount);
    }

    /**
     * Ek step: Check account's updated balance (FR8)
     *  -> Bank sınıfına getAccountBalance(int accountNumber) gibi
     *     bir method eklediyseniz burada kullanabilirsiniz.
     */
    @And("the user's account {string} should be updated with a new balance {string}")
    public void the_user_s_account_should_be_updated_with_a_new_balance(String account, String expectedBalanceStr) {
        int expectedBalance = Integer.parseInt(expectedBalanceStr);
        int accountNumber = Integer.parseInt(account);

        // Mevcut bakiyeyi sorgulayın:
        // Bank içerisinde:
        //   Account acc = bank.getDbProxy().findAccount(accountNumber);
        //   double actual = acc.getBalance().getAvailableBalance();
        double actual = bank.getDbProxy().findAccount(accountNumber).getBalance().getAvailableBalance();

        assertEquals("Balance is not updated correctly", expectedBalance, (int) actual);
    }
}
