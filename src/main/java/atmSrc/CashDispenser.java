package atmSrc;

public class CashDispenser {

    private Log log;
    private Money cashOnHand; // ATM içinde fiziksel nakit

    public CashDispenser(Log log, int totalFund) {
        this.log = log;
        this.cashOnHand = new Money(totalFund, "USD");
    }

    public void setInitialCash(Money initialCash) {
        this.cashOnHand = initialCash;
    }

    public boolean checkCashOnHand(Money amount) {
        // amount isGreaterThan -> yeterli mi?
        return !amount.isGreaterThan(this.cashOnHand);
    }

    public void dispenseCash(Money amount) {
        // buraya kadar geldiysek yeterli cash var demektir
        this.cashOnHand = this.cashOnHand.subtract(amount);
        System.out.println("Dispensing " + amount.getAmount() + " " + amount.getCurrency());
        log.logCashDispensed(amount);
    }

    public void putCash() {
        // deposit senaryolarında da buraya ekleme yapılabilir.
    }
}
