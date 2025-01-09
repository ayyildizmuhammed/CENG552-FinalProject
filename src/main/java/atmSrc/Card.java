package atmSrc;

public class Card {
    private int serialNumber;
    private int accountNumber;
    private int bankCode;
    private boolean expired;

    public Card(int serialNumber, int accountNumber, int bankCode, boolean expired) {
        this.serialNumber = serialNumber;
        this.accountNumber = accountNumber;
        this.bankCode = bankCode;
        this.expired = expired;
    }

    public int getSerialNumber() {
        return this.serialNumber;
    }

    public int getAccountNumber() {
        return this.accountNumber;
    }

    public int getBankCode() {
        return bankCode;
    }

    public boolean isExpired() {
        return expired;
    }
}
