package atmSrc;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class NetworkToBank {

    private Log log;
    private InetAddress bankAddress;
    private Bank bank;

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

    public Account getAccount(int accountNumber) {
        openConnection();
        Account account = bank.getDbProxy().findAccount(accountNumber);
        closeConnection();
        return account;
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

    public Status sendWithdrawalRequest(Account account, double amount, int dailyLimit) {
        double availableBalance = account.getBalance().getAvailableBalance();
        if (availableBalance < amount) {
            return new Status(false, "insufficient funds");
        }

        boolean pass = bank.getDbProxy().checkAndUpdateDailyLimit(account.getAccountNumber(), amount, dailyLimit);

        if (!pass) {
            closeConnection();
            return new Status(false, "transaction failed: daily limit exceeded");
        }
        // limit aşılmadı => transaction succeeded
        closeConnection();
        return new Status(true, "transaction succeeded");

    }

    public Status sendMoneyDispensedRequest(Account account, double amount) {
        openConnection();
        bank.getDbProxy().minusBalance(account.getAccountNumber(), amount);
        closeConnection();
        return new Status(true, "account updated");
    }

    public Status sendTransferRequest(Account fromAccount, Account toAccount, double amount) {
        openConnection();
        if (amount > fromAccount.getBalance().getAvailableBalance()) {
            closeConnection();
            return new Status(false, "transaction failed: insufficient funds");
        }
        bank.getDbProxy().minusBalance(fromAccount.getAccountNumber(), amount);
        bank.getDbProxy().plusBalance(toAccount.getAccountNumber(), amount);
        closeConnection();
        return new Status(true, "transfer succeeded");
    }

    public Status sendDepositRequest(int accountNum, int amount) {
        openConnection();
        // Bank side => dbProxy.plusBalance(accountNum, amount), e.g.:
        bank.getDbProxy().plusBalance(accountNum, amount);
        closeConnection();
        return new Status(true, "deposit succeeded");
    }

    public Status sendInquiryRequest(int accountNum, String inquiryType) {
        openConnection();
        Account acc = bank.getDbProxy().findAccount(accountNum);
        if (acc == null) {
            closeConnection();
            return new Status(false, "account not found");
        }

        if ("BALANCE".equals(inquiryType)) {
            double bal = acc.getBalance().getAvailableBalance();
            closeConnection();
            // "Status" üzerinde ek alan
            //TODO: setBalance??
            return new Status(true, "OK");
        } else if ("DETAILED".equals(inquiryType)) {
            List<String> last10 = new ArrayList<>();
            last10.add("Withdrawal 300 USD");
            last10.add("Deposit 200 USD");
            // ...
            closeConnection();
            //TODO: setTransactionList??
            return new Status(true, "OK");
        } else {
            closeConnection();
            return new Status(false, "unknown inquiry type");
        }
    }

}
