
public class Balances {

    private double availableBalance;
    private double totalBalance;

    public Balances(double availableBalance, double totalBalance) {
        this.availableBalance = availableBalance;
        this.totalBalance = totalBalance;
    }

    public double getAvailableBalance() {
        return availableBalance;
    }

    public double getTotalBalance() {
        return totalBalance;
    }

    public void setAvailableBalance(double balance) {
        this.availableBalance = balance;
    }

    public void setTotalBalance(double balance) {
        this.totalBalance = balance;
    }

    @Override
    public String toString() {
        return "Balances{available=" + availableBalance + ", total=" + totalBalance + "}";
    }
}
