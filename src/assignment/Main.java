package assignment;

public class Main {
    // Declare the static loginUser variable
    public static User loginUser = null;

    public static void main(String[] args) {
        FileHandler.writeSystemLog("(System) System has started.");
        FileHandler.read();
        PaymentGenerator generator = new PaymentGenerator();
        generator.generatePayments();
        LoginPage loginpage = new LoginPage();  // Show the login page
        loginpage.openLoginPage();
    }
}
