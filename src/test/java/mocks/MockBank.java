package mocks;
import atmSrc.Bank;

public class MockBank extends Bank {

    public MockBank() {
        super(null); // parent constructor
        // Bank.java default constructor, normalde “new DatabaseProxy(...)” yapıyordu
        // Bu sefer “mockDb” verelim:
        this.dbProxy = new MockDatabaseProxy(); 
    }
}
