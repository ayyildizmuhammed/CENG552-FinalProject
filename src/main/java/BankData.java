
import java.util.List;

public class BankData {

    private List<Integer> validBankCodes;
    private List<Account> accounts;

    public BankData() {
    }

    public List<Integer> getValidBankCodes() {
        return validBankCodes;
    }

    public void setValidBankCodes(List<Integer> validBankCodes) {
        this.validBankCodes = validBankCodes;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }
}
