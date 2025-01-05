package atmSrc;

public class CardReader {

    private ATM atm;
    private Card currentCard;

    public CardReader(ATM atm) {
        this.atm = atm;
    }

    // FR4: Kart geçerli mi?
    public boolean checkCardValidity(Card card) {
        if (card == null) return false;
        if (card.isExpired()) return false;
        this.currentCard = card;
        return true;
    }

    public Card readCard() {
        return this.currentCard;
    }

    public void ejectCard() {
        System.out.println("[CardReader] Card is ejected.");
        this.currentCard = null;
    }

    public void retainCard() {
        System.out.println("[CardReader] Card is retained by ATM.");
        this.currentCard = null;
    }
}
