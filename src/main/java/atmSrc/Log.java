package atmSrc;

public class Log {

    public Log() {
    }

    public void logSerialNumber(int serial) {
        System.out.println("LOG - Card Serial Number: " + serial);
    }

    public void logCashDispensed(Money amount) {
        System.out.println("LOG - Cash Dispensed: " + amount.getAmount() + " " + amount.getCurrency());
    }

    public void logSend(Message message) {
        System.out.println("LOG - Sending message to bank: " + message);
    }

    public void logResponse(Status status) {
        System.out.println("LOG - Response from bank: " + status);
    }

    public void logEnvelopeAccepted() {
        System.out.println("LOG - Envelope (deposit) accepted");
    }

    // Örneğin deposit logu
    public void logDeposit(Money amount, int accountNum) {
        System.out.println("LOG - Deposit " + amount + " to account " + accountNum);
    }
}
