package mocks;
import java.util.Arrays;

import atmSrc.Account;
import atmSrc.Balance;
import atmSrc.BankData;
import atmSrc.DatabaseProxy;

public class MockDatabaseProxy extends DatabaseProxy {

    private BankData mockData;

    public MockDatabaseProxy() {
        super(null);  // parent constructor
        // buraya “loadData()” çağırmayacağız
        this.mockData = createMockData();
    }

    @Override
    public BankData getBankData() {
        return this.mockData;
    }

    @Override
    public boolean saveCurrentData() {
        // Mock'ta hiçbir şey kaydetmiyor, her zaman true dönebilir
        return true;
    }

    private BankData createMockData() {
        BankData bd = new BankData();
        // 1) validBankCodes
        bd.setValidBankCodes(Arrays.asList(1001));
        
        // 2) mock “accounts” listesi
        Balance b1 = new Balance(1000, 1000);
        Account a1 = new Account(1234, "1111", "active", b1);

        Balance b2 = new Balance(1000, 1000);
        Account a2 = new Account(9999, "2222", "active", b2);

        // … frozen account, vb.
        Balance b3 = new Balance(1000, 1000);
        Account a3 = new Account(8888, "3333", "frozen", b3);

        bd.setAccounts(Arrays.asList(a1, a2, a3));

        return bd;
    }
}
