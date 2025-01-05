
public class DemoATM {

    public static void main(String[] args) {
        Bank bank = new Bank("bankdata.json");

        // 1) ATM oluşturma (FR1: t=10000, k=2000, m=500, n=500)
        System.out.println("=== DEMO: Initialize ATM ===");
        ATM atm = new ATM(10000, 2000, 500, 500, bank);

        // 2) Kart Oluşturma
        // Card(number, expired, bankCode)
        // Bank code = 1001 geçerli (Bank.java içinde sabitledik)
        Card validCard = new Card(12345, false, 1001);

        // 3) Kartı ATM'ye takma (insertCard)
        // Beklenen: "CARD_ACCEPTED"
        System.out.println("\n=== DEMO: Insert a valid card ===");
        Message msgInsert = atm.insertCard(validCard);
        System.out.println("insertCard -> " + msgInsert);

        // 4) ATM verify (PIN girişi).
        // Bu senaryoda DBProxy’de 1234 accountNum varsa PIN=1111
        // Hatalı PIN girersek (örn. 9999), BAD_PASSWORD dönecek.
        System.out.println("\n=== DEMO: Enter PIN ===");
        Message msgVerify = atm.verify(1111, 1234);
        System.out.println("verify -> " + msgVerify);

        // 5) Para çekme senaryosu: FR11–FR16
        // Örneğin 300$ çekmeyi deniyoruz.
        System.out.println("\n=== DEMO: Withdraw 300 USD ===");
        Message msgWithdraw = atm.withdraw(300);
        System.out.println("withdraw -> " + msgWithdraw);

        // 6) Tekrar kart takıp, bu kez hatalı PIN girelim.
        // 3 kez üst üste hata sonrası kart retain edilecek.
        System.out.println("\n=== DEMO: Insert valid card, then enter wrong PIN 3 times ===");
        atm.showInitialDisplay(); // FR2
        Message msgInsertAgain = atm.insertCard(validCard);
        System.out.println("insertCard -> " + msgInsertAgain);

        // Wrong PIN 1
        Message msgVerifyWrong1 = atm.verify(9999, 1234);
        System.out.println("verify (wrong1) -> " + msgVerifyWrong1);

        // Wrong PIN 2
        Message msgVerifyWrong2 = atm.verify(8888, 1234);
        System.out.println("verify (wrong2) -> " + msgVerifyWrong2);

        // Wrong PIN 3
        Message msgVerifyWrong3 = atm.verify(7777, 1234);
        System.out.println("verify (wrong3) -> " + msgVerifyWrong3);

        // 7) Transfer senaryosu (FR17)
        // Yine bir kart takalım, doğru PIN girelim, ardından transfer yapalım.
        System.out.println("\n=== DEMO: Transfer Money ===");
        // Yeni kart takalım (ya da aynı kart, ama retained olduysa o kart gitti)
        // Burada farklı bir kart demosu yapabiliriz.
        Card secondCard = new Card(99999, false, 1001); // Bu da bankCode=1001
        Message msgInsertSecond = atm.insertCard(secondCard);
        System.out.println("insertCard -> " + msgInsertSecond);

        // DBProxy’de 9999 accountNum var, PIN=2222
        Message msgVerifySecond = atm.verify(2222, 9999);
        System.out.println("verify -> " + msgVerifySecond);

        // Transfer 150 USD -> “to account=1234”
        // Bu, sendMessage("TRANSFER_REQUEST") => NetworkToBank => success
        Message msgTransfer = atm.transfer(1234, 150);
        System.out.println("transfer -> " + msgTransfer);

        System.out.println("\n=== DEMO FINISHED ===");
    }
}
