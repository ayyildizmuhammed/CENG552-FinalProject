package steps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import atmSrc.ATM;
import atmSrc.Bank;
import atmSrc.Card;
import atmSrc.Message;
import io.cucumber.java.en.*;

public class ATMStepDefinitions {

    private ATM atm;
    private Card card;
    private Message lastMessage;

    // Sadece demo amaçlı, normalde buradan bank oluşturabilirsiniz
    private Bank bank;

    @Given("the ATM is initialized with totalFund: {int} dailyLimit: {int} transactionLimit: {int} minimumCashRequired: {int} bankData: {string}")
    public void theATMIsInitialized(int totalFund, int dailyLimit, int transactionLimit, int minimumCashRequired,
            String bankData) {
        // Bank veri dosyasını vermek isteyebilirsiniz -> bankdata.json
        bank = new Bank(bankData);
        // FR1: t=10000, k=2000, m=500, n=500
        atm = new ATM(totalFund, dailyLimit, transactionLimit, minimumCashRequired, bank);
    }

    @Given("I insert a valid card")
    public void iInsertAValidCard() {
        // Örnek card: number=12345, expired=false, bankCode=1001
        card = new Card(12345, false, 1001);
        lastMessage = atm.insertCard(card);
    }

    @Given("I insert a second valid card number {int} with bank code {int}")
    public void iInsertASecondValidCard(int cardNumber, int bankCode) {
        // Örnek: secondCard = new Card(99999, false, 1001)
        card = new Card(cardNumber, false, bankCode);
        lastMessage = atm.insertCard(card);
    }

    @When("I enter wrong password {int} times for account {int}")
    public void iEnterWrongPasswordTimes(int times, int accountNum) {
        // 4 kez deneyelim
        for (int i = 0; i < times; i++) {
            lastMessage = atm.verify(9999, accountNum); // 9999 => wrong pin
        }
    }

    @When("I enter the correct password {int} for account {int}")
    public void iEnterTheCorrectPassword(int password, int accountNum) {
        // DBProxy’de 1234 accountNum varsa PIN=1111 demiştiniz
        lastMessage = atm.verify(password, accountNum);
    }

    @When("I attempt to withdraw {int}")
    public void iAttemptToWithdraw(int amount) {
        lastMessage = atm.withdraw(amount);
    }

    @Then("the ATM should store the initial parameters {int} {int} {int} {int} {string}")
    public void theATMShouldStoreTheInitialParameters(int totalFund, int dailyLimt, int transactionLimit,
            int minimumCashRequired, String bankData) {
        // FR10: "CARD_RETAINED"
        assertEquals(totalFund, atm.getTotalFund());
        assertEquals(dailyLimt, atm.getDailyLimit());
        assertEquals(transactionLimit, atm.getTransactionLimit());
        assertEquals(minimumCashRequired, atm.getMinimumCashRequired());

    }

    @Then("the ATM should retain the card")
    public void theATMShouldRetainTheCard() {
        // FR10: "CARD_RETAINED"
        assertEquals("CARD_RETAINED", lastMessage.getCode());
    }

    @Then("I should see {string} message")
    public void iShouldSeeMessage(String expectedCode) {
        assertEquals(expectedCode, lastMessage.getCode());
    }

    @Then("the ATM should display error and eject card")
    public void theATMShouldDisplayErrorAndEjectCard() {
        // Örneğin "TRANSACTION_FAILED" veya "EXCEED_DAILY_LIMIT" vb.
        // Sizin senaryoya göre ayarlanabilir
        assertTrue("TRANSACTION_FAILED".equals(lastMessage.getCode())
                || "EXCEED_DAILY_LIMIT".equals(lastMessage.getCode())
                || "EXCEED_TRANSACTION_LIMIT".equals(lastMessage.getCode())
                || "BAD_ACCOUNT".equals(lastMessage.getCode()));

    }

    @When("I transfer {int} to account {int}")
    public void iTransferToAccount(int amount, int toAccount) {
        lastMessage = atm.transfer(toAccount, amount);
    }

    @Then("the transfer should be successful")
    public void theTransferShouldBeSuccessful() {
        assertEquals("TRANSFER_SUCCESS", lastMessage.getCode());
    }

    @Given("Initial withdrawal sequence successful")
    public void initialWithdrawalSequenceSuccessful() {
        // Örnek: 1000 TL çekildi

        lastMessage = atm.withdraw(1000);

        assertEquals("WITHDRAW_SUCCESS", lastMessage.getCode());
    }

}