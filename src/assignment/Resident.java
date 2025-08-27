package assignment;

import java.util.ArrayList;
    
public class Resident extends User {
    private String address;
    private String roomType;
    private ArrayList<Payment> myPayment;
    private ArrayList<Receipt> myReceipt;

    public Resident(String name, String password, String emailAddress, String contactNumber, String address, String roomType) {
        super(name, password, emailAddress, contactNumber);
        this.address = address;
        this.roomType = roomType;
        this.myPayment = new ArrayList<>();
        this.myReceipt = new ArrayList<> ();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRole() {
        return "Resident";
    }
    
    public ArrayList getMyPayment() {
        return myPayment;
    }
    
    // Public method to add a payment
    public void addPayment(Payment payment) {
        myPayment.add(payment);
    }

    // Public method to get the payment list (if needed)
    public ArrayList<Payment> getPayments() {
        return new ArrayList<>(myPayment);
    }
    
    public ArrayList getMyReceipt() {
        return myReceipt;
    }
    
    // Public method to add a payment
    public void addReceipt(Receipt receipt) {
        myReceipt.add(receipt);
    }

    // Public method to get the payment list (if needed)
    public ArrayList<Receipt> getReceipts() {
        return new ArrayList<>(myReceipt);
    }
    
    public void setPayments(ArrayList myPayment) {
        this.myPayment = myPayment;
    }
    
    public void setReceipts(ArrayList myReceipt) {
        this.myReceipt = myReceipt;
    }
    
    public String getRoomType() {
        return roomType;
    }
    
    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }
}

