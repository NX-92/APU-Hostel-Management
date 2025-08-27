package assignment;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class StaffFunction implements ActionListener{
    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        Object source = e.getSource();

        if (source == updateInfoButton) {
            frame.dispose();
            openUpdateInfo updateWindow = new openUpdateInfo();
            updateWindow.openUpdateInfo(frame, loggedInStaff);
        } else if (source == processPaymentButton) {
            frame.dispose();
            ProcessPayment processpayment = new ProcessPayment();
            processpayment.openProcessPayment(loggedInStaff.getName());
        } else if (source == viewPaymentReceiptHistoryButton) {
            frame.dispose();
            ViewAllPaymentReceipt viewPaymentWindow = new ViewAllPaymentReceipt();
            viewPaymentWindow.openViewAllPaymentReceipt(loggedInStaff.getName(), "Staff", "");
        } else if (source == logoutButton) {
            logout();
        }
    }
    
    private JFrame frame;
    private JPanel mainPanel, buttonPanel;
    private JLabel titleLabel;
    private JButton updateInfoButton, processPaymentButton, viewPaymentReceiptHistoryButton, logoutButton;
    private Staff loggedInStaff;

    public void openStaffFunction(String username) {
        FileHandler.writeSystemLog("(Staff) " + username + " enter into staff function.");
        // Find the logged-in staff based on the username
        for (Staff s : FileHandler.allStaffs) {
            if (s.getName().equals(username)) {
                loggedInStaff = s;
                break;
            }
        }

        if (loggedInStaff == null) {
            JOptionPane.showMessageDialog(null, "Staff member not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        frame = new JFrame("Staff Functions");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null);

        mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        titleLabel = new JLabel("Welcome, " + loggedInStaff.getName() + "!");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(new Color(30, 144, 255));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10)); // Updated layout to accommodate 4 buttons
        buttonPanel.setBackground(new Color(240, 248, 255));

        updateInfoButton = createStyledButton("Update Personal Info");
        processPaymentButton = createStyledButton("Process Resident Payment");
        viewPaymentReceiptHistoryButton = createStyledButton("View Payment History");
        logoutButton = createStyledButton("Logout");

        buttonPanel.add(updateInfoButton);
        buttonPanel.add(processPaymentButton);
        buttonPanel.add(viewPaymentReceiptHistoryButton);
        buttonPanel.add(logoutButton);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        updateInfoButton.addActionListener(this);  // Register the action listener
        processPaymentButton.addActionListener(this);  // Register the action listener
        viewPaymentReceiptHistoryButton.addActionListener(this);  // Register the action listener
        logoutButton.addActionListener(this);  // Register the action listener

        frame.add(mainPanel);
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
            LoginPage loginpage = new LoginPage();
            loginpage.openLoginPage();
            FileHandler.writeSystemLog("(Staff) " + loggedInStaff.getName() + " logout the system.");
        }
    }
}
