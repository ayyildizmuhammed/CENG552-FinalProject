package steps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import atmSrc.ATM;
import atmSrc.Bank;
import atmSrc.Card;
import atmSrc.Message;
import io.cucumber.java.en.*;

public class AtmTransactionSteps {

    private static Bank bank;
    private static ATM atm;
    private Message lastMessage;

    /**
     * Background: 
     *   "Given the Bank is started with "bankdata.json"
     *    And the ATM is started with totalFund "10000", dailyLimit "2000", transactionLimit "500", minCashRequired "500"
     */
    @Given("the Bank is up with {string}")
    public void the_bank_is_up_with(String jsonFile) {
        bank = new Bank(jsonFile);
    }

    @And("the ATM is up with totalFund {string}, dailyLimit {string}, transactionLimit {string}, minCashRequired {string}")
    public void the_atm_is_started_with_params(String t, String d, String m, String n) {
        int totalFund = Integer.parseInt(t);
        int dailyLimit = Integer.parseInt(d);
        int transLimit = Integer.parseInt(m);
        int minCash = Integer.parseInt(n);

        atm = new ATM(totalFund, dailyLimit, transLimit, minCash, bank);
    }

    /**
     * FR10: Insert a valid card, then 4 times wrong password.
     */
    @Given("I insert a valid card")
    public void i_insert_a_valid_card() {
        // Örnek: serial=12345, account=1234, bankCode=1001, expired=false
        Card card = new Card(12345, 1234, 1001, false);
        lastMessage = atm.insertCard(card);
    }

    /**
     * "When I enter wrong password 4 times for account 1234"
     */
    @When("I enter wrong password {int} times for account {int}")
    public void iEnterWrongPasswordTimesForAccount(int times, int accountNum) {
        // We do not use 'accountNum' directly, because atm.verify() just checks session's account.
        for(int i = 0; i < times; i++) {
            lastMessage = atm.verify(9999); // 9999 => wrong pin
        }
    }

    /**
     * "Then the ATM should retain the card"
     */
    @Then("the ATM should retain the card")
    public void theATMShouldRetainTheCard() {
        assertEquals("CARD_RETAINED", lastMessage.getCode());
    }

    /**
     * "And I should see {string} message"
     * This checks the last message code only, or both code + description if you prefer.
     */
    @And("I should see {string} message")
    public void iShouldSeeMessage(String expectedCode) {
        assertEquals(expectedCode, lastMessage.getCode());
    }

    /**
     * FR11–FR16: 
     * "And I enter the correct password "1111" for account "1234""
     */
    @And("I enter the correct password {string} for account {string}")
    public void iEnterTheCorrectPasswordForAccount(String pinStr, String accStr) {
        int pin = Integer.parseInt(pinStr);
        // ATM kodu 'acc' parametresini session'da tutuyor mu? 
        // Bu projede 'session.setAccountNumber(acc)' falan insertCard sırasında. 
        // Sadece pin'i verify ediyoruz:
        lastMessage = atm.verify(pin);
    }

    /**
     * "When I attempt to withdraw {int}"
     */
    @When("I attempt to withdraw {int}")
    public void iAttemptToWithdraw(int amount) {
        lastMessage = atm.withdraw(amount);
    }

    /**
     * "Then I should see "<expectedCode>" message"
     * Bunu yukarıda "I should see {string} message" ile birleştirebilirsiniz.
     * 
     * Aynı step'i paylaşıyoruz, "IShouldSeeMessage(...)". 
     * Ama isterseniz explicitly yazarsanız:
     */
    @Then("I should see {string} message as withdrawal result")
    public void iShouldSeeWithdrawalResult(String expectedCode) {
        System.out.println("Expected code: " + lastMessage.getDescription());
        assertEquals(expectedCode, lastMessage.getCode());
    }

    /**
     * FR17: Transfer
     * "When I transfer 150 to account 9999"
     */
    @When("I transfer {int} to account {int}")
    public void iTransferAmountToAccount(int amount, int toAccount) {
        lastMessage = atm.transfer(toAccount, amount);
    }

    @Then("the transfer should be successful")
    public void theTransferShouldBeSuccessful() {
        assertEquals("TRANSFER_SUCCESS", lastMessage.getCode());
    }

    /**
     * Bazı senaryolarda "And I should see "TRANSFER_SUCCESS" message" 
     * => @And("I should see {string} message") mevcuttur, 
     *    or we can unify in "IShouldSeeMessage(...)"
     */

    /**
     * (Opsiyonel) Ekrana basılan "Transaction done." gibi description doğrulamak isterseniz:
     */
    @Then("the system returns code {string} and description {string}")
    public void theSystemReturnsCodeAndDescription(String expCode, String expDesc) {
        assertEquals(expCode, lastMessage.getCode());
        assertEquals(expDesc, lastMessage.getDescription());
    }
}
