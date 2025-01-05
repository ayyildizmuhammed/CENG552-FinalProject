
public class Card {
    private int number;       // Serial number
    private boolean expired;
    private int bankCode;     // Ekledik: FR7, FR8 'bad bank code' kontrolü için

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
