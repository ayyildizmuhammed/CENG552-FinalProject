package atmSrc;

public class Money {

    private double amount;
    private String currency;

    public Money(double amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public double getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    // Birkaç basit yardımcı metot
    public boolean isGreaterThan(Money other) {
        // Sadece currency aynı varsayıyoruz
        return this.amount > other.amount;
    }

    public Money subtract(Money other) {
        return new Money(this.amount - other.amount, this.currency);
    }

    public Money add(Money other) {
        return new Money(this.amount + other.amount, this.currency);
    }

    @Override
    public String toString() {
        return amount + " " + currency;
    }
}
