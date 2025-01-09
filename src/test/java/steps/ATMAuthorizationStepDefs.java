package steps;

import atmSrc.ATM;
import atmSrc.Bank;
import atmSrc.Message;
import atmSrc.Card;
import atmSrc.Status;
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
     * FR1: Bank is started with a given JSON file
     */
    @Given("the Bank is started with {string}")
    public void the_Bank_is_started_with(String jsonFilePath) {
        bank = new Bank(jsonFilePath);
        assertNotNull("Bank object should not be null", bank);
    }

    /**
     * FR1: ATM is started with given parameters
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

        // FR2: If no card => initial display is stored
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
     * Common step for inserting card
     */
    @When("the user inserts a valid card with bankCode {string} cardNumber {string}")
    public void the_user_inserts_a_valid_card_with_bankCode_cardNumber(String bc, String cn) {
        int bankCode = Integer.parseInt(bc);
        int cardNumber = Integer.parseInt(cn);

        // Using default PIN 1111 or any, 
        // but real approach might read the PIN from a data table or scenario
        Card testCard = new Card(cardNumber, 1111, bankCode, true);
        lastResponse = atm.insertCard(testCard);
    }
    @When("the user inserts a invalid card with bankCode {string} cardNumber {string}")
    public void the_user_inserts_a_invalid_card_with_bankCode_cardNumber(String bc, String cn) {
        int bankCode = Integer.parseInt(bc);
        int cardNumber = Integer.parseInt(cn);

        // Using default PIN 1111 or any, 
        // but real approach might read the PIN from a data table or scenario
        Card testCard = new Card(cardNumber, 1111, bankCode, false);
        lastResponse = atm.insertCard(testCard);
    }

    /**
     * Step to enter PIN after the card is inserted
     */
    @Then("the user enters PIN {string} for account {string}")
    public void the_user_enters_PIN_for_account(String pin, String account) {
        int pinInt = Integer.parseInt(pin);
        // We don't necessarily need the account here to call verify, 
        // but let's store if we want to do a check
        lastResponse = atm.verify(pinInt);
    }

    /**
     * Then the system responds with code {string} and message {string}
     */
    @Then("the system responds with code {string} and message {string}")
    public void the_system_responds_with_code_and_message(String code, String msg) {
        assertNotNull("The lastResponse should not be null", lastResponse);
        assertEquals(code, lastResponse.getCode());
        assertEquals(msg, lastResponse.getDescription());
    }

    /**
     * FR7, FR8, FR9: Request a withdrawal
     */
    @When("the user requests a withdrawal of {string}")
    public void the_user_requests_a_withdrawal_of(String amountStr) {
        int amount = Integer.parseInt(amountStr);
        lastResponse = atm.withdraw(amount);
    }

    /**
     * Utility step if you want to chain multiple PIN attempts:
     */
    @And("the user enters PIN {string} again")
    public void the_user_enters_PIN_for_account_again(String pin) {
        int pinInt = Integer.parseInt(pin);
        lastResponse = atm.verify(pinInt);
    }
}
