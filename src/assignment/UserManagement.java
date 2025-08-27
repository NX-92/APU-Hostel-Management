package assignment;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class UserManagement implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == searchButton) {
            String role = (String) roleComboBox.getSelectedItem();
            String username = searchField.getText().trim();
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(searchFrame, "Please enter a username.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            searchUser(username, role);
        } else if (source == modifyButton) {
            searchFrame.dispose();
            modifyUser(resultArea.getText(), resultArea, searchField);
        } else if (source == deleteButton) {
            deleteUser(resultArea.getText(), (String) roleComboBox.getSelectedItem());
        } else if (e.getActionCommand().equals("Cancel")) {
            searchFrame.dispose();
            ManagerFunction managerfunction = new ManagerFunction();
            managerfunction.openManagerFunction(userName);
        }
    }
    
    private JFrame searchFrame;
    private JTextArea resultArea;
    private JButton modifyButton;
    private JButton deleteButton;
    private JComboBox<String> roleComboBox;
    private JTextField searchField;
    private JButton searchButton;
    private JPanel actionPanel;
    private String userName;

    public void openSearchUser(String username) {
        this.userName = username;
        FileHandler.writeSystemLog("(Manager) " + userName + " enter into user management manager function.");
        searchFrame = new JFrame("Search/Update/Delete User");
        searchFrame.setSize(600, 600);
        searchFrame.setLayout(new BorderLayout());

        // Create role combo box
        String[] roles = {"Resident", "Staff", "Manager"};
        roleComboBox = new JComboBox<>(roles);
        roleComboBox.setSelectedIndex(0);

        // Create search field and button
        searchField = new JTextField();
        searchButton = new JButton("Search");

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.add(roleComboBox, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        searchPanel.setBackground(new Color(240, 240, 255));  // Light blue background

        // Result area
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setBackground(new Color(255, 255, 255)); // White background
        resultArea.setForeground(new Color(0, 0, 0)); // Black text color

        actionPanel = new JPanel();
        modifyButton = new JButton("Modify");
        deleteButton = new JButton("Delete");
        JButton cancelButton = new JButton("Cancel");

        // Set action panel background
        actionPanel.setBackground(new Color(200, 200, 255)); // Light purple background

        actionPanel.add(modifyButton);
        actionPanel.add(deleteButton);
        actionPanel.add(cancelButton);

        modifyButton.setEnabled(false);
        deleteButton.setEnabled(false);

        // Action listeners
        searchButton.addActionListener(this);
        modifyButton.addActionListener(this);
        deleteButton.addActionListener(this);
        cancelButton.addActionListener(this);

        searchFrame.add(searchPanel, BorderLayout.NORTH);
        searchFrame.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        searchFrame.add(actionPanel, BorderLayout.SOUTH);
        searchFrame.setVisible(true);
        
        searchFrame.setLocationRelativeTo(null);
        
        // Add ActionListener to roleComboBox
        roleComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedRole = (String) roleComboBox.getSelectedItem();
                loadUser(selectedRole); // Call loadUser with the selected role
                modifyButton.setEnabled(false);
                deleteButton.setEnabled(false);
            }
        });
        loadUser("Resident");
    }

    private void searchUser(String username, String role) {
        resultArea.setText(""); // Clear the result area
        modifyButton.setEnabled(false);
        deleteButton.setEnabled(false);

        User foundUser = null;

        switch (role) {
            case "Resident":
                for (Resident resident : FileHandler.allResidents) {
                    if (resident.getName().equals(username)) {
                        foundUser = resident;
                        break;
                    }
                }
                break;
            case "Staff":
                for (Staff staff : FileHandler.allStaffs) {
                    if (staff.getName().equals(username)) {
                        foundUser = staff;
                        break;
                    }
                }
                break;
            case "Manager":
                for (Manager manager : FileHandler.allManagers) {
                    if (manager.getName().equals(username)) {
                        foundUser = manager;
                        break;
                    }
                }
                break;
        }

        if (foundUser != null) {
            resultArea.setText(formatUserDetails(foundUser));
            FileHandler.writeSystemLog("(Manager) " + userName + " search " + foundUser.getRole()+ ", " + foundUser.getName());
            modifyButton.setEnabled(true);
            deleteButton.setEnabled(true);
        } else {
            resultArea.setText("User not found.");
        }
    }

    // Format user details for display
    private String formatUserDetails(User user) {
        if (user instanceof Resident) {
            Resident resident = (Resident) user;
            return String.format("Username: %s\nPassword: %s\nEmail: %s\nContact Number: %s\nRole: Resident\nAddress: %s\nroomType: %s",
                    resident.getName(), resident.getPassword(), resident.getEmailAddress(), resident.getContactNumber(), resident.getAddress(), resident.getRoomType());
        } else {
            return String.format("Username: %s\nPassword: %s\nEmail: %s\nContact Number: %s\nRole: %s",
                    user.getName(), user.getPassword(), user.getEmailAddress(), user.getContactNumber(),
                    (user instanceof Staff) ? "Staff" : "Manager");
        }
    }
    
    private void loadUser(String role) {
        resultArea.setText("");
        switch (role) {
            case "Resident":
                for (Resident resident : FileHandler.allResidents) {
                    resultArea.append(formatUserDetails(resident));
                    resultArea.append("\n-----------------------------------------------\n");
                }
                break;

            case "Staff":
                for (Staff staff : FileHandler.allStaffs) {
                    resultArea.append(formatUserDetails(staff));
                    resultArea.append("\n-----------------------------------------------\n");
                }
                break;

            case "Manager":
                for (Manager manager : FileHandler.allManagers) {
                    resultArea.append(formatUserDetails(manager));
                    resultArea.append("\n-----------------------------------------------\n");
                }
                break;

            default:
                resultArea.append("Invalid role specified.\n");
        }
    }

    private void deleteUser(String userInfo, String role) {
        int choice = JOptionPane.showConfirmDialog(null,
                "Are you sure you want to delete this user?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (choice != JOptionPane.YES_OPTION) return;

        ArrayList<?> userList;
        switch (role) {
            case "Resident":
                userList = FileHandler.allResidents;
                break;
            case "Staff":
                userList = FileHandler.allStaffs;
                break;
            case "Manager":
                userList = FileHandler.allManagers;
                break;
            default:
                JOptionPane.showMessageDialog(null, "Invalid role.");
                return;
        }

        // Extract username from the userInfo string
        String username = userInfo.split("\n")[0].split(": ")[1];
        String address = userInfo.split("\n")[5].split(": ")[1];
        String roomType = userInfo.split("\n")[6].split(": ")[1];

        // Remove user from list
        userList.removeIf(user -> ((User) user).getName().equals(username));

        // Delete payment records associated with the user
        deleteUserPayments(address, roomType);

        // Update the corresponding role file
        FileHandler.writeToRoleFile(role);
        FileHandler.read();

        JOptionPane.showMessageDialog(null, "User and their payment records have been deleted.");
        FileHandler.writeSystemLog("(Manager) " + userName + " delete " + role + ", " + username);
        resultArea.setText("");
        modifyButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

    private void deleteUserPayments(String address, String roomType) {
        // Create a new list to store payments to keep
        ArrayList<Payment> updatedPayments = new ArrayList<>();

        for (Payment payment : FileHandler.allPayments) {
            if (!(payment.getAddress().equalsIgnoreCase(address) && payment.getRoomType().equalsIgnoreCase(roomType))) {
                updatedPayments.add(payment);
            }
        }

        // Replace the old list with the new list
        FileHandler.allPayments = updatedPayments;

        // Update payment IDs
        int paymentId = 10001;
        for (Payment payment : FileHandler.allPayments) {
            payment.setPaymentId(paymentId++);
        }

        // Update the payment file
        FileHandler.writePaymentsToFile();
    }

    private void modifyUser(String userDetails, JTextArea resultArea, JTextField searchField) {
        if (userDetails.equals("User not found.") || userDetails.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No user selected for modification!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFrame modifyFrame = new JFrame("Modify User Information");
        modifyFrame.setSize(400, 500); // Adjusted size to fit more components
        modifyFrame.setLayout(new GridLayout(9, 2, 10, 10)); // Increased rows for the new Room Type combo box

        String[] userDetailsArray = userDetails.split("\n");
        String oldName = userDetailsArray[0].split(": ")[1];
        String oldPassword = userDetailsArray[1].split(": ")[1];
        String oldEmail = userDetailsArray[2].split(": ")[1];
        String oldContact = userDetailsArray[3].split(": ")[1];
        String role = userDetailsArray[4].split(": ")[1];

        // Optional: Add address-related fields for Residents
        String oldAddress = role.equals("Resident") && userDetailsArray.length > 5 ? userDetailsArray[5].split(": ")[1] : "";
        String oldRoomType = role.equals("Resident") && userDetailsArray.length > 6 ? userDetailsArray[6].split(": ")[1] : "";

        // Labels and text fields for Name, Password, Email, and Contact Number
        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(oldName);

        JLabel passwordLabel = new JLabel("Password:");
        JTextField passwordField = new JTextField(oldPassword);

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField(oldEmail);

        JLabel contactLabel = new JLabel("Contact Number:");
        JTextField contactField = new JTextField(oldContact);

        // JComboBoxes for Resident's address-related fields
        JLabel blockLabel = new JLabel("Block:");
        JComboBox<String> blockComboBox = new JComboBox<>(new String[]{"A", "B", "C"});
        blockComboBox.setSelectedItem(oldAddress.split("_")[0]);

        JLabel floorLabel = new JLabel("Floor:");
        JComboBox<String> floorComboBox = new JComboBox<>();
        for (int i = 1; i <= 12; i++) {
            floorComboBox.addItem(String.format("%02d", i));
        }

        JLabel numberLabel = new JLabel("Number:");
        JComboBox<String> numberComboBox = new JComboBox<>();
        for (int i = 1; i <= 20; i++) {
            numberComboBox.addItem(String.format("%02d", i));
        }

        JLabel roomTypeLabel = new JLabel("Room Type:");
        JComboBox<String> roomTypeComboBox = new JComboBox<>(new String[]{"Small Room", "Normal Room", "Master Room"});
        roomTypeComboBox.setSelectedItem(oldRoomType);

        // Buttons for Save and Cancel actions
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        // Action for the "Save" button
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newName = nameField.getText().trim();
                String newPassword = passwordField.getText().trim();
                String newEmail = emailField.getText().trim();
                String newContact = contactField.getText().trim();
                String newBlock = (String) blockComboBox.getSelectedItem();
                String newFloor = (String) floorComboBox.getSelectedItem();
                String newNumber = (String) numberComboBox.getSelectedItem();
                String newRoomType = (String) roomTypeComboBox.getSelectedItem();
                String newAddress = newBlock + "_" + newFloor + "_" + newNumber;

                if (newName.isEmpty() || newPassword.isEmpty() || newEmail.isEmpty() || newContact.isEmpty()) {
                    JOptionPane.showMessageDialog(modifyFrame, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!ValidationUtils.validateInput(newName, newPassword, newEmail, newContact, role, null)) {
                    return;
                }
                
                if (!newName.equalsIgnoreCase(oldName) && ValidationUtils.checkDuplicateExceptCurrent(newName, null, null, null, null, role, oldName)) {
                    return; // Abort if new name is a duplicate
                }
                
                if (!newEmail.equalsIgnoreCase(oldEmail) && ValidationUtils.checkDuplicateExceptCurrent(null, newEmail, null, null, null, role, oldName)) {
                    return; // Abort if new name is a duplicate
                }
                
                if (!newContact.equalsIgnoreCase(oldContact) && ValidationUtils.checkDuplicateExceptCurrent(null, null, newContact, null, null, role, oldName)) {
                    return; // Abort if new name is a duplicate
                }
                
                if ((!newAddress.equalsIgnoreCase(oldAddress) || !newRoomType.equalsIgnoreCase(oldRoomType)) 
                    && ValidationUtils.checkDuplicateExceptCurrent(null, null, null, newAddress, newRoomType, role, oldName)) {
                    return; // Abort if new address and room type are a duplicate for a Resident
                }
                
                if (oldName != null && newName != null && !oldName.equals(newName)) {
                    FileHandler.writeSystemLog("(Manager) " + userName + " modify " + role + ", " + oldName + " name from " + oldName + " to " + newName + ".");
                }
                
                if (oldPassword != null && newPassword != null && !oldPassword.equals(newPassword)) {
                    FileHandler.writeSystemLog("(Manager) " + userName + " modify " + role + ", email from " + oldPassword + " to " + newPassword + ".");
                }
                
                // Log email change if applicable
                if (oldEmail != null && newEmail != null && !oldEmail.equals(newEmail)) {
                    FileHandler.writeSystemLog("(Manager) " + userName + " modify " + role + ", email from " + oldEmail + " to " + newEmail + ".");
                }
                
                // Log contact number change if applicable
                if (oldContact != null && newContact != null && !oldContact.equals(newContact)) {
                    FileHandler.writeSystemLog("(Manager) " + userName + " modify " + role + ", contact number from " + oldContact + " to " + newContact + ".");
                }
                
                // Log address change if applicable (only for Residents)
                if (role.equalsIgnoreCase("Resident") && oldAddress != null && newAddress != null && !oldAddress.equals(newAddress)) {
                    FileHandler.writeSystemLog("(Manager) " + userName + " modify " + role + ", address from " + oldAddress + " to " + newAddress + ".");
                }
                
                if (role.equalsIgnoreCase("Resident") && oldRoomType != null && newRoomType != null && !oldRoomType.equals(newRoomType)) {
                    FileHandler.writeSystemLog("(Manager) " + userName + " modify " + role + ", address from " + oldRoomType + " to " + newRoomType + ".");
                }

                // Find the user and update their details
                User updatedUser = null;
                if (role.equals("Resident")) {
                    for (Resident resident : FileHandler.allResidents) {
                        if (resident.getName().equals(oldName)) {
                            resident.setName(newName);
                            resident.setPassword(newPassword);
                            resident.setEmailAddress(newEmail);
                            resident.setContactNumber(newContact);
                            resident.setAddress(newAddress);
                            resident.setRoomType(newRoomType);
                            updatedUser = resident;
                            break;
                        }
                    }
                }
                
                if (role.equals("Staff")) {
                    for (Staff staff : FileHandler.allStaffs) {
                        if (staff.getName().equals(oldName)) {
                            staff.setName(newName);
                            staff.setPassword(newPassword);
                            staff.setEmailAddress(newEmail);
                            staff.setContactNumber(newContact);
                            updatedUser = staff;
                            break;
                        }
                    }
                }
                
                if (role.equals("Manager")) {
                    for (Manager manager : FileHandler.allManagers) {
                        if (manager.getName().equals(oldName)) {
                            manager.setName(newName);
                            manager.setPassword(newPassword);
                            manager.setEmailAddress(newEmail);
                            manager.setContactNumber(newContact);
                            updatedUser = manager;
                            break;
                        }
                    }
                }

                if (updatedUser != null) {
                    FileHandler.writeToRoleFile(updatedUser.getRole());
                    JOptionPane.showMessageDialog(modifyFrame, "User information updated successfully.");
                    modifyFrame.dispose();
                    openSearchUser(userName);
                } else {
                    JOptionPane.showMessageDialog(modifyFrame, "Error updating user information.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Action for the "Cancel" button
        cancelButton.addActionListener(e -> {
            modifyFrame.dispose(); // Close the modify frame
            openSearchUser(userName); // Call the openSearchUser method
        });

        // Add components to the frame
        modifyFrame.add(nameLabel);
        modifyFrame.add(nameField);
        modifyFrame.add(passwordLabel);
        modifyFrame.add(passwordField);
        modifyFrame.add(emailLabel);
        modifyFrame.add(emailField);
        modifyFrame.add(contactLabel);
        modifyFrame.add(contactField);

        if (role.equals("Resident")) {
            modifyFrame.add(blockLabel);
            modifyFrame.add(blockComboBox);
            modifyFrame.add(floorLabel);
            modifyFrame.add(floorComboBox);
            modifyFrame.add(numberLabel);
            modifyFrame.add(numberComboBox);
            modifyFrame.add(roomTypeLabel);
            modifyFrame.add(roomTypeComboBox);
        }

        modifyFrame.add(saveButton);
        modifyFrame.add(cancelButton);

        modifyFrame.setVisible(true);
        modifyFrame.setLocationRelativeTo(null);
    }
}
