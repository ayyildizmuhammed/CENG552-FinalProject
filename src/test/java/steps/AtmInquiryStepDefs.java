package steps;

import static org.junit.Assert.assertEquals;

import atmSrc.ATM;
import atmSrc.Bank;
import atmSrc.Card;
import atmSrc.Message;
import io.cucumber.java.en.*;

public class AtmInquiryStepDefs {

    private ATM atm;
    private Message lastMessage;

    @Given("the ATM system is running with {string}")
    public void theATMSystemIsRunningWith(String jsonFile) {
        // Assume this initializes the ATM system and loads the JSON data.
        // Placeholder for actual ATM initialization.
        atm = new ATM(5000, 1500, 500, 100, new Bank(jsonFile));
    }

    @Given("the user is authenticated for inquiry")
    public void theUserIsAuthenticated() {
        // Simulate user authentication for the ATM session
        atm.insertCard(new Card(1234, 1234, 1001, false)); // valid card
        Message pinVerify = atm.verify(1111); // correct PIN
        assertEquals("ACCOUNT_OK", pinVerify.getCode());
    }

    @When("the user selects {string} for inquiry")
    public void theUserSelects(String inquiryType) {
        // Trigger the inquiry logic based on the type
        lastMessage = atm.inquiry(inquiryType);
    }

    @Then("the ATM should display the account balance")
    public void theATMShouldDisplayTheAccountBalance() {
        assertEquals("INQUIRY_BALANCE_OK", lastMessage.getCode());
        System.out.println(lastMessage.getDescription()); // Balance: ...
    }

    @Then("the ATM should display the last 10 transactions")
    public void theATMShouldDisplayTheLast10Transactions() {
        assertEquals("INQUIRY_DETAILED_OK", lastMessage.getCode());
        System.out.println(lastMessage.getDescription()); // See last 10 transactions
    }

    @Then("the ATM should display an error message for invalid inquiry type")
    public void theATMShouldDisplayAnErrorMessageForInvalidInquiryType() {
        assertEquals("INVALID_INQUIRY_TYPE", lastMessage.getCode());
        System.out.println(lastMessage.getDescription()); // Unknown inquiry type
    }
}