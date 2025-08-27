package assignment;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ProcessPayment implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == searchButton) {
            String selectedAddress = (String) addressComboBox.getSelectedItem();
            String selectedRoomType = (String) roomTypeComboBox.getSelectedItem(); // Get selected room type
            String selectedMonth = (String) monthComboBox.getSelectedItem();
            int selectedYear = (int) yearComboBox.getSelectedItem();
            double unpaidAmount = 0;
            double totalManagementFees = 0;
            boolean paymentFound = false;

            for (Resident resident : FileHandler.allResidents) {
                if (resident.getAddress().equalsIgnoreCase(selectedAddress) 
                        && resident.getRoomType().equalsIgnoreCase(selectedRoomType)) { // Match room type
                    for (Payment payment : resident.getPayments()) {
                        if (payment.getMonth().equalsIgnoreCase(selectedMonth)
                                && payment.getYear() == selectedYear
                                && (payment.getStatus().equalsIgnoreCase("Unsuccessful")
                                || payment.getStatus().equalsIgnoreCase("Partial"))) {
                            unpaidAmount += payment.getAmount();
                            totalManagementFees += payment.getTotalAmount();
                            paymentFound = true;
                        }
                    }
                }
            }

            if (paymentFound) {
                submitButton.setEnabled(true);
                unpaidAmountArea.setText("The remaining amount is: " + String.format("%.2f", unpaidAmount));
                unpaidAmountArea.append("\nTotal management fees for " + selectedMonth + " " + selectedYear + ":\n " + String.format("%.2f", totalManagementFees));
                paymentField.setEnabled(true);
            } else {
                unpaidAmountArea.setText("No unpaid amount found for the selected date and room type.");
                paymentField.setEnabled(false);
            }
        }
    }
    
    private JFrame frame;
    private JPanel mainPanel;
    private JComboBox<String> addressComboBox;
    private JComboBox<String> monthComboBox, roomTypeComboBox;
    private JComboBox<Integer> yearComboBox;
    private JTextArea unpaidAmountArea, unpaidPaymentsArea;
    private JTextField paymentField;
    private JButton searchButton, submitButton;
    
    private String userName;

    public void openProcessPayment(String username) {
        this.userName = username;
        FileHandler.writeSystemLog("(Staff) " + userName + " open process payment staff function.");
        frame = new JFrame("Process Payment");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 500);
        frame.setLocationRelativeTo(null);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));

        JPanel addressPanel = new JPanel(new BorderLayout(5, 5));
        JPanel addressRow = new JPanel(new BorderLayout());
        JLabel addressLabel = new JLabel("Select Address:");
        addressComboBox = new JComboBox<>();
        addressRow.add(addressLabel, BorderLayout.WEST);
        addressRow.add(addressComboBox, BorderLayout.CENTER);
        JTextArea selectedAddressArea = new JTextArea(3, 20);
        selectedAddressArea.setEditable(false);
        selectedAddressArea.setBorder(BorderFactory.createTitledBorder("Selected Address"));

        for (Resident resident : FileHandler.allResidents) {
            String address = resident.getAddress();
            if (!containsAddress(addressComboBox, address)) {
                addressComboBox.addItem(address);
            }
        }

        addressComboBox.addActionListener(e -> {
            String selectedAddress = (String) addressComboBox.getSelectedItem();
            selectedAddressArea.setText(selectedAddress);
            updateUnpaidPaymentsArea(selectedAddress, (String) roomTypeComboBox.getSelectedItem());
        });

        JPanel roomTypeRow = new JPanel(new BorderLayout());
        JLabel roomTypeLabel = new JLabel("Select Room Type:");
        roomTypeComboBox = new JComboBox<>(new String[]{"Small Room", "Normal Room", "Master Room"});
        roomTypeRow.add(roomTypeLabel, BorderLayout.WEST);
        roomTypeRow.add(roomTypeComboBox, BorderLayout.CENTER);
        roomTypeComboBox.addActionListener(e -> {
            String selectedAddress = (String) addressComboBox.getSelectedItem();
            String selectedRoomType = (String) roomTypeComboBox.getSelectedItem();
            updateUnpaidPaymentsArea(selectedAddress, selectedRoomType);
        });

        unpaidPaymentsArea = new JTextArea(5, 30);
        unpaidPaymentsArea.setEditable(false);
        unpaidPaymentsArea.setBorder(BorderFactory.createTitledBorder("Unpaid Payments"));

        addressPanel.add(addressRow, BorderLayout.NORTH);
        addressPanel.add(roomTypeRow, BorderLayout.SOUTH);

        JPanel monthYearPanel = new JPanel(new FlowLayout());
        JLabel monthLabel = new JLabel("Select Month:");
        monthComboBox = new JComboBox<>(new String[]{
            "January", "February", "March", "April", "May", "June", "July",
            "August", "September", "October", "November", "December"
        });

        JLabel yearLabel = new JLabel("Select Year:");
        yearComboBox = new JComboBox<>(new Integer[]{
            2020, 2021, 2022, 2023, 2024, 2025, 2026, 2027, 2028, 2029, 2030
        });

        monthYearPanel.add(monthLabel);
        monthYearPanel.add(monthComboBox);
        monthYearPanel.add(yearLabel);
        monthYearPanel.add(yearComboBox);

        JPanel paymentPanel = new JPanel(new BorderLayout(5, 5));
        searchButton = new JButton("Search Payment");
        unpaidAmountArea = new JTextArea(3, 20);
        unpaidAmountArea.setEditable(false);
        unpaidAmountArea.setBorder(BorderFactory.createTitledBorder("Unpaid Amount"));
        paymentField = new JTextField(10);
        paymentField.setBorder(BorderFactory.createTitledBorder("Enter Amount to Pay"));
        paymentField.setEnabled(false);

        paymentPanel.add(searchButton, BorderLayout.NORTH);
        paymentPanel.add(unpaidAmountArea, BorderLayout.CENTER);
        paymentPanel.add(paymentField, BorderLayout.SOUTH);

        searchButton.addActionListener(this);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        submitButton = new JButton("Submit");
        JButton cancelButton = new JButton("Cancel");

        submitButton.addActionListener(e -> processPayment());
        cancelButton.addActionListener(e -> {
            frame.dispose();
            new StaffFunction().openStaffFunction(username);
        });

        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(addressPanel, BorderLayout.NORTH);
        mainPanel.add(monthYearPanel, BorderLayout.CENTER);
        mainPanel.add(paymentPanel, BorderLayout.SOUTH);

        JPanel unpaidPanel = new JPanel(new BorderLayout());
        unpaidPanel.add(new JScrollPane(unpaidPaymentsArea), BorderLayout.CENTER);
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.add(unpaidPanel, BorderLayout.EAST);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
        submitButton.setEnabled(false);

        if (addressComboBox.getItemCount() > 0) {
            updateUnpaidPaymentsArea((String) addressComboBox.getItemAt(0), (String) roomTypeComboBox.getSelectedItem());
            selectedAddressArea.setText((String) addressComboBox.getItemAt(0));
        }
    }

    private void updateUnpaidPaymentsArea(String address, String roomType) {
        StringBuilder unpaidPaymentsInfo = new StringBuilder();
        for (Resident resident : FileHandler.allResidents) {
            if (resident.getAddress().equalsIgnoreCase(address) && resident.getRoomType().equalsIgnoreCase(roomType)) {
                for (Payment payment : resident.getPayments()) {
                    if (!payment.getStatus().equalsIgnoreCase("Successful")) {
                        unpaidPaymentsInfo.append("Month: ").append(payment.getMonth())
                                .append(", Year: ").append(payment.getYear())
                                .append(", Amount: ").append(String.format("%.2f", payment.getAmount()))
                                .append(", Status: ").append(payment.getStatus())
                                .append("\n");
                    }
                }
            }
        }

        if (unpaidPaymentsInfo.length() == 0) {
            unpaidPaymentsInfo.append("No unpaid payments found for this address and room type.");
        }

        unpaidPaymentsArea.setText(unpaidPaymentsInfo.toString());
    }

    private void processPayment() {
        try {
            String selectedAddress = (String) addressComboBox.getSelectedItem();
            String selectedRoomType = (String) roomTypeComboBox.getSelectedItem();
            String selectedMonth = (String) monthComboBox.getSelectedItem();
            int selectedYear = (int) yearComboBox.getSelectedItem();
            double enteredAmount = Double.parseDouble(paymentField.getText());

            for (Resident resident : FileHandler.allResidents) {
                if (resident.getAddress().equals(selectedAddress) && resident.getRoomType().equals(selectedRoomType)) {
                    for (Payment payment : resident.getPayments()) {
                        double oldPaymentAmount = payment.getAmount();
                        if (payment.getMonth().equalsIgnoreCase(selectedMonth) && payment.getYear() == selectedYear) {
                            if (enteredAmount >= payment.getAmount()) {
                                JOptionPane.showMessageDialog(frame, "Payment successful! Change: " + (enteredAmount - payment.getAmount()), "Success", JOptionPane.INFORMATION_MESSAGE);
                                FileHandler.writeSystemLog("(Staff) " + userName + " process a payment for " + resident.getAddress() + ", " + resident.getRoomType() + " and the amount is " + payment.getAmount() + ", the total amount of payment is " + payment.getTotalAmount() + " and the change is 0.");
                                payment.setAmount(0);
                                payment.setStatus("Successful");
                                generateReceipt(resident, payment, enteredAmount, (enteredAmount - oldPaymentAmount));
                            } else {
                                payment.setAmount(payment.getAmount() - enteredAmount);
                                JOptionPane.showMessageDialog(frame, "Partial payment made. Remaining amount: " + payment.getAmount(), "Partial Payment", JOptionPane.WARNING_MESSAGE);
                                FileHandler.writeSystemLog("(Staff) " + userName + " process a payment for " + resident.getAddress() + ", " + resident.getRoomType() + " and the amount is " + payment.getAmount() + ", the total amount of payment is " + payment.getTotalAmount() + " and the change is " + (enteredAmount - oldPaymentAmount) + ".");
                                payment.setStatus("Partial");
                                generateReceipt(resident, payment, enteredAmount, 0);
                            }

                            FileHandler.writePaymentsToFile();
                            unpaidAmountArea.setText("No unpaid amount found for the selected date and room type.");
                            updateUnpaidPaymentsArea(selectedAddress, selectedRoomType);
                            paymentField.setText("");
                            paymentField.setEnabled(false);
                            submitButton.setEnabled(false);
                        }
                    }
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Invalid payment amount entered.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateReceipt(Resident resident, Payment payment, double paidAmount, double paymentChange) {
        String address = resident.getAddress();
        String roomType = resident.getRoomType();
        String residentName = resident.getName();
        String month = payment.getMonth();
        int year = payment.getYear();
        double totalAmount = payment.getTotalAmount();
        double remainder = payment.getAmount();
        double change = paymentChange;
        LocalDate paymentDate = LocalDate.now();
        String status = payment.getStatus();

        Receipt receipt = new Receipt(address, roomType, residentName, month, year, totalAmount, paidAmount, remainder, change, paymentDate, status);

        resident.addReceipt(receipt);
        FileHandler.allReceipts.add(receipt);

        FileHandler.writeReceiptsToFile();
        
        FileHandler.writeSystemLog("(System) " + userName + " process a payment and generate a receipt for " + resident.getAddress() + ", " + resident.getRoomType() + " and the receipt number is " + receipt.getReceiptNumber() + ".");
        
        try {
            saveAndOpenReceipt(receipt);
            JOptionPane.showMessageDialog(null, "Receipt has been generated and saved as txt file.", "Receipt Generated", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving or opening receipt: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
        private void saveAndOpenReceipt(Receipt receipt) throws IOException {
        // Generate receipt content as plain text
        String content = """
            APU Hostel Management Fees Management System
            ------------------------------
                       RECEIPT
            ------------------------------
            Resident Name: %s
            Address: %s
            Room Type: %s
            Month: %s
            Year: %d
            Total Amount: $%.2f
            Paid Amount: $%.2f
            Remainder: $%.2f
            Change: $%.2f
            Payment Date: %s
            Status: %s
            ------------------------------
            Thank you for your payment!
            """.formatted(
                receipt.getResidentName(),
                receipt.getAddress(),
                receipt.getRoomType(),
                receipt.getMonth(),
                receipt.getYear(),
                receipt.getTotalAmount(),
                receipt.getAmountPaid(),
                receipt.getRemainder(),
                receipt.getChange(),
                receipt.getPaymentDate(),
                receipt.getStatus()
        );

        // Specify the file path
        String directoryPath = "D:\\DD"; // Change this to your desired directory
        File directory = new File(directoryPath);

        // Ensure the directory exists
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new IOException("Failed to create directory: " + directoryPath);
            }
        }

        // Save the file as a .txt file
        String fileName = receipt.getReceiptNumber()+ ".txt";
        File file = new File(directory, fileName);

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }

        // Automatically open the file
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(file);
        } else {
            JOptionPane.showMessageDialog(null, "Desktop is not supported. Please open the file manually.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
        
    private boolean containsAddress(JComboBox<String> comboBox, String address) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            if (comboBox.getItemAt(i).equals(address)) {
                return true;
            }
        }
        return false;
    }
}
