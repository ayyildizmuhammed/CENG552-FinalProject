package atmSrc;

public class Card {
    private int number;
    private boolean expired;
    private int bankCode;

    public Card(int number, boolean expired, int bankCode) {
        this.number = number;
        this.expired = expired;
        this.bankCode = bankCode;
    }

    public int getNumber() {
        return this.number;
    }

    public boolean isExpired() {
        return expired;
    }

    public int getBankCode() {
        return bankCode;
    }
}
