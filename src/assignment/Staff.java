package assignment;

public class Staff extends User {
    public Staff(String name, String password, String emailAddress, String contactNumber) {
        super(name, password, emailAddress, contactNumber);
    }

    public String getRole() {
        return "Staff";
    }
}
