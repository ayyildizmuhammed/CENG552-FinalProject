package atmSrc;

public class Withdrawal extends Transaction {

    private int from;
    private Money amount;

    public Withdrawal(ATM atm, Session session, Card card, int pin) {
        this.atm = atm;
        this.session = session;
        this.card = card;
        this.pin = pin;
    }

    public Message getSpecificsFromCustomer() {
        // Ekrandan çekilecek tutarı alalım (örnek):
        this.from = session.getAccountNumber();
        this.amount = new Money(200, "USD");  // Hard-coded
        return new Message("WITHDRAW_REQUEST","Withdraw 200 USD");
    }

    public Receipt completeTransaction() {
        // ATM'in withdraw metodu
        atm.withdraw((int)amount.getAmount());
        return new Receipt();
    }
}
