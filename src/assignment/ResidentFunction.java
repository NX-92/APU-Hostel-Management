package assignment;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class ResidentFunction implements ActionListener {
    // Handle button actions here
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == updateInfoButton) {
            frame.dispose();
            openUpdateInfo updateWindow = new openUpdateInfo(); // Create an instance
            updateWindow.openUpdateInfo(frame, loggedInResident); // Call the method
        } else if (source == viewPaymentsReceiptsButton) {
            frame.dispose();
            ViewAllPaymentReceipt viewPaymentWindow = new ViewAllPaymentReceipt(); // Create an instance
            viewPaymentWindow.openViewAllPaymentReceipt(loggedInResident.getName(), "Resident", loggedInResident.getAddress()); // Pass the role and address
        } else if (source == logoutButton) {
            logout();
        }
    }
    
    private JFrame frame;
    private JPanel panel;
    private JLabel titleLabel;
    private JButton updateInfoButton, viewPaymentsReceiptsButton, logoutButton;
    private Resident loggedInResident;

    public void openResidentFunction(String username) {
        FileHandler.writeSystemLog("(Resident) " + username + " enter into resident function.");
        for (Resident r : FileHandler.allResidents) {
            if (r.getName().equals(username)) {
                loggedInResident = r;
                break;
            }
        }

        if (loggedInResident == null) {
            JOptionPane.showMessageDialog(null, "Resident member not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Frame setup
        frame = new JFrame("Resident Functions");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 600);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout(20, 20));

        // Title setup
        titleLabel = new JLabel("Welcome to Resident Portal", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(30, 144, 255));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        frame.add(titleLabel, BorderLayout.NORTH);

        // Panel setup
        panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Buttons
        updateInfoButton = createStyledButton("Update Personal Info");
        viewPaymentsReceiptsButton = createStyledButton("View Payment & Receipt History");
        logoutButton = createStyledButton("Logout");

        // Register buttons with action listener
        updateInfoButton.addActionListener(this);
        viewPaymentsReceiptsButton.addActionListener(this);
        logoutButton.addActionListener(this);

        panel.add(updateInfoButton);
        panel.add(viewPaymentsReceiptsButton);
        panel.add(logoutButton);

        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBackground(new Color(30, 144, 255));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return button;
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(frame, 
            "Logout notification!!!\nYou are logging out from the system!!!", 
            "Confirm Logout", 
            JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            frame.dispose();
            LoginPage loginpage = new LoginPage(); // Redirect to LoginPage
            loginpage.openLoginPage();
            FileHandler.writeSystemLog("(Resident) " + loggedInResident.getName() + " logout the system.");
        }
    }

    public static void checkUnpaidPayments(String name) {
        int unpaidPaymentsCount = 0;
        boolean residentFound = false;

        for (Resident resident : FileHandler.allResidents) {
            if (resident.getName().equals(name)) {
                residentFound = true;  // Found the resident
                // Check if the resident has payments
                if (resident.getPayments() != null) {
                    for (Payment payment : resident.getPayments()) {
                        // Check if the status is "Unsuccessful" or "Partial"
                        if (payment.getStatus().equalsIgnoreCase("Unsuccessful") || payment.getStatus().equalsIgnoreCase("Partial")) {
                            unpaidPaymentsCount++;
                        }
                    }
                }
                break;  // Exit the loop once the resident is found
            }
        }

        // Display the number of unpaid payments using JOptionPane
        if (!residentFound) {
            JOptionPane.showMessageDialog(null, "Resident not found.");
        } else if (unpaidPaymentsCount > 0) {
            JOptionPane.showMessageDialog(null, "There are " + unpaidPaymentsCount + " unpaid payment(s) of management fees.");
        } else {
            JOptionPane.showMessageDialog(null, "All payments are successfully done.");
        }
    }
}
