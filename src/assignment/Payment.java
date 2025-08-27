package assignment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import javax.swing.JOptionPane;

public class Payment {
    private int paymentId;
    private String address;
    private String roomType;
    private String userName;
    private String month; // Store month as a String (e.g., "January")
    private int year; // Store year as an int (e.g., 2025)
    private double amount;
    private double totalAmount;
    private String status = "Unsuccessful";
    
    private static double latestSmallRoomPaymentRate = 300.00;
    private static double latestNormalRoomPaymentRate = 400.00;
    private static double latestMasterRoomPaymentRate = 600.00;
    private static final String PAYMENT_RATE_FILE = "paymentRate.txt";

    // Constructor to load payment rate from file
    static {
        loadPaymentRate();
    }

    public Payment(int paymentId, String address, String roomType, String userName, String month, int year, double amount, double totalAmount, String status) {
        this.paymentId = paymentId;
        this.address = address;
        this.roomType = roomType;
        this.userName = userName;
        this.month = month;
        this.year = year;
        this.amount = amount;
        this.totalAmount = totalAmount; // Default total amount
        this.status = status;
    }

    // Getters and Setters
    public int getPaymentId() {
        return paymentId;
    }

    public String getAddress() {
        return address;
    }

    public String getRoomType() {
        return roomType;
    }
    
    public String getUserName() {
        return userName;
    }

    public String getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public double getAmount() {
        return amount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
        this.amount = getPaymentRateByRoomType(roomType); // Update amount based on room type
    }
    
    public void setUserName (String userName) {
        this.userName = userName;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static void updatePaymentRate(String roomType, double newRate) {
        switch (roomType) {
            case "Small Room":
                latestSmallRoomPaymentRate = newRate;
                break;
            case "Normal Room":
                latestNormalRoomPaymentRate = newRate;
                break;
            case "Master Room":
                latestMasterRoomPaymentRate = newRate;
                break;
            default:
                throw new IllegalArgumentException("Invalid room type: " + roomType);
        }
        savePaymentRate(); // Save the new rate to a file
    }

    public static double getPaymentRateByRoomType(String roomType) {
        switch (roomType) {
            case "Small Room":
                return latestSmallRoomPaymentRate;
            case "Normal Room":
                return latestNormalRoomPaymentRate;
            case "Master Room":
                return latestMasterRoomPaymentRate;
            default:
                throw new IllegalArgumentException("Invalid room type: " + roomType);
        }
    }

    private static void loadPaymentRate() {
        try (Scanner scanner = new Scanner(new File(PAYMENT_RATE_FILE))) {
            if (scanner.hasNextDouble()) {
                latestSmallRoomPaymentRate = scanner.nextDouble();
            }
            if (scanner.hasNextDouble()) {
                latestNormalRoomPaymentRate = scanner.nextDouble();
            }
            if (scanner.hasNextDouble()) {
                latestMasterRoomPaymentRate = scanner.nextDouble();
            }
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Payment rate file not found. Using default rates.");
        }
    }

    private static void savePaymentRate() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PAYMENT_RATE_FILE))) {
            writer.write(String.valueOf(latestSmallRoomPaymentRate));
            writer.newLine();
            writer.write(String.valueOf(latestNormalRoomPaymentRate));
            writer.newLine();
            writer.write(String.valueOf(latestMasterRoomPaymentRate));
            writer.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "There are some errors occured when saving payment rate!!!");
        }
    }
}
