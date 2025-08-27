package assignment;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ModifyDeletePaymentReceipt implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == searchButton) {
            String type = (String) typeComboBox.getSelectedItem();

            String block = (String) blockComboBox.getSelectedItem();
            String floor = (String) floorComboBox.getSelectedItem();
            String number = (String) numberComboBox.getSelectedItem();
            String address = block + "_" + floor + "_" + number;
            String roomType = (String) roomTypeComboBox.getSelectedItem();
            
            reloadItemNumberComboBox(type, address, roomType);

        } else if (source == modifyButton) {
            String selectedItem = (String) itemNumberComboBox.getSelectedItem();
            if (selectedItem != null) {
                openModifyGUI(selectedItem);
            }
        } else if (source == deleteButton) {
            
            String type = (String) typeComboBox.getSelectedItem();

            String block = (String) blockComboBox.getSelectedItem();
            String floor = (String) floorComboBox.getSelectedItem();
            String number = (String) numberComboBox.getSelectedItem();
            String address = block + "_" + floor + "_" + number;
            String roomType = (String) roomTypeComboBox.getSelectedItem();
            
            String selectedItem = (String) itemNumberComboBox.getSelectedItem();
            if (selectedItem != null) {
                int choice = JOptionPane.showConfirmDialog(frame, 
                        "Are you sure you want to delete " + selectedItem + "?", 
                        "Confirm Deletion", JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                switch (type) {
                    case "Payments":
                        for (Payment payment : FileHandler.allPayments) {
                            if (String.valueOf(payment.getPaymentId()).equalsIgnoreCase(selectedItem)) {
                                FileHandler.allPayments.remove(payment);
                                break;
                            }
                        }
                        int idCounter = 10001;
                        for (Payment payment : FileHandler.allPayments) {
                            payment.setPaymentId(idCounter++);
                        }
                        break;

                    case "Receipts":
                        for (Receipt receipt : FileHandler.allReceipts) {
                            if (receipt.getReceiptNumber().equals(selectedItem)) {
                                FileHandler.allReceipts.remove(receipt);
                                break;
                            }
                        }
                        // Recount Receipt IDs
                        idCounter = 10001;
                        for (Receipt receipt : FileHandler.allReceipts) {
                            receipt.setReceiptNumber(String.valueOf("REC" + idCounter++));
                        }
                        break;
                }
                
                FileHandler.writeToAllFile();
                FileHandler.writePaymentsToFile();
                FileHandler.writeReceiptsToFile();
                FileHandler.read();
                reloadItemNumberComboBox(type, address, roomType);
                FileHandler.writeSystemLog("(Manager) " + userName + " delete a " + type + ": " + selectedItem);
                resultArea.setText(selectedItem + " has been deleted.");
                modifyButton.setEnabled(false);
                deleteButton.setEnabled(false);
            }
            }
        } else if (source == cancelButton) {
            frame.dispose();
            ManagerFunction managerfunction = new ManagerFunction();
            managerfunction.openManagerFunction(userName);
        }

        if (itemNumberComboBox.getSelectedItem() != null) {
            modifyButton.setEnabled(true);
            deleteButton.setEnabled(true);
        } else {
            modifyButton.setEnabled(false);
            deleteButton.setEnabled(false);
        }
    }
    
    private JFrame frame;
    private JPanel panel;
    private JComboBox<String> typeComboBox, blockComboBox, floorComboBox, numberComboBox, itemNumberComboBox, roomTypeComboBox;
    private JTextArea resultArea;
    private JButton searchButton, modifyButton, deleteButton, cancelButton;
    private String userName;

    public void openModifyDeletePaymentReceipt(String username) {
        FileHandler.writeSystemLog("(Manager) " + username + " enter modify/delete payment receipt manager function.");
        
        this.userName = username;
        
        frame = new JFrame("Modify/Delete Payment/Receipt");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLayout(new BorderLayout());

        panel = new JPanel(new GridLayout(8, 2, 10, 10)); // Increased row count for cancel button

        panel.add(new JLabel("Type:"));
        typeComboBox = new JComboBox<>(new String[] {"Payments", "Receipts"});
        panel.add(typeComboBox);

        panel.add(new JLabel("Block:"));
        blockComboBox = new JComboBox<>(new String[] {"A", "B", "C"});
        panel.add(blockComboBox);

        panel.add(new JLabel("Floor:"));
        String[] floors = new String[12];
        for (int i = 0; i < 12; i++) {
            floors[i] = String.format("%02d", i + 1);
        }
        floorComboBox = new JComboBox<>(floors);
        panel.add(floorComboBox);

        panel.add(new JLabel("Number:"));
        String[] numbers = new String[20];
        for (int i = 0; i < 20; i++) {
            numbers[i] = String.format("%02d", i + 1);
        }
        numberComboBox = new JComboBox<>(numbers);
        panel.add(numberComboBox);
        
        panel.add(new JLabel("Room Type:"));
        String[] roomTypes = new String[] {"Small Room", "Normal Room", "Master Room"};  // Adjust options as needed
        roomTypeComboBox = new JComboBox<>(roomTypes);
        panel.add(roomTypeComboBox);
        
    ActionListener disableButtonsListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Disable buttons when any address combo box is changed
            modifyButton.setEnabled(false);
            deleteButton.setEnabled(false);
        }
    };

    blockComboBox.addActionListener(disableButtonsListener);
    floorComboBox.addActionListener(disableButtonsListener);
    numberComboBox.addActionListener(disableButtonsListener);
    roomTypeComboBox.addActionListener(disableButtonsListener);
    typeComboBox.addActionListener(disableButtonsListener);

        panel.add(new JLabel("Item Number:"));
        itemNumberComboBox = new JComboBox<>();
        panel.add(itemNumberComboBox);

        searchButton = new JButton("Search");
        searchButton.addActionListener(this);
        panel.add(searchButton);

        resultArea = new JTextArea(5, 30);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        modifyButton = new JButton("Modify");
        modifyButton.setEnabled(false); // Initially disabled
        modifyButton.addActionListener(this);

        deleteButton = new JButton("Delete");
        deleteButton.setEnabled(false); // Initially disabled
        deleteButton.addActionListener(this);

        panel.add(modifyButton);
        panel.add(deleteButton);

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                ManagerFunction managerfunction = new ManagerFunction();
                managerfunction.openManagerFunction(username);
            }
        });
        panel.add(cancelButton); // Add the cancel button to the panel

        frame.add(panel, BorderLayout.NORTH);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void openModifyGUI(String itemNumber) {
        // Determine the selected type
        String type = (String) typeComboBox.getSelectedItem();
        frame.dispose();

        if ("Payments".equalsIgnoreCase(type)) {
            openModifyPaymentGUI(itemNumber);
        } else if ("Receipts".equalsIgnoreCase(type)) {
            openModifyReceiptGUI(itemNumber);
        }
    }

    private void openModifyPaymentGUI(String paymentId) {
        JFrame modifyFrame = new JFrame("Modify Payment: " + paymentId);
        modifyFrame.setSize(400, 400);
        modifyFrame.setLayout(new GridLayout(8, 2, 10, 10));

        Payment payment = getPaymentByID(paymentId);

        modifyFrame.add(new JLabel("Address:"));
        JTextField addressField = new JTextField(payment.getAddress());
        addressField.setEditable(false);
        modifyFrame.add(addressField);
        
        modifyFrame.add(new JLabel("Room Type:"));
        JTextField roomTypeField = new JTextField(payment.getRoomType());
        roomTypeField.setEditable(false);
        modifyFrame.add(roomTypeField);

        modifyFrame.add(new JLabel("Month:"));
        JComboBox<String> monthComboBox = new JComboBox<>(new String[]{
            "JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE",
            "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"
        });
        monthComboBox.setSelectedItem(payment.getMonth());
        modifyFrame.add(monthComboBox);

        modifyFrame.add(new JLabel("Year:"));
        JComboBox<Integer> yearComboBox = new JComboBox<>();
        for (int year = 2020; year <= 2030; year++) {
            yearComboBox.addItem(year);
        }
        yearComboBox.setSelectedItem(payment.getYear());
        modifyFrame.add(yearComboBox);

        modifyFrame.add(new JLabel("Amount:"));
        JTextField amountField = new JTextField(String.valueOf(payment.getAmount()));
        modifyFrame.add(amountField);

        modifyFrame.add(new JLabel("Total Amount:"));
        JTextField totalAmountField = new JTextField(String.valueOf(payment.getTotalAmount()));
        modifyFrame.add(totalAmountField);

        modifyFrame.add(new JLabel("Status:"));
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"Unsuccessful", "Partial", "Successful"});
        statusComboBox.setSelectedItem(payment.getStatus());
        modifyFrame.add(statusComboBox);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            payment.setMonth((String) monthComboBox.getSelectedItem());
            payment.setYear((Integer) yearComboBox.getSelectedItem());
            payment.setAmount(Double.parseDouble(amountField.getText()));
            payment.setTotalAmount(Double.parseDouble(totalAmountField.getText()));
            payment.setStatus((String) statusComboBox.getSelectedItem());
            
            FileHandler.writePaymentsToFile();

            JOptionPane.showMessageDialog(modifyFrame, "Payment updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            FileHandler.writeSystemLog("(Manager) " + userName + " change payment, " + payment.getPaymentId());
            modifyFrame.dispose();
            openModifyDeletePaymentReceipt(userName);
        });
        modifyFrame.add(saveButton);
        
        payment.setRoomType((String) roomTypeComboBox.getSelectedItem());

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            modifyFrame.dispose();
            openModifyDeletePaymentReceipt(userName);
        });
        modifyFrame.add(cancelButton);

        modifyFrame.setLocationRelativeTo(frame);
        modifyFrame.setVisible(true);
    }

    private void openModifyReceiptGUI(String receiptNumber) {
        JFrame modifyFrame = new JFrame("Modify Receipt: " + receiptNumber);
        modifyFrame.setSize(400, 600);
        modifyFrame.setLayout(new GridLayout(12, 2, 10, 10));

        // Retrieve the receipt details (you need to implement the search logic)
        Receipt receipt = getReceiptByNumber(receiptNumber);

        // Address (non-editable)
        modifyFrame.add(new JLabel("Address:"));
        JTextField addressField = new JTextField(receipt.getAddress());
        addressField.setEditable(false);
        modifyFrame.add(addressField);
        
        // Room Type (non-editable)
        modifyFrame.add(new JLabel("Room Type:"));
        JTextField roomTypeField = new JTextField(receipt.getRoomType());
        roomTypeField.setEditable(false);
        modifyFrame.add(roomTypeField);

        // Resident Name
        modifyFrame.add(new JLabel("Resident Name:"));
        JTextField residentNameField = new JTextField(receipt.getResidentName());
        modifyFrame.add(residentNameField);

        // Month (ComboBox)
        modifyFrame.add(new JLabel("Month:"));
        JComboBox<String> monthComboBox = new JComboBox<>(new String[]{
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        });
        monthComboBox.setSelectedItem(receipt.getMonth());
        modifyFrame.add(monthComboBox);

        // Year (ComboBox)
        modifyFrame.add(new JLabel("Year:"));
        JComboBox<Integer> yearComboBox = new JComboBox<>();
        for (int year = 2020; year <= 2030; year++) {
            yearComboBox.addItem(year);
        }
        yearComboBox.setSelectedItem(receipt.getYear());
        modifyFrame.add(yearComboBox);

        // Total Amount
        modifyFrame.add(new JLabel("Total Amount:"));
        JTextField totalAmountField = new JTextField(String.valueOf(receipt.getTotalAmount()));
        modifyFrame.add(totalAmountField);

        // Amount Paid
        modifyFrame.add(new JLabel("Amount Paid:"));
        JTextField amountPaidField = new JTextField(String.valueOf(receipt.getAmountPaid()));
        modifyFrame.add(amountPaidField);

        // Remainder
        modifyFrame.add(new JLabel("Remainder:"));
        JTextField remainderField = new JTextField(String.valueOf(receipt.getRemainder()));
        modifyFrame.add(remainderField);

        // Change
        modifyFrame.add(new JLabel("Change:"));
        JTextField changeField = new JTextField(String.valueOf(receipt.getChange()));
        modifyFrame.add(changeField);

        // Payment Date (non-editable)
        modifyFrame.add(new JLabel("Payment Date:"));
        JTextField paymentDateField = new JTextField(receipt.getPaymentDate().toString());
        paymentDateField.setEditable(false);
        modifyFrame.add(paymentDateField);

        // Status (ComboBox)
        modifyFrame.add(new JLabel("Status:"));
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"Unsuccessful", "Partial", "Successful"});
        statusComboBox.setSelectedItem(receipt.getStatus());
        modifyFrame.add(statusComboBox);

        // Save Button
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            // Save updated receipt details (you need to implement the update logic)
            receipt.setResidentName(residentNameField.getText());
            receipt.setMonth((String) monthComboBox.getSelectedItem());
            receipt.setYear((Integer) yearComboBox.getSelectedItem());
            receipt.setTotalAmount(Double.parseDouble(totalAmountField.getText()));
            receipt.setAmountPaid(Double.parseDouble(amountPaidField.getText()));
            receipt.setRemainder(Double.parseDouble(remainderField.getText()));
            receipt.setChange(Double.parseDouble(changeField.getText()));
            receipt.setStatus((String) statusComboBox.getSelectedItem());
            
            FileHandler.writeReceiptsToFile();

            JOptionPane.showMessageDialog(modifyFrame, "Receipt updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            FileHandler.writeSystemLog("(Manager) " + userName + " change receipt, " + receipt.getReceiptNumber());
        
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

            String directoryPath = "D:\\DD";
            File directory = new File(directoryPath);

            if (!directory.exists() && !directory.mkdirs()) {
                JOptionPane.showMessageDialog(modifyFrame, "Failed to create directory.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String fileName = receipt.getReceiptNumber() + ".txt";
            File file = new File(directory, fileName);

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(content);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(modifyFrame, "Failed to write file.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                if (Desktop.isDesktopSupported()) {
                    JOptionPane.showMessageDialog(modifyFrame, "New receipt has been generated.");
                    Desktop.getDesktop().open(file);
                } else {
                    JOptionPane.showMessageDialog(modifyFrame, "Desktop is not supported. Please open the file manually.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(modifyFrame, "Failed to open file.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            modifyFrame.dispose();
            openModifyDeletePaymentReceipt(userName);
        });
        modifyFrame.add(saveButton);

        // Cancel Button
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            modifyFrame.dispose();
            openModifyDeletePaymentReceipt(userName);
        });
        modifyFrame.add(cancelButton);

        modifyFrame.setLocationRelativeTo(frame);
        modifyFrame.setVisible(true);
    }
    
    private Payment getPaymentByID(String paymentID) {
        for (Payment payment : FileHandler.allPayments) {
            if (String.valueOf(payment.getPaymentId()).equalsIgnoreCase(paymentID)) {
                return payment;
            }
        }
        return null; // Return null if no matching payment is found
    }
    
    private Receipt getReceiptByNumber(String receiptNumber) {
        for (Receipt receipt : FileHandler.allReceipts) {
            if (receipt.getReceiptNumber().equalsIgnoreCase(receiptNumber)) {
                return receipt;
            }
        }
        return null; // Return null if no matching receipt is found
    }
    
    private void reloadItemNumberComboBox(String type, String address, String roomType) {
        // Load items (Payments or Receipts) associated with this address
        itemNumberComboBox.removeAllItems();
        if (type.equalsIgnoreCase("Payments")) {
            boolean foundPayments = false;
            for (Payment payment : FileHandler.allPayments) {
                if (payment.getAddress().equalsIgnoreCase(address) && payment.getRoomType().equalsIgnoreCase(roomType)) {
                    itemNumberComboBox.addItem(String.valueOf(payment.getPaymentId()));
                    foundPayments = true;
                    }
                }
            
            if (foundPayments) {
                resultArea.setText("Payments loaded for address: " + address);
            } else {
                resultArea.setText("No payments found for address: " + address);
            }
        } else if (type.equalsIgnoreCase("Receipts")) {
            boolean foundReceipts = false;
            for (Receipt receipt : FileHandler.allReceipts) {
                if (receipt.getAddress().equalsIgnoreCase(address) && receipt.getRoomType().equalsIgnoreCase(roomType)) {
                    itemNumberComboBox.addItem(receipt.getReceiptNumber());
                    foundReceipts = true;
                }
            }
            if (foundReceipts) {
                resultArea.setText("Receipts loaded for address: " + address);
            } else {
                resultArea.setText("No receipts found for address: " + address);
            }
        }
    }
}
