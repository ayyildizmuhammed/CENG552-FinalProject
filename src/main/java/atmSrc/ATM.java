package atmSrc;

import java.net.InetAddress;
import java.util.List;

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

    public ATM(int totalFund, int dailyLimit, int transactionLimit, int minimumCashRequired, Bank bank) {
        this.totalFund = totalFund;
        this.dailyLimit = dailyLimit;
        this.transactionLimit = transactionLimit;
        this.minimumCashRequired = minimumCashRequired;

        this.log = new Log();
        this.dispenser = new CashDispenser(log, totalFund);
        this.display = new Display();
        this.session = new Session();

        try {
            InetAddress bankAddress = InetAddress.getByName("localhost");
            this.network = new NetworkToBank(bankAddress, bank);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.cardReader = new CardReader();

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

    public Message enterPinWithMenu(int pin) {
        // Simüle: menüyü göster
        int choice = display.showPinMenu();
        if (choice == 2) {
            display.display("You chose CORRECTION. Please re-enter your PIN.");
            // Tekrar PIN almak vb.
            return new Message("CORRECTION", "User wants to correct the PIN input.");
        } else if (choice == 3) {
            // Kartı iade et ve session sonlandır
            cardReader.ejectCard();
            this.cardInserted = false;
            return new Message("TAKE_CARD", "Card ejected, transaction canceled.");
        } else {
            // Normal confirm flow => verify
            return verify(pin);
        }
    }

    public Message changePassword(int oldPin, int newPin) {
        if (!session.isAuthorized()) {
            return new Message("NOT_AUTHORIZED", "Please log in first.");
        }
        // Adım 1: Eski PIN kontrolü
        Status st = this.network.sendAuthorizationRequest(
                session.getBankCode(),
                session.getAccountNumber(),
                String.valueOf(oldPin));
        if (!st.isSuccess()) {
            return new Message("BAD_OLD_PASSWORD", "Old password is incorrect.");
        }
        // Adım 2: Bankaya "change password" isteği
        Status changeSt = network.sendChangePasswordRequest(
                session.getAccountNumber(),
                String.valueOf(newPin));
        if (!changeSt.isSuccess()) {
            return new Message("PASSWORD_CHANGE_FAILED", changeSt.getErrorCode());
        }
        return new Message("PASSWORD_CHANGED", "Your password has been changed successfully.");
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
            return new Message("ATM_OUT_OF_CASH", "ATM does not have enough funds, returning your card.");
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
        int serial = card.getSerialNumber();
        int bankCode = card.getBankCode();
        int accountNumber = card.getAccountNumber();

        // FR5: read serial, FR6: log
        session.setCardNumber(serial);
        session.setBankCode(bankCode);
        session.setAccountNumber(accountNumber);
        log.logSerialNumber(serial);

        // Zaman kaydı (Performance Requirements 2 - 2 dakika response vs.)
        this.requestTimestamp = System.currentTimeMillis();

        // Kart kabul edildi, PIN girişi bekleniyor
        return new Message("CARD_ACCEPTED", "Card accepted, please enter your PIN.");
    }

    public Message verify(int enteredPin) {
        if (!this.cardInserted) {
            return new Message("NO_CARD", "No card inserted.");
        }

        long now = System.currentTimeMillis();
        if ((now - this.requestTimestamp) > 2 * 60 * 1000) {
            cardReader.ejectCard();
            this.cardInserted = false;
            return new Message("TIMEOUT", "No response for 120 seconds, card ejected.");
        }

        // Bank code from session
        int bankCode = session.getBankCode();
        int accountNum = session.getAccountNumber();

        // NetworkToBank => Bank
        Status st = this.network.sendAuthorizationRequest(bankCode, accountNum, String.valueOf(enteredPin));
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

    public Message withdraw(int amount) {
        int accountNumber = this.session.getAccountNumber();
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
            return new Message("EXCEED_DAILY_LIMIT", "Requested amount exceeds ATM daily limit.");
        }
        Message request = new Message("WITHDRAW_REQUEST", "Attempting to withdraw " + amount);
        log.logSend(request);
        Status st = network.sendWithdrawalRequest(accountNumber, amount, this.dailyLimit);

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
        printReceipt("withdraw", amount);

        // FR8 => After money is dispensed, we must update the account in the bank
        // 2) “MONEY_DISPENSED” -> “applyWithdrawal”
        Message dispMsg = new Message("MONEY_DISPENSED", "ATM dispensed " + amount);
        Status dispStatus = network.sendMoneyDispensedRequest(accountNumber, amount);
        if (!dispStatus.isSuccess()) {
            displayErrorLong("Account update failed: " + dispStatus.getErrorCode());
            cardReader.ejectCard();
            this.cardInserted = false;
            return new Message("ACCOUNT_UPDATE_FAILED", dispStatus.getErrorCode());
        }

        log.logSend(dispMsg);

        // if dispStatus success => account updated
        // ATM local fund update
        this.totalFund -= amount;

        return new Message("TRANSACTION_SUCCESS", "Withdrawal done.");
    }

    /**
     * FR17: Transfer
     */
    public Message transfer(int toAccountNumber, int amount) {
        if (!session.isAuthorized()) {
            return new Message("NOT_AUTHORIZED", "You must log in first.");
        }

        int fromAccountNumber = this.session.getAccountNumber();
        // Banka üzerinden transfer isteği
        Message request = new Message("TRANSFER_REQUEST",
                "Transfer from: " + session.getAccountNumber() + " to " + toAccountNumber
                        + " amount " + amount);

        Status st = network.sendTransferRequest(fromAccountNumber, toAccountNumber, amount);
        log.logSend(request);

        if (!st.isSuccess()) {
            displayErrorLong("Transfer failed: " + st.getErrorCode());
            return new Message("TRANSFER_FAILED", st.getErrorCode());
        }

        return new Message("TRANSFER_SUCCESS",
                "Transfer completed successfully.");
    }

    public Message deposit(int amount) {
        // 1) Check authorization
        if (!session.isAuthorized()) {
            return new Message("NOT_AUTHORIZED", "Please log in first.");
        }

        display.display("Cover is opened. Please insert your cash...");
        // 2) Simüle: “open deposit slot” – deposit physically
        display.display("Please insert your cash...");

        // 3) Bank’a deposit isteği
        int accountNum = session.getAccountNumber();
        Status st = network.sendDepositRequest(accountNum, amount);
        if (!st.isSuccess()) {
            // deposit failed
            display.display("Deposit failed: " + st.getErrorCode());
            return new Message("DEPOSIT_FAILED", st.getErrorCode());
        }

        // 4) ATM tarafında “putCash()” ile makineye fiziksel nakit ekleyebilirsiniz
        dispenser.putCash(amount);
        // Log deposit
        log.logDeposit(new Money(amount, "USD"), accountNum);

        // 5) Return success message
        return new Message("DEPOSIT_SUCCESS", "Deposit completed. Amount: " + amount);
    }

    public Message inquiry(String inquiryType) {
        // 1) Check authorization
        if (!session.isAuthorized()) {
            return new Message("NOT_AUTHORIZED", "Please log in first.");
        }

        int accountNum = session.getAccountNumber();
        if ("balance".equalsIgnoreCase(inquiryType)) {
            // 2a) Bank'a "INQUIRY_BALANCE" isteği
            List<Object> result = network.sendInquiryRequest(accountNum, "BALANCE");
            Status st = (Status) result.get(0);
            if (st.isSuccess()) {
                // Devamında ATM ekranda gösterebilir
                double bal = (double) result.get(1);
                display.display("Your available balance is: " + bal);
                return new Message("INQUIRY_BALANCE_OK", "Balance: " + bal);
            } else {
                return new Message("INQUIRY_FAILED", st.getErrorCode());
            }
        } else if ("detailed".equalsIgnoreCase(inquiryType)) {
            List<Object> result = network.sendInquiryRequest(accountNum, "DETAILED");
            Status st = (Status) result.get(0);
            if (st.isSuccess()) {
                // st içinde "transactionList" vb. var varsayalım
                List<String> transactionList = (List<String>) result.get(1);
                display.display("Recent transactions: ");
                for (String tr : transactionList) {
                    display.display(tr);
                }
                return new Message("INQUIRY_DETAILED_OK", "See last 10 transactions");
            } else {
                return new Message("INQUIRY_FAILED", st.getErrorCode());
            }
        } else {
            return new Message("INVALID_INQUIRY_TYPE", "Unknown inquiry type: " + inquiryType);
        }
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
        // try {
        //     Thread.sleep(30_000); // 30 sn bekleme
        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        // }
    }

    private void printReceipt(String transactionType, int amount) {
        // Construct a Receipt object
        Receipt receipt = new Receipt();
        receipt.setTransactionType(transactionType);
        receipt.setAmount(amount);
        receipt.setCardSerial(session.getCardNumber()); // FR15: link to card serial

        // If needed, set more fields: date/time, account number, etc.

        // Now, pass it to ReceiptPrinter
        // If we want a real receipt printer, we must handle the
        // 'UnsupportedOperationException' inside:
        try {
            ReceiptPrinter printer = new ReceiptPrinter();
            printer.printReceipt(receipt);
        } catch (UnsupportedOperationException e) {
            System.out.println("Receipt printer not implemented yet: " + e.getMessage());
        }
    }

    public Log getLog() {
        return log;
    }

    public int getTotalFund() {
        return totalFund;
    }

    public int getDailyLimit() {
        return dailyLimit;
    }

    public int getTransactionLimit() {
        return transactionLimit;
    }

    public int getMinimumCashRequired() {
        return minimumCashRequired;
    }
}
