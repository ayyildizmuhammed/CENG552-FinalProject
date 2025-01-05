
public class Deposit extends Transaction {

    private int to;
    private Money amount;

    public Deposit(ATM atm, Session session, Card card, int pin) {
        this.atm = atm;
        this.session = session;
        this.card = card;
        this.pin = pin;
    }

    public Message getSpecificsFromCustomer() {
        // Basitçe paranın miktarını alalım
        // Normalde ATM display üzerinden input okur
        this.amount = new Money(300, "USD"); // Hard-coded örnek
        return new Message("DEPOSIT_REQUEST","Deposit 300 USD");
    }

    public Receipt completeTransaction() {
        // Varsayalım: Müşteri parayı yatırıyor, 
        // ATM meblağı database'e plusBalance vb. ekleyebilir.
        // (Bu akış, ATM’de tam gösterilmemiş. Gerekirse ATM’de deposit() metodu da eklenir.)

        // Demo: Log’a “envelope accepted”
        atm.getLog().logEnvelopeAccepted();

        // Geriye bir Receipt
        return new Receipt();
    }
}
