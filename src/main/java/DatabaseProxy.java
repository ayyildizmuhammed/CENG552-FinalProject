
import java.util.HashMap;
import java.util.Map;

public class DatabaseProxy {

    private static Map<Integer, String> accountPinMap = new HashMap<>();
    private static Map<Integer, Double> accountBalanceMap = new HashMap<>();

    static {
        accountPinMap.put(1234, "1111");
        accountBalanceMap.put(1234, 1000.0);

        accountPinMap.put(9999, "2222");
        accountBalanceMap.put(9999, 2000.0);
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
}
