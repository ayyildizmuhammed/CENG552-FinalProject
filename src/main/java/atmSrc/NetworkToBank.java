package atmSrc;
import java.net.InetAddress;

public class NetworkToBank {

    private Log log;
    private InetAddress bankAddress;
    private Bank bank; // JSON verileri vs.
    // FR10: Bank security => Yalnızca bank bu kodu koruyor, 
    // ATM tarafı bu güvenliğe müdahale edemez.

    public NetworkToBank(Log log, InetAddress bankAddress, Bank bank) {
        this.log = log;
        this.bankAddress = bankAddress;
        this.bank = bank;
    }

    public void openConnection() {
        System.out.println("[NetworkToBank] Connection opened.");
    }

    public void closeConnection() {
        System.out.println("[NetworkToBank] Connection closed.");
    }

    /**
     * Authorization request (değişmeden kalabilir)
     */
    public Status sendAuthorizationRequest(int bankCode, int accountNumber, String password) {
        openConnection();
        String result = bank.verifyAccount(bankCode, accountNumber, password);
        closeConnection();

        switch (result) {
            case "bad bank code":
                return new Status(false, "bad bank code");
            case "bad password":
                return new Status(false, "bad password");
            case "bad account":
                return new Status(false, "bad account");
            case "account ok":
                return new Status(true, "account ok");
            default:
                return new Status(false, "unknown error");
        }
    }

    /**
     * FR7-FR8-FR9: 
     * "WITHDRAW_REQUEST", "MONEY_DISPENSED", "TRANSFER_REQUEST" vb.
     */
    public Status sendMessage(Message message, Balances balances) {
        openConnection();

        String code = message.getCode();
        String desc = message.getDescription();

        // Örnek "Attempting to withdraw 300" 
        // Bu string içinden isterseniz amount ve accountNum parse edebilirsiniz.
        // Biz basitçe session’ı atm içinde sakladık, oradan accountNum alacağız.
        
        // 1) WITHDRAW_REQUEST => FR7 => check daily limit (FR9)
        if ("WITHDRAW_REQUEST".equals(code)) {
            // Örneğin description = "Attempting to withdraw 300"
            // ATM tarafı: 
            //   - dailyLimit i? 
            //   - accountNum i? (session’dan?)
            // Aslında Bank'ı bu veriye ulaştırmalıyız.
            // 
            // Daha pratik: ATM, "WITHDRAW_REQUEST" anında 
            // NetworkToBank'a "accountNum" ve "amount" parametresini de yollamalı.
            // Kolaylık için "balances" objesine "amount" koyabiliriz.
            double amount = balances.getAvailableBalance(); // diyelim ki amount = availableBalance
            int accountNum = (int)balances.getTotalBalance(); // hack: totalBalance alanını accountNum olarak kullanalım

            // Artık bankData => DBProxy => dailyLimit check
            double dailyLimit = 2000; // ATM / Bank'tan sabit veya parametre
            boolean pass = bank.getDbProxy().checkAndUpdateDailyLimit(accountNum, amount, dailyLimit);
            if (!pass) {
                closeConnection();
                return new Status(false, "transaction failed: daily limit exceeded");
            }
            // limit aşılmadı => transaction succeeded
            closeConnection();
            return new Status(true, "transaction succeeded");
        }
        // 2) MONEY_DISPENSED => FR8 => update account after money is dispensed
        else if ("MONEY_DISPENSED".equals(code)) {
            // Yine "balances" veya "desc" yardımıyla accountNum & amount al
            double amount = balances.getAvailableBalance();
            int accountNum = (int)balances.getTotalBalance();

            // Artık gerçekten bakiyeden düş
            bank.getDbProxy().applyWithdrawal(accountNum, amount);

            closeConnection();
            return new Status(true, "account updated");
        }
        // 3) TRANSFER_REQUEST
        else if ("TRANSFER_REQUEST".equals(code)) {
            // ...
            closeConnection();
            return new Status(true, null);
        } 
        else {
            closeConnection();
            return new Status(false, "Unknown request code");
        }
    }
}
