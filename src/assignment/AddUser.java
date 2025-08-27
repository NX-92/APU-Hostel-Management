package assignment;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class AddUser implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String email = emailField.getText().trim();
            String contactNumber = ValidationUtils.formatContactNumber(contactNumberField.getText().trim());
            String role = (String) roleSelector.getSelectedItem();
            String address = "";
            String roomType = "";

            if ("Resident".equals(role)) {
                String block = (String) blockSelector.getSelectedItem();
                String floor = (String) floorSelector.getSelectedItem();
                String number = (String) numberSelector.getSelectedItem();
                roomType = (String) roomTypeSelector.getSelectedItem();
                address = block + "_" + floor + "_" + number;
            }

            if (!ValidationUtils.validateInput(username, password, email, contactNumber, role, address)) {
                return;
            }

            if (ValidationUtils.checkForDuplicates(username, email, contactNumber, address, roomType, role)) {
                return;
            }

            if (addUser(username, password, email, contactNumber, role, address, roomType)) {
                JOptionPane.showMessageDialog(frame, "Add User Successful!");
                FileHandler.writeSystemLog("(Manager) " + userName + " Add a new " + role + " user, Name: " + username + ".");
                frame.dispose();
                ManagerFunction managerfunction = new ManagerFunction();
                managerfunction.openManagerFunction(userName);
            } else {
                JOptionPane.showMessageDialog(frame, "Add Failed!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            frame.dispose(); // Close the current frame
            ManagerFunction managerfunction = new ManagerFunction();
            managerfunction.openManagerFunction(userName);
        }
    }

    private JFrame frame;
    private JPanel rightPanel;
    private JLabel usernameLabel, passwordLabel, emailLabel, contactNumberLabel, roleLabel, blockLabel, floorLabel, numberLabel, roomTypeLabel, paymentRateLabel;
    private JTextField usernameField, emailField, contactNumberField;
    private JPasswordField passwordField;
    private JComboBox<String> roleSelector, blockSelector, floorSelector, numberSelector, roomTypeSelector;
    private JButton submitButton, backButton;
    private JCheckBox showPasswordCheckbox;
    
    private String userName;

    public void openAddUser(String username) {
        this.userName = username;
        FileHandler.writeSystemLog("(Manager) " + userName + " Enter add user manager function.");
        frame = new JFrame("Add User Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 750);
        frame.setLayout(new BorderLayout());

        rightPanel = new JPanel(null);
        rightPanel.setBackground(Color.WHITE);
        createFormUI();

        frame.add(rightPanel, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void createFormUI() {
        usernameLabel = new JLabel("Username:");
        passwordLabel = new JLabel("Password:");
        emailLabel = new JLabel("Email:");
        contactNumberLabel = new JLabel("Contact Number:");
        roleLabel = new JLabel("Role:");
        blockLabel = new JLabel("Block:");
        floorLabel = new JLabel("Floor:");
        numberLabel = new JLabel("Number:");
        roomTypeLabel = new JLabel("Room Type:");

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        emailField = new JTextField();
        contactNumberField = new JTextField();

        roleSelector = new JComboBox<>(new String[]{"Resident", "Staff", "Manager"});
        roleSelector.addActionListener(e -> toggleAddressFields());

        blockSelector = new JComboBox<>(new String[]{"A", "B", "C"});
        floorSelector = new JComboBox<>();
        numberSelector = new JComboBox<>();
        roomTypeSelector = new JComboBox<>(new String[]{"Small Room", "Normal Room", "Master Room"});

        for (int i = 1; i <= 12; i++) {
            floorSelector.addItem(String.format("%02d", i));
        }
        for (int i = 1; i <= 20; i++) {
            numberSelector.addItem(String.format("%02d", i));
        }
        
        paymentRateLabel = new JLabel("Current Payment Rate: ");

        submitButton = new JButton("Submit");
        backButton = new JButton("Back");

        int y = 60;
        usernameLabel.setBounds(50, y, 100, 30);
        usernameField.setBounds(160, y, 250, 30);

        passwordLabel.setBounds(50, y += 40, 100, 30);
        passwordField.setBounds(160, y, 250, 30);
        
        showPasswordCheckbox = new JCheckBox("Show Password");
        showPasswordCheckbox.setBounds(160, y += 30, 250, 30);
        showPasswordCheckbox.setBackground(Color.WHITE); // Optional: match panel background
        showPasswordCheckbox.addActionListener(e -> {
            if (showPasswordCheckbox.isSelected()) {
                passwordField.setEchoChar((char) 0); // Show password
            } else {
                passwordField.setEchoChar('*'); // Mask password
            }
        });

        emailLabel.setBounds(50, y += 40, 100, 30);
        emailField.setBounds(160, y, 250, 30);

        contactNumberLabel.setBounds(50, y += 40, 120, 30);
        contactNumberField.setBounds(160, y, 250, 30);

        roleLabel.setBounds(50, y += 40, 100, 30);
        roleSelector.setBounds(160, y, 250, 30);

        blockLabel.setBounds(50, y += 40, 100, 30);
        blockSelector.setBounds(160, y, 250, 30);

        floorLabel.setBounds(50, y += 40, 100, 30);
        floorSelector.setBounds(160, y, 250, 30);

        numberLabel.setBounds(50, y += 40, 100, 30);
        numberSelector.setBounds(160, y, 250, 30);

        roomTypeLabel.setBounds(50, y += 40, 100, 30);
        roomTypeSelector.setBounds(160, y, 250, 30);
        
        paymentRateLabel.setBounds(50, y += 40, 250, 30); 

        submitButton.setBounds(100, y += 60, 100, 30);
        backButton.setBounds(220, y, 100, 30);

        rightPanel.add(usernameLabel);
        rightPanel.add(usernameField);
        rightPanel.add(passwordLabel);
        rightPanel.add(passwordField);
        rightPanel.add(emailLabel);
        rightPanel.add(emailField);
        rightPanel.add(contactNumberLabel);
        rightPanel.add(contactNumberField);
        rightPanel.add(roleLabel);
        rightPanel.add(roleSelector);
        rightPanel.add(blockLabel);
        rightPanel.add(blockSelector);
        rightPanel.add(floorLabel);
        rightPanel.add(floorSelector);
        rightPanel.add(numberLabel);
        rightPanel.add(numberSelector);
        rightPanel.add(roomTypeLabel);
        rightPanel.add(roomTypeSelector);
        rightPanel.add(paymentRateLabel);
        rightPanel.add(submitButton);
        rightPanel.add(backButton);
        rightPanel.add(showPasswordCheckbox);
        
        paymentRateLabel.setText("Current Payment Rate: " + Payment.getPaymentRateByRoomType("Small Room"));
        
        roomTypeSelector.addActionListener(e -> {
            String selectedRoom = (String) roomTypeSelector.getSelectedItem();
            Double paymentRate = Payment.getPaymentRateByRoomType(selectedRoom);
            paymentRateLabel.setText("Current Payment Rate: " + paymentRate);
        });

        submitButton.addActionListener(this);
        backButton.addActionListener(this);
        toggleAddressFields();
    }

    private void toggleAddressFields() {
        boolean isResident = "Resident".equals(roleSelector.getSelectedItem());
        blockLabel.setVisible(isResident);
        blockSelector.setVisible(isResident);
        floorLabel.setVisible(isResident);
        floorSelector.setVisible(isResident);
        numberLabel.setVisible(isResident);
        numberSelector.setVisible(isResident);
        roomTypeLabel.setVisible(isResident);
        roomTypeSelector.setVisible(isResident);
        paymentRateLabel.setVisible(isResident);
    }
    
    private boolean addUser(String username, String password, String email, String contactNumber, String role, String address, String roomType) {
        boolean isUserAdded = false;

        switch (role) {
            case "Resident":
                String currentMonth = LocalDate.now().getMonth().toString();
                int currentYear = LocalDate.now().getYear();
                
                int id = FileHandler.allPayments.size() + 10001;

                Payment p = new Payment(id, address, roomType, username, currentMonth, currentYear, Payment.getPaymentRateByRoomType(roomType), Payment.getPaymentRateByRoomType(roomType), "Unsuccessful");

                Resident newResident = new Resident(username, password, email, contactNumber, address, roomType);

                newResident.addPayment(p);
                FileHandler.allPayments.add(p);

                FileHandler.allResidents.add(newResident);

                FileHandler.writePaymentsToFile();
                
                isUserAdded = true;
                break;
                
            case "Staff":
                Staff newStaff = new Staff(username, password, email, contactNumber);
                FileHandler.allStaffs.add(newStaff);
                isUserAdded = true;
                break;
                
            case "Manager":
                Manager newManager = new Manager(username, password, email, contactNumber);
                FileHandler.allManagers.add(newManager);
                isUserAdded = true;
                break;
                
        }
        FileHandler.writeToAllFile();

        return isUserAdded;
    }
}