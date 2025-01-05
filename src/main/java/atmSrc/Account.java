package atmSrc;

public class Account {
    private int accountNumber;
    private String password;
    private String status;  // örn: "active", "frozen"

    public Account() {
    }

    public Account(int accountNumber, String password, String status) {
        this.accountNumber = accountNumber;
        this.password = password;
        this.status = status;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public String getPassword() {
        return password;
    }

    public String getStatus() {
        return status;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
