package atmSrc;

public class Receipt {

    private String transactionType;
    private int amount;
    private int cardSerial;  // or long

    // (isteğe bağlı: date/time, accountNumber, vb.)

    public String getTransactionType() {
        return transactionType;
    }
    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public int getAmount() {
        return amount;
    }
    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getCardSerial() {
        return cardSerial;
    }
    public void setCardSerial(int cardSerial) {
        this.cardSerial = cardSerial;
    }
}
