package assignment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class FileHandler {
    public static ArrayList<Resident> allResidents = new ArrayList<>();
    public static ArrayList<Staff> allStaffs = new ArrayList<>();
    public static ArrayList<Manager> allManagers = new ArrayList<>();
    public static ArrayList<Payment> allPayments = new ArrayList<>();
    public static ArrayList<Receipt> allReceipts = new ArrayList<>();

    public static User getUserByUsername(String username, String role) {
        ArrayList<? extends User> userList;

        switch (role) {
            case "Resident":
                userList = allResidents;
                break;
            case "Staff":
                userList = allStaffs;
                break;
            case "Manager":
                userList = allManagers;
                break;
            default:
                return null;
        }

        for (User user : userList) {
            if (user.getName().equals(username)) {
                return user;
            }
        }

        return null;
    }

    public static void writeToRoleFile(String role) {
        String fileName = role.toLowerCase() + "s.txt";

        ArrayList<? extends User> userList = getUserListByRole(role);

        if (userList == null) {
            JOptionPane.showMessageDialog(null, "Invalid role: " + role, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Try-with-resources to ensure file is properly closed
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName, false))) {
            for (User user : userList) {
                writer.println(user.getName());
                writer.println(user.getPassword());
                writer.println(user.getEmailAddress());
                writer.println(user.getContactNumber());
                writer.println(role);
                if (user.getRole().equalsIgnoreCase("Resident")) {
                    writer.println(((Resident) user).getAddress());
                    writer.println(((Resident) user).getRoomType());
                }
                writer.println();
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving changes to " + fileName, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static ArrayList<? extends User> getUserListByRole(String role) {
        switch (role) {
            case "Resident":
                return allResidents;
            case "Staff":
                return allStaffs;
            case "Manager":
                return allManagers;
            default:
                return null;
        }
    }
    
    public static void writeToAllFile() {
        writeToRoleFile("Resident");
        writeToRoleFile("Staff");
        writeToRoleFile("Manager");
    }
    
    public static void read() {
        // === Clear Lists to Avoid Duplication ===
        allResidents.clear();
        allStaffs.clear();
        allManagers.clear();
        allPayments.clear();
        allReceipts.clear();
        for (Resident resident : FileHandler.allResidents) {
            resident.getMyPayment().clear();
            resident.getMyReceipt().clear();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader("payments.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // Reading Payment ID
                int paymentId = Integer.parseInt(line);

                // Reading Address
                String address = reader.readLine().trim();
                
                // Reading roomType
                String roomType = reader.readLine().trim();
                
                // Reading userName
                String userName = reader.readLine().trim();

                // Reading Month and parsing it from string to Month enum
                String month = reader.readLine().trim(); // Month as a string (e.g., "JANUARY")

                // Reading Year and parsing it
                int year = Integer.parseInt(reader.readLine().trim()); // Year as integer (e.g., 2025)
                
                // Reading amount and parsing it
                double amount = Double.parseDouble(reader.readLine().trim()); // Amount as Double (e.g., 300)
                
                // Reading TotalAmount and parsing it
                double totalAmount = Double.parseDouble(reader.readLine().trim()); // totalAmount as Double (e.g., 300)
                
                // Reading roomType
                String status = reader.readLine().trim();

                // Adding Payment to the list
                allPayments.add(new Payment(paymentId, address, roomType, userName, month, year, amount, totalAmount, status));

                // Skipping blank line between records
                reader.readLine(); // Skip blank line
            }
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "The file 'payments.txt' was not found.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error reading the file 'payments.txt'.");
        }
        
        // === Read Receipts ===
        try (BufferedReader reader = new BufferedReader(new FileReader("receipts.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue; // Skip empty lines

                // Extract information for each receipt
                String receiptId = line;
                String address = reader.readLine().trim();
                String roomType = reader.readLine().trim();
                String residentName = reader.readLine().trim();
                String month = reader.readLine().trim();
                int year = Integer.parseInt(reader.readLine().trim());
                double totalAmount = Double.parseDouble(reader.readLine().trim());
                double amountPaid = Double.parseDouble(reader.readLine().trim());
                double remainder = Double.parseDouble(reader.readLine().trim());
                double change = Double.parseDouble(reader.readLine().trim());
                LocalDate paymentDate = LocalDate.parse(reader.readLine().trim());
                String status = reader.readLine().trim();

                // Create and add the receipt if all fields are valid
                if (!address.isEmpty() && !roomType.isEmpty() && !residentName.isEmpty() && !month.isEmpty() && year > 0 && totalAmount >= 0 && amountPaid >= 0 && remainder >= 0 && change >= 0 && paymentDate != null && !status.isEmpty()) {
                    allReceipts.add(new Receipt(address, roomType, residentName, month, year, totalAmount, amountPaid, remainder, change, paymentDate, status));
                }

                reader.readLine(); // Skip blank line
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "The file 'receipts.txt' was not found.");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Error parsing values in 'receipts.txt'. Please check the format.");
        }

        // === Read Residents ===
        try (BufferedReader reader = new BufferedReader(new FileReader("residents.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue; // Skip empty lines

                String username = line;
                String password = reader.readLine().trim();
                String email = reader.readLine().trim();
                String contactNumber = reader.readLine().trim();
                String role = reader.readLine().trim();
                String address = reader.readLine().trim();
                String roomType = reader.readLine().trim();

                if (!username.isEmpty() && !password.isEmpty() && !email.isEmpty() &&
                    !contactNumber.isEmpty() && !address.isEmpty() && !roomType.isEmpty()) {
                    Resident resident = new Resident(username, password, email, contactNumber, address, roomType);

                    // Link payments to the resident
                    for (int i = 0; i < allPayments.size(); i++) {
                        Payment payment = allPayments.get(i);
                        if (address.equalsIgnoreCase(payment.getAddress()) && roomType.equalsIgnoreCase(payment.getRoomType()) && username.equals(payment.getUserName())) {
                            resident.addPayment(payment); // Assuming `addPayment` handles private access to `myPayment`
                        }
                    }
                    
                    for (int i = 0; i < allReceipts.size(); i++) {
                        Receipt receipt = allReceipts.get(i);
                        if (address.equalsIgnoreCase(receipt.getAddress()) && roomType.equalsIgnoreCase(receipt.getRoomType()) && username.equals(receipt.getResidentName())) {
                            resident.addReceipt(receipt); // Assuming `addPayment` handles private access to `myPayment`
                        }
                    }

                    allResidents.add(resident);
                }

                reader.readLine(); // Skip blank line
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "The file 'residents.txt' was not found.");
        }

        // === Read Staff ===
        try (BufferedReader reader = new BufferedReader(new FileReader("staffs.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue; // Skip empty lines

                String username = line;
                String password = reader.readLine().trim();
                String email = reader.readLine().trim();
                String contactNumber = reader.readLine().trim();

                if (!username.isEmpty() && !password.isEmpty() && !email.isEmpty() &&
                    !contactNumber.isEmpty()) {
                    allStaffs.add(new Staff(username, password, email, contactNumber));
                }

                reader.readLine(); // Skip blank line
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "The file 'staffs.txt' was not found.");
        }

        // === Read Managers ===
        try (BufferedReader reader = new BufferedReader(new FileReader("managers.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue; // Skip empty lines

                String username = line;
                String password = reader.readLine().trim();
                String email = reader.readLine().trim();
                String contactNumber = reader.readLine().trim();

                if (!username.isEmpty() && !password.isEmpty() && !email.isEmpty() &&
                    !contactNumber.isEmpty()) {
                    allManagers.add(new Manager(username, password, email, contactNumber));
                }

                reader.readLine(); // Skip blank line
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "The file 'managers.txt' was not found.");
        }
    }
    
    public static void writePaymentsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("payments.txt"))) {
            for (Payment payment : allPayments) {
                writer.write(String.valueOf(payment.getPaymentId()));
                writer.newLine();
                writer.write(payment.getAddress());
                writer.newLine();
                writer.write(payment.getRoomType());
                writer.newLine();
                writer.write(payment.getUserName());
                writer.newLine();
                writer.write(payment.getMonth());
                writer.newLine();
                writer.write(String.valueOf(payment.getYear()));
                writer.newLine();
                writer.write(String.format("%.2f", payment.getAmount())); // Format amount to 2 decimal places
                writer.newLine();
                writer.write(String.format("%.2f", payment.getTotalAmount())); // Format amount to 2 decimal places
                writer.newLine();
                writer.write(payment.getStatus());
                writer.newLine();
                writer.newLine(); // Add a blank line between entries
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "An error occurred while writing into the 'payments' file. Please check if the file exists and is accessible.", "File Writing Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void writeReceiptsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("receipts.txt"))) {
            // Loop through all receipts and write their details
            for (Receipt receipt : allReceipts) {
                writer.write(receipt.getReceiptNumber());
                writer.newLine();
                writer.write(receipt.getAddress());
                writer.newLine();
                writer.write(receipt.getRoomType());
                writer.newLine();
                writer.write(receipt.getResidentName());
                writer.newLine();
                writer.write(receipt.getMonth());
                writer.newLine();
                writer.write(String.valueOf(receipt.getYear()));
                writer.newLine();
                writer.write(String.valueOf(receipt.getTotalAmount()));  // Write totalAmount value
                writer.newLine();
                writer.write(String.valueOf(receipt.getAmountPaid()));  // Write amountPaid value
                writer.newLine();
                writer.write(String.valueOf(receipt.getRemainder()));  // Write remainder value
                writer.newLine();
                writer.write(String.valueOf(receipt.getChange()));  // Write remainder value
                writer.newLine();
                writer.write(receipt.getPaymentDate().toString());  // Write date in string format
                writer.newLine();
                writer.write(receipt.getStatus());
                writer.newLine();
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error writing to file: " + e.getMessage());
        }
    }
    
    public static void writeSystemLog(String logMessage) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("SystemLogs.txt", true))) {
            // Get the current time in a readable format
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            // Write the log message with the timestamp to the log file
            writer.write(timestamp + " - " + logMessage);
            writer.newLine();  // Add a newline after each log entry
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "There are some errors in writing in SystemLogs.txt file");
        }
    }
    
    public static void clearLogFile(int year, String month) {
        ArrayList<String> updatedLogs = new ArrayList<>();
        String targetMonth = String.format("%02d", SystemLogView.getMonthNumber(month)); // Format month as 2 digits, e.g., "01", "02"

        try (BufferedReader reader = new BufferedReader(new FileReader("SystemLogs.txt"))) {
            String line;

            // Read each line and filter logs not matching the selected year and month
            while ((line = reader.readLine()) != null) {
                String[] lineParts = line.split(" "); // Split the log line by spaces
                if (lineParts.length > 0) {
                    String timestamp = lineParts[0]; // Extract the timestamp (yyyy-MM-dd)
                    String[] dateParts = timestamp.split("-");

                    if (dateParts.length >= 2) {
                        String logYear = dateParts[0];
                        String logMonth = dateParts[1];

                        // If the log does not match the selected year and month, keep it
                        if (!logYear.equals(String.valueOf(year)) && !logMonth.equals(targetMonth)) {
                            updatedLogs.add(line);
                        }
                    }
                }
            }

            // Write the filtered logs back to the file
            try (FileWriter writer = new FileWriter("SystemLogs.txt")) {
                for (String log : updatedLogs) {
                    writer.write(log + "\n");
                }
            }

            JOptionPane.showMessageDialog(null, "Logs for " + month + " " + year + " cleared.", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error clearing log file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}