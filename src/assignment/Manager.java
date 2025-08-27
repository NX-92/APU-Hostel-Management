package assignment;

public class Manager extends User {
    public Manager(String name, String password, String emailAddress, String contactNumber) {
        super(name, password, emailAddress, contactNumber);
    }

    public String getRole() {
        return "Manager";
    }
}