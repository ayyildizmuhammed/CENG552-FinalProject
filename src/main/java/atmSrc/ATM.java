package atmSrc;

public class ATM {

    // FR1 için
    private int totalFund; // t
    private int dailyLimit; // k
    private int transactionLimit; // m
    private int minimumCashRequired; // n

    // Demo amaçlı bileşenler
    private Log log;
    private NetworkToBank network;
    private CashDispenser dispenser;
    private CardReader cardReader;
    private Display display;
    private Session session;

    private boolean cardInserted = false;
    private long requestTimestamp;

    public ATM(int t, int k, int m, int n, Bank bank) {
        this.totalFund = t;
        this.dailyLimit = k;
        this.transactionLimit = m;
        this.minimumCashRequired = n;

        this.log = new Log();
        this.dispenser = new CashDispenser(log);
        this.display = new Display();
        this.session = new Session();

        // Bank parametresi dışarıdan geliyor,
        // NetworkToBank’e de veriyoruz:
        try {
            this.network = new NetworkToBank(log, null, bank);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.cardReader = new CardReader(this);

        // FR2: ilk mesaj
        showInitialDisplay();
    }

    /**
     * FR2: “If no card is in the ATM, the system should display initial display.”
     */
    public void showInitialDisplay() {
        if (!cardInserted) {
            display.display("Welcome to the Bank ATM! Insert your card...");
        }
    }

    /**
     * FR3: ATM para bitmişse, kart kabul etmemeli
     * FR4: Kart geçerli mi (okunabiliyor mu, expired mı)?
     * FR5: Seri numara ve bank code okunuyor
     * FR6: Seri numara loglanıyor
     */
    public Message insertCard(Card card) {
        // FR3: Yeterli fon var mı?
        if (this.totalFund < this.minimumCashRequired) {
            // Kart hemen iade edilsin
            return new Message("ATM_OUT_OF_CASH",
                    "ATM does not have enough funds, returning your card.");
        }

        // Kartı cihaz aldı
        this.cardInserted = true;
        display.display("Card inserted. Checking validity...");

        // FR4: Kart geçerli mi?
        boolean valid = cardReader.checkCardValidity(card);
        if (!valid) {
            cardReader.ejectCard();
            this.cardInserted = false;
            return new Message("INVALID_CARD", "Your card is invalid or expired. Ejecting...");
        }

        // Bank code & serial
        int serial = card.getNumber();
        int bankCode = card.getBankCode();

        // FR5: read serial, FR6: log
        session.setCardNumber(serial);
        session.setBankCode(bankCode);
        log.logSerialNumber(serial);

        // Zaman kaydı (Performance Requirements 2 - 2 dakika response vs.)
        this.requestTimestamp = System.currentTimeMillis();

        // Kart kabul edildi, PIN girişi bekleniyor
        return new Message("CARD_ACCEPTED", "Card accepted, please enter your PIN.");
    }

    public Message verify(int enteredPin, int accountNum) {
        if (!this.cardInserted) {
            return new Message("NO_CARD", "No card inserted.");
        }

        // 2 dk time-out (Performance Requirement 2)
        long now = System.currentTimeMillis();
        if ((now - this.requestTimestamp) > 2 * 60 * 1000) {
            cardReader.ejectCard();
            this.cardInserted = false;
            return new Message("TIMEOUT", "No response for 2 min, card ejected.");
        }

        // Bank code from session
        int bankCode = session.getBankCode();

        // NetworkToBank => Bank
        Status st = network.sendAuthorizationRequest(bankCode, accountNum, String.valueOf(enteredPin));
        if (!st.isSuccess()) {
            // Gelen hata: "bad bank code", "bad password", "bad account"
            String err = st.getErrorCode();
            switch (err) {
                case "bad bank code":
                    // FR2 bank side => ATM "bad bank code"
                    cardReader.ejectCard();
                    this.cardInserted = false;
                    return new Message("BAD_BANK_CODE", "Unsupported bank code. Card ejected.");
                case "bad password":
                    // FR4 bank side => ATM "bad password"
                    // FR10: 3 kez yanlış => kart reten
                    session.incrementWrongAttempt();
                    if (session.getWrongPasswordAttempts() >= 4) {
                        cardReader.retainCard();
                        this.cardInserted = false;
                        return new Message("CARD_RETAINED", "Too many wrong attempts. Card is retained.");
                    }
                    return new Message("BAD_PASSWORD", "Wrong password. Try again.");
                case "bad account":
                    // FR5 bank side => ATM "bad account"
                    cardReader.ejectCard();
                    this.cardInserted = false;
                    return new Message("BAD_ACCOUNT", "There is a problem with this account. Card ejected.");
                default:
                    cardReader.ejectCard();
                    this.cardInserted = false;
                    return new Message("AUTH_FAILED", "Unknown auth error, card ejected.");
            }
        } else {
            // "account ok" => FR6 bank side => ATM -> auth success
            session.setAuthorized(true);
            session.setAccountNumber(accountNum);
            return new Message("ACCOUNT_OK", "Authorization success, you may proceed.");
        }
    }

    /**
     * FR11–FR16: Withdraw akışı
     */
    public Message withdraw(int amount) {
        if (!session.isAuthorized()) {
            return new Message("NOT_AUTHORIZED", "Please log in first.");
        }

        // FR12: transaction limit (ATM side)
        if (amount > this.transactionLimit) {
            return new Message("EXCEED_TRANSACTION_LIMIT", "Requested amount exceeds the transaction limit.");
        }

        // FR9 bank side => dailyLimit.
        // ATM’de kısaca local check de yapabiliriz:
        if (amount > this.dailyLimit) {
            return new Message("EXCEED_DAILY_LIMIT", "Requested amount exceeds your daily limit.");
        }

        // 1) FR7 => “WITHDRAW_REQUEST” -> Bank checks daily usage
        Message request = new Message("WITHDRAW_REQUEST", "Attempting to withdraw " + amount);

        // Hack: "balances.availableBalance" = amount, "balances.totalBalance" =
        // accountNum
        // Bu, bank’ın sendMessage içinde parse edebilmesi için
        Balances param = new Balances(amount, session.getAccountNumber());
        log.logSend(request);
        Status st = network.sendMessage(request, param);

        if (!st.isSuccess()) {
            // Bank "transaction failed"
            displayErrorLong("Transaction not successful: " + st.getErrorCode());
            cardReader.ejectCard();
            this.cardInserted = false;
            return new Message("TRANSACTION_FAILED", st.getErrorCode());
        }

        // Bank => "transaction succeeded"
        // FR14 => ATM dispenses money
        Money withdrawMoney = new Money(amount, "USD");
        if (!dispenser.checkCashOnHand(withdrawMoney)) {
            cardReader.ejectCard();
            this.cardInserted = false;
            return new Message("ATM_NOT_ENOUGH_CASH", "ATM cannot dispense that amount.");
        }
        dispenser.dispenseCash(withdrawMoney);
        log.logCashDispensed(withdrawMoney);

        // FR8 => After money is dispensed, we must update the account in the bank
        // 2) “MONEY_DISPENSED” -> “applyWithdrawal”
        Message dispMsg = new Message("MONEY_DISPENSED", "ATM dispensed " + amount);
        Balances dispParam = new Balances(amount, session.getAccountNumber());
        Status dispStatus = network.sendMessage(dispMsg, dispParam);
        log.logSend(dispMsg);

        // if dispStatus success => account updated
        // ATM local fund update
        this.totalFund -= amount;

        // FR14 => "After the Customer has taken the card the money is dispensed."
        cardReader.ejectCard();
        this.cardInserted = false;

        return new Message("TRANSACTION_SUCCESS", "Withdrawal done. Card ejected.");
    }

    /**
     * FR17: Transfer
     */
    public Message transfer(int toAccount, int amount) {
        if (!session.isAuthorized()) {
            return new Message("NOT_AUTHORIZED", "You must log in first.");
        }
        // Banka üzerinden transfer isteği
        Message request = new Message("TRANSFER_REQUEST",
                "Transfer from: " + session.getAccountNumber() + " to " + toAccount
                        + " amount " + amount);
        Balances balances = new Balances(0, 0);
        Status st = network.sendMessage(request, balances);
        log.logSend(request);

        if (!st.isSuccess()) {
            displayErrorLong("Transfer failed: " + st.getErrorCode());
            cardReader.ejectCard();
            this.cardInserted = false;
            return new Message("TRANSFER_FAILED", st.getErrorCode());
        }

        // Success
        cardReader.ejectCard();
        this.cardInserted = false;
        return new Message("TRANSFER_SUCCESS",
                "Transfer completed successfully.");
    }

    /**
     * FR3: checkAvailabilityOfCashInATM
     */
    public Message checkAvailabilityOfCashInATM() {
        if (this.totalFund < this.minimumCashRequired) {
            return new Message("ATM_OUT_OF_CASH",
                    "Insufficient ATM funds, transaction not allowed.");
        }
        return new Message("ATM_HAS_CASH",
                "ATM has enough cash to process transactions.");
    }

    /**
     * FR2 Performance Requirement 1: "Error message should be displayed at least 30
     * sec."
     * Bunu gerçekte 'Thread.sleep(30000)' vs. ile yapabilirdik.
     * Burada sadece simüle ediyoruz.
     */
    private void displayErrorLong(String message) {
        display.display("ERROR: " + message);
        try {
            Thread.sleep(30_000); // 30 sn bekleme
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Log getLog() {
        return log;
    }

    /**
     * FR2 Performance Requirement 2: "If no response from bank in 2 minutes =>
     * reject card"
     * Yukarıda `requestTimestamp` ile basit bir check yaptık.
     * Gelişmiş senaryoda Thread veya Timer ile de yapılabilir.
     */

    // Diğer metotlar: readAccountNum, checkTime vs.
}
