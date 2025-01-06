package atmSrc;

public class ReceiptPrinter {

    public ReceiptPrinter() {
        // Artık UnsupportedOperationException atmıyoruz.
    }

    public void printReceipt(Receipt receipt) {
        if (receipt == null) {
            System.out.println("[ReceiptPrinter] No receipt data to print.");
            return;
        }
        System.out.println("=== RECEIPT ===");
        System.out.println("Transaction Type: " + receipt.getTransactionType());
        System.out.println("Card Serial: " + receipt.getCardSerial());
        // eğer accountNumber da varsa:
        // System.out.println("Account: " + receipt.getAccountNumber());
        System.out.println("Amount: " + receipt.getAmount());
        System.out.println("Thank you for using our ATM!");
    }
}
