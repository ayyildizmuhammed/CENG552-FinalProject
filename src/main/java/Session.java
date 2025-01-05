
public class Session {
    private int wrongPasswordAttempts;
    private boolean authorized;
    private int accountNumber;  // Hangi hesapla ilişkilendirildiği
    private int cardNumber;     // Hangi kartla işlem yapıldığı
    private int bankCode;       // Hangi banka ile ilişkilendirildiği

    public Session() {
        this.wrongPasswordAttempts = 0;
        this.authorized = false;
    }

    public void incrementWrongAttempt() {
        wrongPasswordAttempts++;
    }

    public int getWrongPasswordAttempts() {
        return wrongPasswordAttempts;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public int getBankCode() {
        return bankCode;
    }

    public void setBankCode(int bankCode) {
        this.bankCode = bankCode;
    }

    public void setAccountNumber(int acc) {
        this.accountNumber = acc;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public void setCardNumber(int cardNum) {
        this.cardNumber = cardNum;
    }

    public int getCardNumber() {
        return cardNumber;
    }
}
