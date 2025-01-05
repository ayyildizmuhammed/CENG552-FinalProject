
public class Transfer extends Transaction {

    private int from;
    private int to;
    private Money amount;

    public Transfer(ATM atm, Session session, Card card, int pin) {
        this.atm = atm;
        this.session = session;
        this.card = card;
        this.pin = pin;
    }

    public Message getSpecificsFromCustomer() {
        // Normalde ekrandan "toAccount" ve "amount" alır
        this.from = session.getAccountNumber();
        this.to = 9999; // Hard-coded
        this.amount = new Money(150, "USD");
        return new Message("TRANSFER_REQUEST", 
            "Transfer from " + from + " to " + to + " amount 150 USD");
    }

    public Receipt completeTransaction() {
        // ATM içindeki "transfer(to, amount)" metodu çağrılabilir
        atm.transfer(this.to, (int)this.amount.getAmount());
        return new Receipt();
    }
}
