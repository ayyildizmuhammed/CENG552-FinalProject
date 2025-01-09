package steps;

import static org.junit.Assert.assertEquals;

import atmSrc.ATM;
import atmSrc.Bank;
import atmSrc.Card;
import atmSrc.Message;
import io.cucumber.java.en.*;

public class AtmDepositStepDefs {

    private ATM atm;
    private Message lastMessage;
    private boolean networkFailureSimulated = false;

    @Given("the ATM system is running with {string} for deposit")
    public void theATMSystemIsRunningWith(String jsonFile) {
        atm = new ATM(5000, 1500, 500, 100, new Bank(jsonFile));
    }

    @Given("the user is authenticated for deposit")
    public void theUserIsAuthenticated() {
        atm.insertCard(new Card(1234, 1234, 1001, false)); // valid card
        Message pinVerify = atm.verify(1111); // correct PIN
        assertEquals("ACCOUNT_OK", pinVerify.getCode());
    }

    @When("the user inserts cash amount {int}")
    public void theUserInsertsCashAmount(int amount) {
        lastMessage = atm.deposit(amount);
    }

    @And("the network fails to process the deposit")
    public void theNetworkFailsToProcessTheDeposit() {
        networkFailureSimulated = true;
    }

    @Then("the ATM should confirm the deposit as successful")
    public void theATMShouldConfirmTheDepositAsSuccessful() {
        assertEquals("DEPOSIT_SUCCESS", lastMessage.getCode());
        System.out.println(lastMessage.getDescription()); // Deposit completed. Amount: ...
    }

    @Then("the ATM should display a failure message")
    public void theATMShouldDisplayAFailureMessage() {
        assertEquals("DEPOSIT_FAILED", lastMessage.getCode());
        System.out.println(lastMessage.getDescription()); // Deposit failed: <errorCode>
    }
}