package assignment;

import java.time.LocalDate;

public class Receipt {
    private String receiptNumber;
    private String address;
    private String roomType;
    private String residentName;
    private String month;
    private int year;
    private double totalAmount;
    private double amountPaid;
    private double remainder;
    private double change;
    private LocalDate paymentDate;
    private String status;

    // Constructor to initialize the receipt
    public Receipt(String address, String roomType, String residentName, String month, int year, double totalAmount, double amountPaid, double remainder, double change, LocalDate paymentDate, String status) {
        this.receiptNumber = "REC" + (FileHandler.allReceipts.size() + 10001);
        this.address = address;
        this.roomType = roomType;
        this.residentName = residentName;
        this.month = month;
        this.year = year;
        this.totalAmount = totalAmount;
        this.amountPaid = amountPaid;
        this.remainder = remainder;
        this.change = change;
        this.paymentDate = paymentDate;
        this.status = status;
    }

    // Getters and Setters
    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }
    
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getRoomType() {
        return roomType;
    }
    
    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getResidentName() {
        return residentName;
    }

    public void setResidentName(String residentName) {
        this.residentName = residentName;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public double getRemainder() {
        return remainder;
    }

    public void setRemainder(double remainder) {
        this.remainder = remainder;
    }
    
    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
