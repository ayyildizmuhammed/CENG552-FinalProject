package atmSrc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.google.gson.Gson;

public class DatabaseProxy {

    private BankData bankData;

    public DatabaseProxy(String jsonFilePath) {
        loadData(jsonFilePath);
    }

    public BankData getBankData() {
        return this.bankData;
    }

    public boolean saveCurrentData() {
        try {
            Gson gson = new Gson();
            String jsonString = gson.toJson(bankData);
            Path path = Paths.get(getClass().getClassLoader().getResource("bankdata.json").toURI());
            Files.writeString(path, jsonString);
            System.out.println("[Bank] JSON data saved to bankdata.json");
            loadData("bankdata.json");
            return true;
        } catch (Exception e) {
            System.err.println("[Bank] Failed to save JSON data: " + e.getMessage());
            return false;
        }
    }

    public boolean isValidBankCode(int code) {
        if (bankData == null || bankData.getValidBankCodes() == null)
            return false;
        return bankData.getValidBankCodes().contains(code);
    }

    public Account findAccount(int accountNumber) {
        if (bankData == null || bankData.getAccounts() == null)
            return null;
        for (Account ba : bankData.getAccounts()) {
            if (ba.getAccountNumber() == accountNumber) {
                return ba;
            }
        }
        return null;
    }

    public void minusBalance(int accountNum, double amount) {
        Account account = bankData.getAccounts().stream().filter(a -> a.getAccountNumber() == accountNum)
                .findFirst().get();
        account.getBalance().setAvailableBalance(account.getBalance().getAvailableBalance() - amount);
    }

    public void plusBalance(int accountNum, double amount) {
        Account account = bankData.getAccounts().stream().filter(a -> a.getAccountNumber() == accountNum)
                .findFirst().get();
        account.getBalance().setAvailableBalance(account.getBalance().getAvailableBalance() + amount);
    }

    public void checkTheBalance(int accountNum) {
        Account account = bankData.getAccounts().stream().filter(a -> a.getAccountNumber() == accountNum)
                .findFirst()
                .get();
        System.out.println("Available balance: " + account.getBalance().getAvailableBalance());
        System.out.println("Total balance: " + account.getBalance().getTotalBalance());

    }

    public boolean checkAndUpdateDailyLimit(int accountNum, double amount, double dailyAtmLimit) {

        boolean result = false;
        Account account = getBankData().getAccounts().stream().filter(a -> a.getAccountNumber() == accountNum)
                .findFirst().get();
        if (account.getDailyUsed() + amount <= dailyAtmLimit) {
            account.setDailyUsed(account.getDailyUsed() + amount);
            result = true;
        }
        return result;

    }

    public void applyWithdrawal(int accountNum, double amount) {
        minusBalance(accountNum, amount);
    }

    private void loadData(String jsonFilePath) {
        try {
            Path path = Paths.get(getClass().getClassLoader().getResource(jsonFilePath).toURI());
            String jsonString = Files.readString(path);
            Gson gson = new Gson();
            this.bankData = gson.fromJson(jsonString, BankData.class);
            System.out.println("[Bank] JSON data loaded from " + jsonFilePath);
        } catch (Exception e) {
            System.err.println("[Bank] Failed to load JSON data: " + e.getMessage());
            this.bankData = new BankData(); // fallback
        }
    }

    public List<String> getLast10Transactions(int accountNum) {
        Account account = findAccount(accountNum);
        return account.getTransactionHistory();
    }

    public boolean updatePassword(int accountNum, String newPwd) {
        Account acc = findAccount(accountNum);
        if (acc == null) {
            return false;
        }
        acc.setPassword(newPwd);
        return true;
    }

}
