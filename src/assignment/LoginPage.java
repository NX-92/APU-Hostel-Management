package assignment;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class LoginPage implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            handleLogin();
        } else if (e.getSource() == registerButton) {
            frame.dispose();
            RegisterPage registerpage = new RegisterPage();
            registerpage.RegisterPage();
        } else if (e.getSource() == exitButton) {
            FileHandler.writeToAllFile();
            FileHandler.writePaymentsToFile();
            FileHandler.writeReceiptsToFile();
            FileHandler.writeSystemLog("(System) System has been closed.");
            System.exit(0);
        }
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String role = (String) roleSelector.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (ValidationUtils.validateUser(username, password, role)) {
            JOptionPane.showMessageDialog(frame, "Login Successful as " + role);
            frame.dispose(); // Close the login page window

            // Set the loginUser as the logged-in user
            Main.loginUser = FileHandler.getUserByUsername(username, role);
            if (Main.loginUser == null) {
                JOptionPane.showMessageDialog(frame, "Error fetching user details.", "Error", JOptionPane.ERROR_MESSAGE);
                return; // Exit if user is not found
            }
            
            FileHandler.writeSystemLog("(" + role + ") " + username + " login into system.");

            // Open the corresponding function page based on role
            switch (role) {
                case "Resident":
                    ResidentFunction residentfunction = new ResidentFunction();
                    residentfunction.openResidentFunction(username);
                    for (Resident r : FileHandler.allResidents) {
                        if (r.getName().equals(username)) {
                            ResidentFunction.checkUnpaidPayments(r.getName());
                            break;
                        }
                    }
                    break;
                case "Staff":
                    StaffFunction stafffunction = new StaffFunction(); // Pass the username
                    stafffunction.openStaffFunction(username);
                    break;
                case "Manager":
                    ManagerFunction managerfunction = new ManagerFunction();
                    managerfunction.openManagerFunction(username);
                    break;
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Invalid Credentials", "Error", JOptionPane.ERROR_MESSAGE);
            clearForm();
        }
    }
    /**
     * Clears all form inputs after a failed login.
     */
    private void clearForm() {
        usernameField.setText("");
        passwordField.setText("");
        roleSelector.setSelectedIndex(0);
        showPassword.setSelected(false);
        passwordField.setEchoChar('*');
    }

    private JFrame frame;
    private JPanel leftPanel, rightPanel, mainPanel;
    private JLabel titleLabel, titleLabel2, usernameLabel, passwordLabel, roleLabel, forgetPasswordLabel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox showPassword;
    private JComboBox<String> roleSelector;
    private JButton loginButton, registerButton, exitButton;

    public void openLoginPage() {
        frame = new JFrame("Login Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 450);
        frame.setLayout(new BorderLayout());

        leftPanel = new JPanel(null); // Use null layout for absolute positioning
        rightPanel = new JPanel(null);

        leftPanel.setBackground(new Color(34, 139, 34));

        // Initialize title labels
        titleLabel = new JLabel("APU Hostel Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel2 = new JLabel("Fees Payment System", SwingConstants.CENTER);
        titleLabel2.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel2.setForeground(Color.WHITE);

        // Set bounds for title labels to center them
        titleLabel.setBounds(0, 160, 430, 40);  // (x, y, width, height)
        titleLabel2.setBounds(0, 200, 430, 40);

        // Add title labels to the left panel
        leftPanel.add(titleLabel);
        leftPanel.add(titleLabel2);

        rightPanel.setBackground(Color.WHITE);
        createFormUI();

        mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        frame.add(mainPanel, BorderLayout.CENTER);

        // Center the frame on the screen
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void createFormUI() {
        usernameLabel = new JLabel("Username:");
        passwordLabel = new JLabel("Password:");
        roleLabel = new JLabel("Role:");

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        showPassword = new JCheckBox("Show Password");
        showPassword.addActionListener(e -> {
            passwordField.setEchoChar(showPassword.isSelected() ? (char) 0 : '*');
        });

        roleSelector = new JComboBox<>(new String[]{"Resident", "Staff", "Manager"});

        loginButton = new JButton("Login");
        registerButton = new JButton("Register");
        exitButton = new JButton("Exit");

        // Create the "Forget Password" label with underline using HTML
        forgetPasswordLabel = new JLabel("Forgot Password?");
        forgetPasswordLabel.setForeground(Color.BLUE);  // Set the text color to blue
        forgetPasswordLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));  // Set the cursor to a hand on hover
        forgetPasswordLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                frame.dispose();
                ForgetPassword forgetpassword = new ForgetPassword();
                forgetpassword.openForgetPassword();
            }
        });

        int y = 60;
        usernameLabel.setBounds(50, y, 100, 30);
        usernameField.setBounds(160, y, 250, 30);

        passwordLabel.setBounds(50, y += 40, 100, 30);
        passwordField.setBounds(160, y, 250, 30);

        showPassword.setBounds(160, y += 30, 150, 20);

        roleLabel.setBounds(50, y += 40, 100, 30);
        roleSelector.setBounds(160, y, 250, 30);

        loginButton.setBounds(100, y += 60, 100, 30);
        registerButton.setBounds(220, y, 100, 30);
        exitButton.setBounds(160, y + 50, 100, 30);

        // Add the "Forget Password" link with underline below the buttons
        forgetPasswordLabel.setBounds(160, y + 100, 200, 30);

        rightPanel.add(usernameLabel);
        rightPanel.add(usernameField);
        rightPanel.add(passwordLabel);
        rightPanel.add(passwordField);
        rightPanel.add(showPassword);
        rightPanel.add(roleLabel);
        rightPanel.add(roleSelector);
        rightPanel.add(loginButton);
        rightPanel.add(registerButton);
        rightPanel.add(exitButton);
        rightPanel.add(forgetPasswordLabel); // Add the forget password link to the panel

        loginButton.addActionListener(this);
        registerButton.addActionListener(this);
        exitButton.addActionListener(this);
    }
}