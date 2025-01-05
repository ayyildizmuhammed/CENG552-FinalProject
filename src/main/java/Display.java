
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

    public int readMenuChoice(String prompt, String[] menu) {
        // ...
        return 0;
    }

    public Money readAmount(String prompt) {
        // ...
        return new Money(100, "USD"); 
    }
}
