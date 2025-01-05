
import java.net.InetAddress;

public class NetworkToBank {

    private Log log;
    private InetAddress bankAddress;
    private Bank bank; // Bank nesnesi (JSON verilerini tutan)

    public NetworkToBank(Log log, InetAddress bankAddress, Bank bank) {
        this.log = log;
        this.bankAddress = bankAddress;
        this.bank = bank;
    }

    public void openConnection() {
        System.out.println("[NetworkToBank] Connection opened.");
    }

    public void closeConnection() {
        System.out.println("[NetworkToBank] Connection closed.");
    }

    /**
     * Authorization request
     * Input: bankCode, accountNumber, password
     * Output: Status => success or error code
     */
    public Status sendAuthorizationRequest(int bankCode, int accountNumber, String password) {
        openConnection();
        // Bank'tan sonucu alalım:
        String result = bank.verifyAccount(bankCode, accountNumber, password);
        closeConnection();

        switch (result) {
            case "bad bank code":
                return new Status(false, "bad bank code"); // FR2
            case "bad password":
                return new Status(false, "bad password"); // FR4
            case "bad account":
                return new Status(false, "bad account"); // FR5
            case "account ok":
                return new Status(true, "account ok"); // FR6
            default:
                // Beklenmeyen durum
                return new Status(false, "unknown error");
        }
    }

    /**
     * FR7-FR9-FR10 vb. bank side
     * "WITHDRAW_REQUEST", "TRANSFER_REQUEST" vs.
     */
    public Status sendMessage(Message message, Balances balances) {
        openConnection();

        String code = message.getCode();
        // Basit simülasyon
        if ("WITHDRAW_REQUEST".equals(code)) {
            // Normalde: Banka, account balance, daily limit vb. check yapar
            // Burada "başarılı" dönelim
            closeConnection();
            return new Status(true, null);

        } else if ("TRANSFER_REQUEST".equals(code)) {
            // Transfer mantığı
            closeConnection();
            return new Status(true, null);

        } else {
            // Diğer durumlar
            closeConnection();
            return new Status(false, "Unknown request code");
        }
    }
}
