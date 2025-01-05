
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.google.gson.Gson;

public class Bank {

    private BankData bankData; // JSON’dan gelen veriler

    // Bank, JSON path alarak veriyi yükleyebilir
    public Bank(String jsonFilePath) {
        loadData(jsonFilePath);
    }

    // Parametresiz constructor (bazı durumlarda)
    public Bank() {
        // Default JSON path
        loadData("bankdata.json");
    }

    /**
     * FR1 (bank side):
     * The bank computer checks if the bank code is valid
     * FR2:
     * If not valid => "bad bank code"
     * FR3:
     * Check password...
     * FR4:
     * if invalid => "bad password"
     * FR5:
     * if valid card & password but problems => "bad account"
     * FR6:
     * else => "account ok"
     */
    public String verifyAccount(int bankCode, int accountNumber, String password) {
        // FR1: bank code valid?
        if (!isValidBankCode(bankCode)) {
            return "bad bank code";
        }

        // FR3: password valid?
        // bulalım
        Account acc = findAccount(accountNumber);
        if (acc == null) {
            // “bad account” da diyebiliriz, ama “unknown account” durumu
            return "bad account";
        }

        // eğer password eşleşmezse => FR4
        if (!acc.getPassword().equals(password)) {
            return "bad password";
        }

        // FR5: account sorunlu mu? (ör. status != "active" => “bad account”)
        if (!"active".equalsIgnoreCase(acc.getStatus())) {
            return "bad account";
        }

        // FR6: her şey yolunda
        return "account ok";
    }

    /**
     * Bank code array içinde var mı?
     */
    public boolean isValidBankCode(int code) {
        if (bankData == null || bankData.getValidBankCodes() == null)
            return false;
        return bankData.getValidBankCodes().contains(code);
    }

    /**
     * accounts listesinde arama
     */
    private Account findAccount(int accountNumber) {
        if (bankData == null || bankData.getAccounts() == null)
            return null;
        for (Account ba : bankData.getAccounts()) {
            if (ba.getAccountNumber() == accountNumber) {
                return ba;
            }
        }
        return null;
    }

    /**
     * JSON dosyasını okuyup bankData'ya dolduran metot
     */
    private void loadData(String jsonFilePath) {
        try {
            Path path = Paths.get(jsonFilePath);
            String jsonString = Files.readString(path);
            Gson gson = new Gson();
            this.bankData = gson.fromJson(jsonString, BankData.class);
            System.out.println("[Bank] JSON data loaded from " + jsonFilePath);
        } catch (Exception e) {
            System.err.println("[Bank] Failed to load JSON data: " + e.getMessage());
            this.bankData = new BankData(); // fallback
        }
    }
}
