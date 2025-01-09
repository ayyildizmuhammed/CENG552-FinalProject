package atmSrc;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class NetworkToBank {

    private InetAddress bankAddress;
    private Bank bank;

    public NetworkToBank(InetAddress bankAddress, Bank bank) {
        this.bankAddress = bankAddress;
        this.bank = bank;
    }

    public void openConnection() {
        System.out.println("[NetworkToBank] Connection opened Address: " + bankAddress);
    }

    public void closeConnection() {
        this.bank.persistChanges();
        System.out.println("[NetworkToBank] Connection closed Address: " + bankAddress);
    }

    /**
     * Authorization request (değişmeden kalabilir)
     */
    public Status sendAuthorizationRequest(int bankCode, int accountNumber, String password) {
        openConnection();
        String result = bank.verifyAccount(bankCode, accountNumber, password);
        System.out.println("[NetworkToBank] Authorization result: " + result +  " for account: " + accountNumber);
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

    public Status sendWithdrawalRequest(int accountNumber, double amount, int dailyLimit) {
        Account account = bank.getDbProxy().findAccount(accountNumber);
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

    public Status sendMoneyDispensedRequest(int accountNumber, double amount) {
        openConnection();
        Account account = bank.getDbProxy().findAccount(accountNumber);
        bank.getDbProxy().applyWithdrawal(account.getAccountNumber(), amount);
        List<String> transactionHistory = account.getTransactionHistory(); 
        transactionHistory.add("Withdrawal: " + amount);
        account.setTransactionHistory(transactionHistory);
        closeConnection();
        return new Status(true, "account updated");
    }

    public Status sendTransferRequest(int fromAccountNumber, int toAccountNumber, double amount) {
        openConnection();
        Account fromAccount = bank.getDbProxy().findAccount(fromAccountNumber);
        Account toAccount = bank.getDbProxy().findAccount(toAccountNumber);
        if(fromAccount == null || toAccount == null) {
            closeConnection();
            return new Status(false, "transaction failed: account not found");
        }
        if (amount > fromAccount.getBalance().getAvailableBalance()) {
            closeConnection();
            return new Status(false, "transaction failed: insufficient funds");
        }
        bank.getDbProxy().minusBalance(fromAccount.getAccountNumber(), amount);
        bank.getDbProxy().plusBalance(toAccount.getAccountNumber(), amount);
        List<String> fromTransactionHistory = fromAccount.getTransactionHistory();
        fromTransactionHistory.add("Transfer to " + toAccount.getAccountNumber() + ": " + amount);
        fromAccount.setTransactionHistory(fromTransactionHistory);
        List<String> toTransactionHistory = toAccount.getTransactionHistory();
        toTransactionHistory.add("Transfer from " + fromAccount.getAccountNumber() + ": " + amount);
        toAccount.setTransactionHistory(toTransactionHistory);
        closeConnection();
        return new Status(true, "transfer succeeded");
    }

    public Status sendDepositRequest(int accountNum, int amount) {
        openConnection();
        // Bank side => dbProxy.plusBalance(accountNum, amount), e.g.:
        bank.getDbProxy().plusBalance(accountNum, amount);
        Account account = bank.getDbProxy().findAccount(accountNum);
        List<String> transactionHistory = account.getTransactionHistory();
        transactionHistory.add("Deposit: " + amount);
        account.setTransactionHistory(transactionHistory);
        closeConnection();
        return new Status(true, "deposit succeeded");
    }

    // return status and List<String> for transaction list
    public List<Object> sendInquiryRequest(int accountNum, String inquiryType) {
        openConnection();
        List<Object> result = new ArrayList<>();
        Account acc = bank.getDbProxy().findAccount(accountNum);
        if (acc == null) {
            closeConnection();
            result.add(new Status(false, "account not found"));
            return result;
        }

        if ("BALANCE".equals(inquiryType)) {
            double bal = acc.getBalance().getAvailableBalance();
            closeConnection();
            // "Status" üzerinde ek alan
            result.add(new Status(true, "OK"));
            result.add(bal);
            return result;
        } else if ("DETAILED".equals(inquiryType)) {
            List<String> transactionList = acc.getTransactionHistory();
            closeConnection();
            // get latest 10 transactions
            List<String> last10 = transactionList.subList(Math.max(transactionList.size() - 10, 0),
                    transactionList.size());
            result.add(new Status(true, "OK"));
            result.add(last10);
            return result;
        } else {
            closeConnection();
            result.add(new Status(false, "unknown inquiry type"));
            return result;
        }
    }

    public Status sendChangePasswordRequest(int accountNumber, String newPin) {
        openConnection();
        boolean success = bank.getDbProxy().updatePassword(accountNumber, newPin);
        closeConnection();
        if (success) {
            return new Status(true, "password updated");
        } else {
            return new Status(false, "cannot update password");
        }
    }

}
