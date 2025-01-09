package atmSrc;

import java.util.List;

public class Account {
    private int accountNumber;
    private String password;
    private String status; // örn: "active", "frozen"
    private List<String> transactionHistory; //
    private int invalidPasswordCount = 0;
    private double dailyUsed = 0;
    private Balance balance;

    public Account(int accountNumber, String password, String status, Balance balance) {
        this.accountNumber = accountNumber;
        this.password = password;
        this.status = status;
        this.balance = balance;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getInvalidPasswordCount() {
        return invalidPasswordCount;
    }

    public void incrementInvalidPasswordCount() {
        invalidPasswordCount++;
    }

    public void resetInvalidPasswordCount() {
        invalidPasswordCount = 0;
    }

    public double getDailyUsed() {
        return dailyUsed;
    }

    public void setDailyUsed(double dailyUsed) {
        this.dailyUsed = dailyUsed;
    }

    public Balance getBalance() {
        return balance;
    }

    public void setBalance(Balance balance) {
        this.balance = balance;
    }

    public List<String> getTransactionHistory() {
        return transactionHistory;
    }

    public void setTransactionHistory(List<String> transactionHistory) {
        this.transactionHistory = transactionHistory;
    }
}
