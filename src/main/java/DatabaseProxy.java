import java.util.HashMap;
import java.util.Map;

public class DatabaseProxy {

    // Mevcut map'ler
    private static Map<Integer, String> accountPinMap = new HashMap<>();
    private static Map<Integer, Double> accountBalanceMap = new HashMap<>();

    // Yeni: Günlük kullanım takibi
    private static Map<Integer, Double> dailyUsedMap = new HashMap<>();

    static {
        // Örnek veriler
        accountPinMap.put(1234, "1111");
        accountBalanceMap.put(1234, 1000.0);

        accountPinMap.put(9999, "2222");
        accountBalanceMap.put(9999, 2000.0);

        accountPinMap.put(5555, "9999");
        accountBalanceMap.put(5555, 1500.0);

        accountPinMap.put(4321, "1234");
        accountBalanceMap.put(4321, 500.0);
    }

    public String selectPasswordByAccountNum(int accountNum) {
        return accountPinMap.get(accountNum);
    }

    public void minusBalance(int accountNum, double amount) {
        if (!accountBalanceMap.containsKey(accountNum)) return;
        double current = accountBalanceMap.get(accountNum);
        accountBalanceMap.put(accountNum, current - amount);
    }

    public void plusBalance(int accountNum, double amount) {
        if (!accountBalanceMap.containsKey(accountNum)) return;
        double current = accountBalanceMap.get(accountNum);
        accountBalanceMap.put(accountNum, current + amount);
    }

    public void checkTheBalance(int accountNum) {
        Double bal = accountBalanceMap.get(accountNum);
        if (bal == null) {
            System.out.println("Balance: Account not found!");
        } else {
            System.out.println("Balance: " + bal);
        }
    }

    /**
     * FR9: Her hesap için günlük limit k var.
     * Bu metot, "amount" çekileceğinde daily limit aşılır mı diye bakar.
     *  - eğer (günlükKullanım + amount) <= dailyLimit ise true döner ve dailyUsedMap'i günceller
     *  - değilse false döner
     */
    public boolean checkAndUpdateDailyLimit(int accountNum, double amount, double dailyLimit) {
        double usedSoFar = dailyUsedMap.getOrDefault(accountNum, 0.0);
        double newUsed = usedSoFar + amount;
        if (newUsed > dailyLimit) {
            // limit aşıldı
            return false;
        } else {
            // limit dahilinde
            dailyUsedMap.put(accountNum, newUsed);
            return true;
        }
    }

    /**
     * FR8: Para gerçekten ATM'den çıktıktan sonra, "update account" (minusBalance)
     * Bu, ATM'den "MONEY_DISPENSED" gibi bir mesaj aldığımızda çağrılabilir.
     */
    public void applyWithdrawal(int accountNum, double amount) {
        minusBalance(accountNum, amount);
    }

    // Additional methods for testing

    public void setDailyUsed(int accountNum, double usage) {
        dailyUsedMap.put(accountNum, usage);
    }

    public void setBalance(int accountNum, double balance) {
        accountBalanceMap.put(accountNum, balance);
    }
}
