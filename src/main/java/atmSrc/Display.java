package atmSrc;

public class Display {

    public Display() {
    }

    public void display(String message) {
        System.out.println("[DISPLAY] " + message);
    }

    public int readPIN(String prompt) {
        // Konsoldan okumak istenirse, Scanner vs. kullanılabilir. Demo:
        System.out.println(prompt);
        return 1111; // Hard-coded ya da test simülasyonu
    }

    public int showPinMenu() {
        // Bu method, gerçekte ekranda 3 buton sunuyormuş gibi simüle edebilir:
        System.out.println("[DISPLAY] Please enter your PIN or choose an option:");
        System.out.println("1) Confirm");
        System.out.println("2) Correction");
        System.out.println("3) Take the card");
        // Burada Scanner ile input alabilirsiniz. Şimdilik pseudo-code:
        int choice = 1; // Hard-coded simülasyon: 1 -> Confirm
        return choice;
    }

    public int readMenuChoice(String prompt, String[] menu) {
        // ...
        return 0;
    }

    public Money readAmount(String prompt) {
        // ...
        return new Money(100, "USD"); 
    }
}
