package assignment;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class openUpdateInfo implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == saveButton) {
            String newName = nameField.getText().trim();
            String newPassword = passwordField.getText().trim();
            String newEmail = emailField.getText().trim();
            String newContact = contactField.getText().trim();

            String oldPassword = user.getPassword().trim();

            if (!ValidationUtils.validateInput(newName, newPassword, newEmail, newContact, user.getRole(), null)) {
                return;
            }

            // Validate only modified fields for duplicates
            if (!newName.equalsIgnoreCase(oldName) && ValidationUtils.checkDuplicateExceptCurrent(newName, null, null, null, null, user.getRole(), oldName)) {
                return; // Abort if new name is a duplicate
            }

            if (!newEmail.equalsIgnoreCase(oldEmail) && ValidationUtils.checkDuplicateExceptCurrent(null, newEmail, null, null, null, user.getRole(), oldName)) {
                return; // Abozrt if new email is a duplicate
            }

            if (!newContact.equalsIgnoreCase(oldContact) && ValidationUtils.checkDuplicateExceptCurrent(null, null, newContact, null, null, user.getRole(), oldName)) {
                return; // Abort if new contact is a duplicate
            }

            if (oldName != null && newName != null && !oldName.equals(newName)) {
                FileHandler.writeSystemLog("(" + user.getRole() + ") " + newName + " modify his/her name from " + oldName + " to " + newName + ".");
            }

            if (oldPassword != null && newPassword != null && !oldPassword.equals(newPassword)) {
                FileHandler.writeSystemLog("(" + user.getRole() + ") " + newName + " modify his/her password from " + oldPassword + " to " + newPassword + ".");
            }

            // Log email change if applicable
            if (oldEmail != null && newEmail != null && !oldEmail.equals(newEmail)) {
                FileHandler.writeSystemLog("(" + user.getRole() + ") " + newName + " modify his/her email address from " + oldEmail + " to " + newEmail + ".");
            }

            // Log contact number change if applicable
            if (oldContact != null && newContact != null && !oldContact.equals(newContact)) {
                FileHandler.writeSystemLog("(" + user.getRole() + ") " + newName + " modify his/her contact number fromm " + oldContact + " to " + newContact + ".");
            }

            switch (user.getRole()) {
                case "Resident":
                    for (Resident resident : FileHandler.allResidents) {
                        if (resident.getName().equalsIgnoreCase(oldName)) {
                            resident.setName(newName);
                            resident.setPassword(newPassword);
                            resident.setEmailAddress(newEmail);
                            resident.setContactNumber(newContact);
                            break; // Exit loop once the resident is found and updated
                        }
                    }
                case "Staff":
                    for (Staff staff : FileHandler.allStaffs) {
                        if (staff.getName().equalsIgnoreCase(oldName)) {
                            staff.setName(newName);
                            staff.setPassword(newPassword);
                            staff.setEmailAddress(newEmail);
                            staff.setContactNumber(newContact);
                            break; // Exit loop once the staff member is found and updated
                        }
                    }
            }

            FileHandler.writeToRoleFile(user.getRole());
            JOptionPane.showMessageDialog(updateFrame, "Name: " + user.getName() + "\nRole: " + user.getRole() + "\nPersonal Information updated successfully!");
            updateFrame.dispose();
            if(user.getRole().equalsIgnoreCase("Resident")) {
                ResidentFunction residentfunction = new ResidentFunction();
                residentfunction.openResidentFunction(newName);
            } else {
                StaffFunction stafffunction = new StaffFunction();
                stafffunction.openStaffFunction(newName);
            }
        } else if (e.getSource() == cancelButton) {
            updateFrame.dispose();
            oldName = user.getName();
            oldContact = user.getContactNumber();
            oldEmail = user.getEmailAddress();
            if(user.getRole().equalsIgnoreCase("Resident")) {
                ResidentFunction residentfunction = new ResidentFunction();
                residentfunction.openResidentFunction(oldName);
            } else {
                StaffFunction stafffunction = new StaffFunction();
                stafffunction.openStaffFunction(oldName);
            }
        }
    }
    private JTextField nameField;
    private JTextField passwordField;
    private JTextField emailField;
    private JTextField contactField;
    private JFrame updateFrame;
    private JButton saveButton, cancelButton;
    private User user;
    private String oldName;
    private String oldContact;
    private String oldEmail;

    public void openUpdateInfo(JFrame parentFrame, User user) {
        this.user = user;
        oldName = user.getName();
        oldContact = user.getContactNumber();
        oldEmail = user.getEmailAddress();

        updateFrame = new JFrame("Update Personal Information");
        updateFrame.setSize(500, 500);
        updateFrame.setLayout(new BorderLayout(10, 10));
        updateFrame.setLocationRelativeTo(parentFrame);

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(64, 128, 255));
        JLabel headerLabel = new JLabel("Update Personal Information", JLabel.CENTER);
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(headerLabel);

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBackground(new Color(230, 230, 250));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField(user.getName());

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JTextField(user.getPassword());

        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField(user.getEmailAddress());

        JLabel contactLabel = new JLabel("Contact Number:");
        contactField = new JTextField(user.getContactNumber());

        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(emailLabel);
        formPanel.add(emailField);
        formPanel.add(contactLabel);
        formPanel.add(contactField);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(192, 192, 192));

        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");

        saveButton.addActionListener(this);
        cancelButton.addActionListener(this);

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Add Panels to Frame
        updateFrame.add(headerPanel, BorderLayout.NORTH);
        updateFrame.add(formPanel, BorderLayout.CENTER);
        updateFrame.add(buttonPanel, BorderLayout.SOUTH);

        updateFrame.setVisible(true);
    }
}
