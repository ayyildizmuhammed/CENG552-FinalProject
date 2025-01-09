package atmSrc;

import java.util.Scanner;

public class DemoConsoleApp {

    private static int readIntFromUser(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt); // Kullanıcıya mesaj göster
            String input = sc.nextLine().trim(); // Kullanıcıdan girdi al ve boşlukları temizle
            try {
                return Integer.parseInt(input); // Girdi bir tam sayı ise döndür
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a numeric value."); // Hata mesajı göster
            }
        }
    }

    public static void main(String[] args) {
        // 1) Bank oluştur (JSON data yolu)
        Bank bank = new Bank("bankdata.json");

        // 2) ATM oluştur (FR1: parametreler)
        // Örnek: totalFund=5000, dailyLimit=1500, tra2nsactionLimit=500,
        // minCashRequired=100
        ATM atm = new ATM(5000, 1500, 500, 100, bank);

        System.out.println("=== Welcome to the Console ATM Demo ===");
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1) Insert Card");
            System.out.println("2) Exit");
            System.out.print("Enter choice: ");
            int choice = readIntFromUser(sc, "Enter choice: ");

            if (choice == 1) {
                // 3) Kart Oluşturma (örnek sabit veriler)
                int serialNumber = 12345;
                int accountNumber = 1234;
                int bankCode = 1001;

                Card card = new Card(serialNumber, accountNumber, bankCode, false);

                // 4) Kartı ATM'ye takma
                Message msgInsert = atm.insertCard(card);
                System.out.println("insertCard -> " + msgInsert);

                if ("CARD_ACCEPTED".equals(msgInsert.getCode())) {
                    // Kart kabul edildiyse PIN iste
                    int enteredPin = readIntFromUser(sc, "Enter PIN: ");


                    // PIN doğrulama
                    Message msgVerify = atm.verify(enteredPin);
                    System.out.println("verify -> " + msgVerify);

                    if ("ACCOUNT_OK".equals(msgVerify.getCode())) {
                        // Başarıyla authorize olduktan sonra
                        // Artık withdrawal, deposit, transfer, inquiry vb. yapabiliriz
                        boolean done = false;
                        while (!done) {
                            System.out.println("\n--- Transaction Menu ---");
                            System.out.println("1) Withdraw");
                            System.out.println("2) Deposit");
                            System.out.println("3) Transfer");
                            System.out.println("4) Inquiry - Balance");
                            System.out.println("5) Inquiry - Detailed");
                            System.out.println("6) Change Password");
                            System.out.println("7) Exit (Eject card)");
                            int txChoice = readIntFromUser(sc, "Choose transaction: ");

                            switch (txChoice) {
                                case 1: // Withdraw
                                    int wAmount = readIntFromUser(sc, "Enter amount to withdraw: ");
                                    Message wMsg = atm.withdraw(wAmount);
                                    System.out.println("withdraw -> " + wMsg);
                                    break;

                                case 2: // Deposit
                                    int dAmount = readIntFromUser(sc, "Enter amount to deposit: ");
                                    Message dMsg = atm.deposit(dAmount);
                                    System.out.println("deposit -> " + dMsg);
                                    break;

                                case 3: // Transfer
                                    int toAcc = readIntFromUser(sc, "Enter target account number: ");
                                    int tAmount = readIntFromUser(sc, "Enter transfer amount: ");
                                    Message tMsg = atm.transfer(toAcc, tAmount);
                                    System.out.println("transfer -> " + tMsg);
                                    break;

                                case 4: // Inquiry balance
                                    Message iMsg = atm.inquiry("balance");
                                    System.out.println("inquiry -> " + iMsg);
                                    break;

                                case 5: // Inquiry detailed
                                    Message iDetMsg = atm.inquiry("detailed");
                                    System.out.println("inquiry -> " + iDetMsg);
                                    break;

                                case 6: // Change password
                                    int oldPin = readIntFromUser(sc, "Enter old PIN: ");
                                    int newPin = readIntFromUser(sc, "Enter new PIN: ");
                                    Message cPassMsg = atm.changePassword(oldPin, newPin);
                                    System.out.println("changePassword -> " + cPassMsg);
                                    break;

                                case 7: // Exit => Kart iade + logout
                                    System.out.println("Exiting transaction menu, card ejected.");
                                    done = true;
                                    break;

                                default:
                                    System.out.println("Invalid transaction choice.");
                            }
                        }
                    }
                }
            } else if (choice == 2) {
                System.out.println("Exiting program. Goodbye!");
                break;
            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        }
        sc.close();
        System.out.println("=== Demo ended ===");
    }
}
