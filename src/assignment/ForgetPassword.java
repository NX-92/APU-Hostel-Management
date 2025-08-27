package assignment;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ForgetPassword implements ActionListener {
    private JFrame frame;
    private JTextField nameField, emailField, contactField;
    private JComboBox<String> roleComboBox;
    private JTextArea currentPasswordArea;
    private JPasswordField newPasswordField;
    private JButton submitButton, cancelButton, searchButton;
    private JCheckBox showPasswordCheckBox;

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == searchButton) {
            handleSearch();
        } else if (e.getSource() == submitButton) {
            handleSubmit(nameField.getText().trim(), (String) roleComboBox.getSelectedItem());
        } else if (e.getSource() == cancelButton) {
            frame.dispose();
            LoginPage loginpage = new LoginPage();
            loginpage.openLoginPage();
        }
    }

    public void openForgetPassword() {
        // Initialize frame
        frame = new JFrame("Forgot Password");
        frame.setLayout(new BorderLayout(20, 20));

        // Title panel
        JLabel titleLabel = new JLabel("Forgot Password", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(64, 128, 255)); // Use blue color for title
        frame.add(titleLabel, BorderLayout.NORTH);

        // Form panel with BoxLayout
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Form fields with padding and styling
        formPanel.add(createFormRow("Name:", nameField = new JTextField(15)));
        formPanel.add(createFormRow("Email Address:", emailField = new JTextField(15)));
        formPanel.add(createFormRow("Contact Number:", contactField = new JTextField(15)));
        formPanel.add(createFormRow("Role:", roleComboBox = new JComboBox<>(new String[]{"Resident", "Staff", "Manager"})));

        JLabel currentPasswordLabel = new JLabel("Current Password:");
        currentPasswordArea = new JTextArea(2, 15);
        currentPasswordArea.setEditable(false);
        currentPasswordArea.setBackground(new Color(240, 240, 240));
        currentPasswordArea.setLineWrap(true);
        currentPasswordArea.setWrapStyleWord(true);
        currentPasswordArea.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        currentPasswordArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        formPanel.add(currentPasswordLabel);
        formPanel.add(currentPasswordArea);

        formPanel.add(createFormRow("New Password:", newPasswordField = new JPasswordField(15)));

        // Show password checkbox
        showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        showPasswordCheckBox.setForeground(new Color(60, 60, 60));
        showPasswordCheckBox.setBackground(new Color(240, 240, 240)); // Light background to match form

        showPasswordCheckBox.addActionListener(e -> {
            newPasswordField.setEchoChar(showPasswordCheckBox.isSelected() ? (char) 0 : '*');
        });

        formPanel.add(showPasswordCheckBox); // Add checkbox to form panel

        frame.add(formPanel, BorderLayout.CENTER);

        // Button panel with FlowLayout and enhanced button styles
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(240, 240, 240)); // Light gray background for button area

        searchButton = new JButton("Search");
        submitButton = new JButton("Submit");
        cancelButton = new JButton("Cancel");

        // Button styling with rounded corners
        searchButton.setBackground(new Color(64, 128, 255));
        searchButton.setForeground(Color.WHITE);
        searchButton.setBorderPainted(false);
        searchButton.setFocusPainted(false);
        searchButton.setPreferredSize(new Dimension(120, 40));
        searchButton.setFont(new Font("Segoe UI", Font.BOLD, 14));

        submitButton.setBackground(new Color(64, 128, 255));
        submitButton.setForeground(Color.WHITE);
        submitButton.setBorderPainted(false);
        submitButton.setFocusPainted(false);
        submitButton.setPreferredSize(new Dimension(120, 40));
        submitButton.setFont(new Font("Segoe UI", Font.BOLD, 14));

        cancelButton.setBackground(new Color(200, 200, 200));
        cancelButton.setForeground(Color.BLACK);
        cancelButton.setBorderPainted(false);
        cancelButton.setFocusPainted(false);
        cancelButton.setPreferredSize(new Dimension(120, 40));
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Add buttons to panel
        buttonPanel.add(searchButton);
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Button listeners
        searchButton.addActionListener(this);
        submitButton.addActionListener(this);
        cancelButton.addActionListener(this);

        // Frame settings
        frame.setSize(500, 500); // Adjusted height to accommodate the checkbox
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Initially disable the submit button
        submitButton.setEnabled(false);
    }

    // Create a form row with a label and input field
    private JPanel createFormRow(String labelText, JComponent inputField) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(60, 60, 60)); // Dark text color for labels
        panel.add(label, BorderLayout.WEST);

        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        inputField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        inputField.setPreferredSize(new Dimension(250, 30));
        inputField.setBackground(new Color(250, 250, 250)); // Light background for input fields
        panel.add(inputField, BorderLayout.CENTER);

        panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        return panel;
    }

    private void handleSearch() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String contact = contactField.getText().trim();
        String role = (String) roleComboBox.getSelectedItem();

        if (name.isEmpty() || email.isEmpty() || contact.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String foundPassword = searchUser(name, email, contact, role);
        if (foundPassword != null) {
            currentPasswordArea.setText(foundPassword);
            submitButton.setEnabled(true);
            JOptionPane.showMessageDialog(frame, "User found! You can now reset your password.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame, "User not found. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSubmit(String username, String role) {
        String newPassword = new String(newPasswordField.getPassword()).trim();
        try {
            if (newPassword.isEmpty() || !ValidationUtils.validateInputForField("Password", newPassword, role)) {
                JOptionPane.showMessageDialog(frame, "New password cannot be empty and must between 8 to 20 characters.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                User user = FileHandler.getUserByUsername(username, role);
                user.setPassword(newPassword);
                FileHandler.writeToRoleFile(role);
                FileHandler.writeSystemLog("(" + role + ") " + user.getName() + " Change password from " + currentPasswordArea.getText() + " to " + newPassword);
                JOptionPane.showMessageDialog(frame, "Password successfully reset!", "Success", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
                LoginPage loginpage = new LoginPage();
                loginpage.openLoginPage();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "User Not Found, Please check your information.");
        }
    }

    private static String searchUser(String name, String email, String contact, String role) {
        switch (role) {
            case "Resident":
                for (Resident resident : FileHandler.allResidents) {
                    if (resident.getName().equals(name) && resident.getEmailAddress().equals(email) && resident.getContactNumber().equals(contact)) {
                        return resident.getPassword();
                    }
                }
                break;
            case "Staff":
                for (Staff staff : FileHandler.allStaffs) {
                    if (staff.getName().equals(name) && staff.getEmailAddress().equals(email) && staff.getContactNumber().equals(contact)) {
                        return staff.getPassword();
                    }
                }
                break;
            case "Manager":
                for (Manager manager : FileHandler.allManagers) {
                    if (manager.getName().equals(name) && manager.getEmailAddress().equals(email) && manager.getContactNumber().equals(contact)) {
                        return manager.getPassword();
                    }
                }
                break;
            default:
                return null; // Invalid role
        }
        return null; // No match found
    }
}
