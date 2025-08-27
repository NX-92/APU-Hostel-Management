package assignment;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ViewAllPaymentReceipt implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        JButton sourceButton = (JButton) e.getSource();

        if (sourceButton.getText().equals("Search")) {
            String address = "";
            if (userRole.equals("Resident")) {
                address = residentAddress;
            } else {
                address = blockComboBox.getSelectedItem() + "_" +
                        String.format("%02d", floorComboBox.getSelectedItem()) + "_" +
                        String.format("%02d", numberComboBox.getSelectedItem());
            }

            String selectedMonth = (String) monthComboBox.getSelectedItem();
            String selectedYear = yearComboBox.getSelectedItem().toString(); // Convert to String
            String selectedRoomType = (String) roomTypeComboBox.getSelectedItem();
            String type = String.valueOf(typeComboBox.getSelectedItem());

            displayTransaction(address, selectedMonth, selectedYear, selectedRoomType, userName, userRole, type);
        } else if (sourceButton.getText().equals("Search by Receipt Number") || sourceButton.getText().equals("Search by Payment Number")) {
            String number = receiptNumberField.getText().trim();
            String type = String.valueOf(typeComboBox.getSelectedItem());
            if (!number.isEmpty()) {
                displayTransactionByNumber(number, userRole, type);
            } else {
                paymentReceiptArea.setText("Please enter a valid number. (Example: 10001(PAYMENT) or REC10001(RECEIPT)");
            }
        } else if (sourceButton.getText().equals("Back")) {
            frame.dispose();
            if (this.userRole.equalsIgnoreCase("Manager")) {
                ManagerFunction managerfunction = new ManagerFunction();
                managerfunction.openManagerFunction(userName);
            } else if (this.userRole.equalsIgnoreCase("Staff")) {
                StaffFunction stafffunction = new StaffFunction();
                stafffunction.openStaffFunction(userName);
            } else {
                ResidentFunction residentfunction = new ResidentFunction();
                residentfunction.openResidentFunction(userName);
            }
        }
    }

    private JFrame frame;
    private JTextArea paymentReceiptArea;
    private JComboBox<String> blockComboBox, monthComboBox, roomTypeComboBox, typeComboBox;
    private JComboBox<Integer> floorComboBox, numberComboBox;
    private JComboBox<Object> yearComboBox;
    private JTextField receiptNumberField; // TextField for receipt number search
    private String userRole;
    private String residentAddress;
    private String userName;

    public void openViewAllPaymentReceipt(String username, String role, String address) {
        FileHandler.writeSystemLog("(" + role + ") " + username + " enter view all payments " + role + " function.");
        this.userName = username;
        this.userRole = role;
        this.residentAddress = address;

        frame = new JFrame("View All Payments");
        frame.setSize(800, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        frame.setLayout(new BorderLayout(10, 10));

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(70, 130, 180));
        JLabel headerLabel = new JLabel("View All Payments");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 22));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);

        JPanel contentPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        typePanel.add(new JLabel("Type:"));
        typeComboBox = new JComboBox<>(new String[]{"Payment", "Receipt"});
        typePanel.add(typeComboBox);

        contentPanel.add(typePanel);

        if (!userRole.equals("Resident")) {
            // Non-resident address selection
            JPanel addressPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            addressPanel.add(new JLabel("Block:"));
            blockComboBox = new JComboBox<>(new String[]{"A", "B", "C"});
            addressPanel.add(blockComboBox);

            addressPanel.add(new JLabel("Floor:"));
            floorComboBox = new JComboBox<>();
            for (int i = 1; i <= 12; i++) floorComboBox.addItem(i);
            addressPanel.add(floorComboBox);

            addressPanel.add(new JLabel("Number:"));
            numberComboBox = new JComboBox<>();
            for (int i = 1; i <= 20; i++) numberComboBox.addItem(i);
            addressPanel.add(numberComboBox);

            contentPanel.add(addressPanel);
        } else {
            // Resident-specific address display
            JPanel addressPanel = new JPanel();
            JLabel residentLabel = new JLabel("Your Address: " + residentAddress);
            residentLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            addressPanel.add(residentLabel);
            contentPanel.add(addressPanel);

            // Dynamically populate room type based on resident information
            roomTypeComboBox = new JComboBox<>();
            ArrayList<Resident> residents = FileHandler.allResidents; // Assuming resident data is stored here
            for (Resident resident : residents) {
                if (resident.getAddress().equalsIgnoreCase(residentAddress)) {
                    roomTypeComboBox.addItem(resident.getRoomType());
                    break; // Assuming each address maps to one resident and room type
                }
            }
        }

        JPanel roomTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        roomTypePanel.add(new JLabel("Room Type:"));
        if (userRole.equals("Resident")) {
            roomTypePanel.add(roomTypeComboBox); // Use dynamically updated combo box for residents
        } else {
            roomTypeComboBox = new JComboBox<>(new String[]{"All", "Small Room", "Normal Room", "Master Room"});
            roomTypePanel.add(roomTypeComboBox);
        }
        contentPanel.add(roomTypePanel);

        JPanel monthYearPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        monthYearPanel.add(new JLabel("Month:"));
        monthComboBox = new JComboBox<>(new String[]{"All", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"});
        monthYearPanel.add(monthComboBox);

        monthYearPanel.add(new JLabel("Year:"));
        yearComboBox = new JComboBox<>();
        yearComboBox.addItem("All"); // Add "All" for years
        for (int i = 2020; i <= 2030; i++) yearComboBox.addItem(i);
        monthYearPanel.add(yearComboBox);

        contentPanel.add(monthYearPanel);

        JPanel receiptNumberPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel numberLabel = new JLabel("Payment Number:");
        receiptNumberPanel.add(numberLabel);
        receiptNumberField = new JTextField(10);
        receiptNumberPanel.add(receiptNumberField);

        JButton searchByNumberButton = new JButton("Search by Payment Number");
        searchByNumberButton.addActionListener(this);
        receiptNumberPanel.add(searchByNumberButton);

        contentPanel.add(receiptNumberPanel);

        typeComboBox.addActionListener(e -> {
            String selectedType = (String) typeComboBox.getSelectedItem();
            if ("Receipt".equals(selectedType)) {
                numberLabel.setText("Receipt Number:");
                searchByNumberButton.setText("Search by Receipt Number");
            } else if ("Payment".equals(selectedType)) {
                numberLabel.setText("Payment Number:");
                searchByNumberButton.setText("Search by Payment Number");
            }
            contentPanel.revalidate();
            contentPanel.repaint();
        });

        paymentReceiptArea = new JTextArea();
        paymentReceiptArea.setEditable(false);
        paymentReceiptArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        paymentReceiptArea.setBackground(new Color(245, 245, 245));
        JScrollPane scrollPane = new JScrollPane(paymentReceiptArea);
        scrollPane.setPreferredSize(new Dimension(700, 250));
        contentPanel.add(scrollPane);

        JPanel buttonPanel = new JPanel();
        JButton searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.BOLD, 16));
        searchButton.setBackground(new Color(60, 179, 113));
        searchButton.setFocusPainted(false);
        searchButton.addActionListener(this);

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.setBackground(new Color(255, 69, 0));
        backButton.setFocusPainted(false);
        backButton.addActionListener(this);

        buttonPanel.add(searchButton);
        buttonPanel.add(backButton);

        contentPanel.add(buttonPanel);

        frame.add(headerPanel, BorderLayout.NORTH);
        frame.add(contentPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private void displayTransaction(String address, String month, String year, String roomType, String name, String role, String type) {
        boolean transactionFound = false;
        paymentReceiptArea.setText(""); // Clear previous content

        // Determine whether it's Receipt or Payment
        if (type.equalsIgnoreCase("Receipt")) {
            // Search in Receipts
            for (Receipt receipt : FileHandler.allReceipts) {
                // Role-based restriction: Residents can only view their own receipts
                if (role.equalsIgnoreCase("Resident") && !receipt.getResidentName().equalsIgnoreCase(name)) {
                    continue; // Skip receipts not belonging to the resident
                }

                // Check if the receipt matches the given criteria
                if ((month.equals("All") || receipt.getMonth().equalsIgnoreCase(month)) &&
                    (year.equals("All") || receipt.getYear() == Integer.parseInt(year)) &&
                    (roomType.equals("All") || receipt.getRoomType().equalsIgnoreCase(roomType)) &&
                    receipt.getAddress().equalsIgnoreCase(address)) {

                    // Mark receipt as found
                    transactionFound = true;

                    // Append receipt details to the text area
                    paymentReceiptArea.append("Receipt ID: " + receipt.getReceiptNumber() + "\n");
                    paymentReceiptArea.append("Address: " + receipt.getAddress() + "\n");
                    paymentReceiptArea.append("Room Type: " + receipt.getRoomType() + "\n");
                    paymentReceiptArea.append("Username: " + receipt.getResidentName() + "\n");
                    paymentReceiptArea.append("Month: " + receipt.getMonth() + "\n");
                    paymentReceiptArea.append("Year: " + receipt.getYear() + "\n");
                    paymentReceiptArea.append("Total Amount: " + String.format("%.2f", receipt.getTotalAmount()) + "\n");
                    paymentReceiptArea.append("Paid Amount: " + String.format("%.2f", receipt.getAmountPaid()) + "\n");
                    paymentReceiptArea.append("Unpaid Amount: " + String.format("%.2f", receipt.getRemainder()) + "\n");
                    paymentReceiptArea.append("Change Amount: " + String.format("%.2f", receipt.getChange()) + "\n");
                    paymentReceiptArea.append("Pay Date: " + receipt.getPaymentDate() + "\n");
                    paymentReceiptArea.append("Status: " + receipt.getStatus() + "\n");
                    paymentReceiptArea.append("\n----------------------------------------\n");
                }
            }
        } else if (type.equalsIgnoreCase("Payment")) {
            // Search in Payments (similar logic as above, assuming Payment class has similar properties)
            for (Payment payment : FileHandler.allPayments) {
                // Role-based restriction: Residents can only view their own payments
                if (role.equalsIgnoreCase("Resident") && !payment.getUserName().equalsIgnoreCase(name)) {
                    continue; // Skip payments not belonging to the resident
                }

                // Check if the payment matches the given criteria
                if ((month.equals("All") || payment.getMonth().equalsIgnoreCase(month)) &&
                    (year.equals("All") || payment.getYear() == Integer.parseInt(year)) &&
                    (roomType.equals("All") || payment.getRoomType().equalsIgnoreCase(roomType)) &&
                    payment.getAddress().equalsIgnoreCase(address)) {

                    // Mark payment as found
                    transactionFound = true;

                    // Append payment details to the text area
                    paymentReceiptArea.append("Payment ID: " + payment.getPaymentId() + "\n");
                    paymentReceiptArea.append("Address: " + payment.getAddress() + "\n");
                    paymentReceiptArea.append("Room Type: " + payment.getRoomType() + "\n");
                    paymentReceiptArea.append("Username: " + payment.getUserName() + "\n");
                    paymentReceiptArea.append("Month: " + payment.getMonth() + "\n");
                    paymentReceiptArea.append("Year: " + payment.getYear() + "\n");
                    paymentReceiptArea.append("Amount: " + String.format("%.2f", payment.getAmount()) + "\n");
                    paymentReceiptArea.append("Total Amount: " + String.format("%.2f", payment.getTotalAmount()) + "\n");
                    paymentReceiptArea.append("Status: " + payment.getStatus() + "\n");
                    paymentReceiptArea.append("\n----------------------------------------\n");
                }
            }
        }

        // Log the search action
        FileHandler.writeSystemLog("(" + role + ") " + userName + " searched " + type.toLowerCase() + " for " + address + "(Room Type: " + roomType + ")" + " when " + month + " month, " + year + " year.");

        // If no transactions were found, display a message
        if (!transactionFound) {
            paymentReceiptArea.setText("No " + type.toLowerCase() + " found for the selected criteria.");
        }
    }

    private void displayTransactionByNumber(String transactionNumber, String role, String type) {
        boolean transactionFound = false;
        paymentReceiptArea.setText(""); // Clear previous content

        // Determine whether it's Receipt or Payment
        if (type.equalsIgnoreCase("Receipt")) {
            // Search in Receipts
            for (Receipt receipt : FileHandler.allReceipts) {
                // Role-based restriction: Residents can only view their own receipts
                if (role.equalsIgnoreCase("Resident") && !receipt.getResidentName().equalsIgnoreCase(userName)) {
                    continue; // Skip receipts not associated with the current resident
                }

                if (receipt.getReceiptNumber().equalsIgnoreCase(transactionNumber)) {
                    transactionFound = true;
                    FileHandler.writeSystemLog("(" + role + ") " + userName + " searched payment receipt by RECEIPT NUMBER: " + transactionNumber + ".");

                    paymentReceiptArea.append("Receipt ID: " + receipt.getReceiptNumber() + "\n");
                    paymentReceiptArea.append("Address: " + receipt.getAddress() + "\n");
                    paymentReceiptArea.append("Room Type: " + receipt.getRoomType() + "\n");
                    paymentReceiptArea.append("Username: " + receipt.getResidentName() + "\n");
                    paymentReceiptArea.append("Month: " + receipt.getMonth() + "\n");
                    paymentReceiptArea.append("Year: " + receipt.getYear() + "\n");
                    paymentReceiptArea.append("Total Amount: " + String.format("%.2f", receipt.getTotalAmount()) + "\n");
                    paymentReceiptArea.append("Paid Amount: " + String.format("%.2f", receipt.getAmountPaid()) + "\n");
                    paymentReceiptArea.append("Unpaid Amount: " + String.format("%.2f", receipt.getRemainder()) + "\n");
                    paymentReceiptArea.append("Change Amount: " + String.format("%.2f", receipt.getChange()) + "\n");
                    paymentReceiptArea.append("Pay Date: " + receipt.getPaymentDate() + "\n");
                    paymentReceiptArea.append("Status: " + receipt.getStatus() + "\n");
                    paymentReceiptArea.append("\n----------------------------------------\n");
                    break;
                }
            }
        } else if (type.equalsIgnoreCase("Payment")) {
            // Search in Payments
            for (Payment payment : FileHandler.allPayments) {
                // Role-based restriction: Residents can only view their own payments
                if (role.equalsIgnoreCase("Resident") && !payment.getUserName().equalsIgnoreCase(userName)) {
                    continue; // Skip payments not associated with the current resident
                }

                if (String.valueOf(payment.getPaymentId()).equals(transactionNumber)) {
                    transactionFound = true;
                    FileHandler.writeSystemLog("(" + role + ") " + userName + " searched payment by PAYMENT NUMBER: " + transactionNumber + ".");

                    paymentReceiptArea.append("Receipt ID: " + payment.getPaymentId() + "\n");
                    paymentReceiptArea.append("Address: " + payment.getAddress() + "\n");
                    paymentReceiptArea.append("Room Type: " + payment.getRoomType() + "\n");
                    paymentReceiptArea.append("Username: " + payment.getUserName() + "\n");
                    paymentReceiptArea.append("Month: " + payment.getMonth() + "\n");
                    paymentReceiptArea.append("Year: " + payment.getYear() + "\n");
                    paymentReceiptArea.append("Amount: " + String.format("%.2f", payment.getAmount()) + "\n");
                    paymentReceiptArea.append("Total Amount: " + String.format("%.2f", payment.getTotalAmount()) + "\n");
                    paymentReceiptArea.append("Status: " + payment.getStatus() + "\n");
                    paymentReceiptArea.append("\n----------------------------------------\n");
                    break;
                }
            }
        }

        if (!transactionFound) {
            paymentReceiptArea.setText("No " + type.toLowerCase() + " found with the provided number.");
        }
    }
}
