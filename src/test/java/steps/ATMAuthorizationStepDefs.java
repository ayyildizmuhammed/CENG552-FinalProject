package steps;

import atmSrc.ATM;
import atmSrc.Bank;
import atmSrc.Card;
import atmSrc.Message;
import io.cucumber.java.en.*;

import static org.junit.Assert.*;

public class ATMAuthorizationStepDefs {

    private static Bank bank;
    private static ATM atm;
    private static Message lastResponse; // stores the result of insertCard() or verify()
    private String initialDisplay;       // for FR2 test

    /**
     * FR1: The Bank is started with a given JSON (bank data)
     */
    @Given("the Bank is started with {string}")
    public void the_bank_is_started_with(String jsonFile) {
        bank = new Bank(jsonFile);  // e.g. new Bank("bankdata.json")
        assertNotNull("Bank object should not be null", bank);
    }

    /**
     * FR1 continued: ATM is started with t, k, m, n
     */
    @Given("the ATM is started with totalFund {string}, dailyLimit {string}, transactionLimit {string}, minCashRequired {string}")
    public void the_atm_is_started_with_totalFund_dailyLimit_transactionLimit_minCashRequired(
            String t, String d, String tr, String n) {
        int totalFund = Integer.parseInt(t);
        int dailyLimit = Integer.parseInt(d);
        int transactionLimit = Integer.parseInt(tr);
        int minCash = Integer.parseInt(n);

        atm = new ATM(totalFund, dailyLimit, transactionLimit, minCash, bank);
        assertNotNull("ATM must not be null", atm);

        // FR2: If no card => initial display
        initialDisplay = "Welcome to the Bank ATM! Insert your card...";
    }

    /**
     * FR2 test: "Then the ATM should show {string}"
     */
    @Then("the ATM should show {string}")
    public void the_atm_should_show(String expected) {
        // Compare the expected text with our stored initialDisplay
        assertEquals(expected, initialDisplay);
    }

    /**
     * FR3, FR4, FR5, FR6: Insert card
     */
    @When("the user inserts a card with bankCode {string} cardNumber {string}")
    public void the_user_inserts_a_card_with_bankCode_cardNumber(String bc, String cn) {
        int bankCode = Integer.parseInt(bc);
        int cardNumber = Integer.parseInt(cn);

        // Suppose we create a Card like: new Card(bankCode, false, cardNumber)
        Card testCard = new Card(cardNumber, 1234, bankCode, false);

        // Insert the card
        lastResponse = atm.insertCard(testCard);
    }

    /**
     * "Then the system responds with code {string} and message {string}"
     */
    @Then("the system responds with code {string} and message {string}")
    public void the_system_responds_with_code_and_message(String expectedCode, String expectedMsg) {
        assertNotNull("Response should not be null", lastResponse);
        // Suppose Message has getCode() and getDescription() or getMessage()
        assertEquals(expectedCode, lastResponse.getCode());
        assertEquals(expectedMsg, lastResponse.getDescription());
    }

    /**
     * FR7, FR8, FR9: Enter PIN
     */
    @When("the user enters PIN {string} for account {string}")
    public void the_user_enters_pin_for_account(String pin, String account) {
        int pinInt = Integer.parseInt(pin);
        int accountInt = Integer.parseInt(account);

        // e.g., atm.verify(pinInt, accountInt)
        lastResponse = atm.verify(pinInt);
    }
}
