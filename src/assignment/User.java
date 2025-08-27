package assignment;

public abstract class User {
    private String name;
    private String password;
    private String emailAddress;
    private String contactNumber;

    public User(String name, String password, String emailAddress, String contactNumber) {
        this.name = name;
        this.password = password;
        this.emailAddress = emailAddress;
        this.contactNumber = contactNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public abstract String getRole();
}