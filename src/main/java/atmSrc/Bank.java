package atmSrc;

public class Bank {
    private DatabaseProxy dbProxy;

    public Bank(String jsonFilePath) {
        this.dbProxy = new DatabaseProxy(jsonFilePath);
    }

    public DatabaseProxy getDbProxy() {
        return dbProxy;
    }

    public String verifyAccount(int bankCode, int accountNumber, String password) {
        if (!this.dbProxy.isValidBankCode(bankCode)) {
            return "bad bank code";
        }

        Account acc = this.dbProxy.findAccount(accountNumber);
        if (acc == null) {
            // “bad account” da diyebiliriz, ama “unknown account” durumu
            return "bad account";
        }

        if (!acc.getPassword().equals(password)) {
            acc.incrementInvalidPasswordCount();
            return "bad password";
        }

        acc.resetInvalidPasswordCount();

        // FR5: account sorunlu mu? (ör. status != "active" => “bad account”)
        if (!"active".equalsIgnoreCase(acc.getStatus())) {
            return "bad account";
        }

        // FR6: her şey yolunda
        return "account ok";
    }
}
