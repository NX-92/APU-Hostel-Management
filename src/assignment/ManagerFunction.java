package assignment;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class ManagerFunction implements ActionListener {
    // Overriding actionPerformed method from ActionListener interface
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == approveRequestsButton) {
            frame.dispose();
            ApproveRejectRequests approverejectrequests = new ApproveRejectRequests();
            approverejectrequests.openApproveRequests(userName);
        } else if (source == addUserButton) {
            frame.dispose();
            AddUser adduser = new AddUser();
            adduser.openAddUser(userName);
        } else if (source == searchUserButton) {
            frame.dispose();
            UserManagement usermanagement = new UserManagement();
            usermanagement.openSearchUser(userName);
        } else if (source == updatePaymentRateButton) {
            frame.dispose();
            UpdatePaymentRate updatepaymentrate = new UpdatePaymentRate();
            updatepaymentrate.openUpdatePaymentRate(userName);
        } else if (source == viewAllPaymentsReceiptsButton) {
            frame.dispose();
            ViewAllPaymentReceipt viewPaymentWindow = new ViewAllPaymentReceipt(); // Create an instance
            viewPaymentWindow.openViewAllPaymentReceipt(userName, "Manager", ""); // Pass the role and address
        } else if (source == modifyDeletePaymentsReceiptsButton) {
            frame.dispose();
            ModifyDeletePaymentReceipt modifyDeletePayments = new ModifyDeletePaymentReceipt();
            modifyDeletePayments.openModifyDeletePaymentReceipt(userName);
        } else if (source == systemLogButton) {
            frame.dispose();
            SystemLogView systemlogview = new SystemLogView();
            systemlogview.openSystemLogView(userName);
        }else if (source == logoutButton) {
            logout();
        }
    }

    private JFrame frame;
    private JPanel panel;
    private JButton approveRequestsButton, addUserButton, searchUserButton, updatePaymentRateButton, viewAllPaymentsReceiptsButton, modifyDeletePaymentsReceiptsButton, systemLogButton, logoutButton;
    private Manager loggedInManager;
    private String userName;

    public void openManagerFunction(String username) {
        this.userName = username;
        FileHandler.writeSystemLog("(Manager) " + username + " enter into manager function.");
        for (Manager m : FileHandler.allManagers) {
            if (m.getName().equals(username)) {
                loggedInManager = m;
                break;
            }
        }

        if (loggedInManager == null) {
            JOptionPane.showMessageDialog(null, "Staff member not found!", "Error", JOptionPane.ERROR_MESSAGE);
            frame.dispose();
            LoginPage loginpage = new LoginPage();
            loginpage.openLoginPage();
        }

        frame = new JFrame("Manager Functions");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 500);
        frame.setLayout(new BorderLayout());

        panel = new JPanel(new GridLayout(8, 1, 10, 10)); // Updated grid layout to 8 rows

        approveRequestsButton = new JButton("Approve/Reject User Requests");
        addUserButton = new JButton("Add User");
        searchUserButton = new JButton("Search/Update/Delete User");
        updatePaymentRateButton = new JButton("Change Payment Rate");
        viewAllPaymentsReceiptsButton = new JButton("View All Payments");
        modifyDeletePaymentsReceiptsButton = new JButton("Modify/Delete Payments & Receipts");
        systemLogButton = new JButton("View System Logs");
        logoutButton = new JButton("Logout");

        panel.add(approveRequestsButton);
        panel.add(addUserButton);
        panel.add(searchUserButton);
        panel.add(updatePaymentRateButton);
        panel.add(viewAllPaymentsReceiptsButton);
        panel.add(modifyDeletePaymentsReceiptsButton);
        panel.add(systemLogButton);
        panel.add(logoutButton);

        approveRequestsButton.addActionListener(this);
        addUserButton.addActionListener(this);
        searchUserButton.addActionListener(this);
        updatePaymentRateButton.addActionListener(this);
        viewAllPaymentsReceiptsButton.addActionListener(this);
        modifyDeletePaymentsReceiptsButton.addActionListener(this);
        systemLogButton.addActionListener(this);
        logoutButton.addActionListener(this);

        frame.add(panel, BorderLayout.CENTER);

        frame.setLocationRelativeTo(null); // Center the frame on the screen
        frame.setVisible(true);
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
            FileHandler.writeSystemLog("(Manager) " + loggedInManager.getName() + " logout the system.");
        }
    }
}
