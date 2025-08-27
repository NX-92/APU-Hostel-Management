package assignment;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

public class ApproveRejectRequests implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == approveButton) {
            processRequest(true);
        } else if (e.getSource() == rejectButton) {
            processRequest(false);
        } else if (e.getSource() == searchButton || e.getSource() == roleSelector) {
            searchRequest();
        } else if (e.getSource() == backButton) {
            requestFrame.dispose();
            ManagerFunction managerfunction = new ManagerFunction();
            managerfunction.openManagerFunction(userName);
        }
    }
    
    private JFrame requestFrame;
    private DefaultListModel<String> requestListModel;
    private JList<String> requestList;
    private JTextArea requestDetailsArea;
    private JComboBox<String> roleSelector;
    private JTextField searchField;
    private JButton approveButton;
    private JButton rejectButton;
    private JButton searchButton;
    private JButton backButton;
    private String selectedRequest;
    private String userName;

    public void openApproveRequests(String username) {
        this.userName = username;
        FileHandler.writeSystemLog("(Manager) " + userName + " enter approve registration request manager function.");
        requestFrame = new JFrame("Approve/Reject Requests");
        requestFrame.setSize(700, 700);
        requestFrame.setLayout(new BorderLayout());

        requestListModel = new DefaultListModel<>();
        requestList = new JList<>(requestListModel);
        requestList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        approveButton = new JButton("Approve");
        rejectButton = new JButton("Reject");
        backButton = new JButton("Back");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(approveButton);
        buttonPanel.add(rejectButton);
        buttonPanel.add(backButton);

        requestDetailsArea = new JTextArea(10, 40);
        requestDetailsArea.setEditable(false);
        requestDetailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JPanel searchPanel = new JPanel(new BorderLayout());
        roleSelector = new JComboBox<>(new String[]{"All", "Resident", "Staff", "Manager"});
        searchField = new JTextField();
        searchButton = new JButton("Search");
        searchPanel.add(roleSelector, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        requestList.addListSelectionListener(e -> {
            selectedRequest = requestList.getSelectedValue();
            if (selectedRequest != null) {
                requestDetailsArea.setText(selectedRequest);
            }
        });

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(requestList), new JScrollPane(requestDetailsArea));
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.5);

        requestFrame.add(searchPanel, BorderLayout.NORTH);
        requestFrame.add(splitPane, BorderLayout.CENTER);
        requestFrame.add(buttonPanel, BorderLayout.SOUTH);

        loadRequests("All"); // Load all requests by default

        roleSelector.addActionListener(this);
        searchButton.addActionListener(this);
        approveButton.addActionListener(this);
        rejectButton.addActionListener(this);
        backButton.addActionListener(this);

        requestFrame.setVisible(true);
        
        requestFrame.setLocationRelativeTo(null);
    }

    private void loadRequests(String roleFilter) {
        requestListModel.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader("WaitingList.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String username = line;
                String password = reader.readLine();
                String email = reader.readLine();
                String contactNumber = reader.readLine();
                String role = reader.readLine();
                String address = reader.readLine();
                String roomType = reader.readLine();

                if (roleFilter.equals("All") || role.equalsIgnoreCase(roleFilter)) {
                    // Format the request based on the role
                    String formattedRequest = formatUserRequest(username, password, email, contactNumber, role, address, roomType);
                    requestListModel.addElement(formattedRequest);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(requestFrame, "An error occurred while reading the 'WaitingList' file. Please check if the file exists and is accessible.", "File Reading Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void processRequest(boolean isApproved) {
        if (selectedRequest == null) {
            JOptionPane.showMessageDialog(null, "Please select a request.");
            return;
        }

        String[] lines = selectedRequest.split("\n");
        String username = lines[0].split(": ")[1];
        String password = lines[1].split(": ")[1];
        String email = lines[2].split(": ")[1];
        String contactNumber = lines[3].split(": ")[1];
        String role = lines[4].split(": ")[1];
        String address = role.equalsIgnoreCase("Resident") ? lines[5].split(": ")[1] : "";
        String roomType = role.equalsIgnoreCase("Resident") ? lines[6].split(": ")[1] : "";

        if (isApproved) {
            switch (role) {
                case "Resident":
                    try {
                        // Check if a payment already exists for this address, month, and year
                        String currentMonth = LocalDate.now().getMonth().toString();
                        int currentYear = LocalDate.now().getYear();
                        if (!paymentExists(address, currentMonth, currentYear, roomType)) {
                            // Generate a unique payment ID based on the size of the allPayments list
                            int id = FileHandler.allPayments.size() + 10001;

                            // Create a Payment object using the current month and year
                            Payment p = new Payment(id, address, roomType, username, currentMonth, currentYear, Payment.getPaymentRateByRoomType(roomType), Payment.getPaymentRateByRoomType(roomType), "Unsuccessful");

                            // Create a new Resident object
                            Resident newResident = new Resident(username, password, email, contactNumber, address, roomType);

                            // Add the payment to the resident
                            newResident.addPayment(p);
                            FileHandler.allPayments.add(p);

                            // Add the resident to the allResidents list
                            FileHandler.allResidents.add(newResident);

                            // Write the updated payments data to the payment file
                            FileHandler.writePaymentsToFile();
                        } else {
                            Resident newResident = new Resident(username, password, email, contactNumber, address, roomType);
                            FileHandler.allResidents.add(newResident);
                            for (Payment payment : FileHandler.allPayments) {
                                if(newResident.getAddress().equals(address)) {
                                    newResident.addPayment(payment);
                                }
                            }
                            FileHandler.writePaymentsToFile();
                            JOptionPane.showMessageDialog(null, "User approved, and a payment for this address already exists for " +
                                    currentMonth + " " + currentYear + ".");
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, "Error writing to payment file.");
                    }
                    break;

                case "Staff":
                    FileHandler.allStaffs.add(new Staff(username, password, email, contactNumber));
                    break;

                case "Manager":
                    FileHandler.allManagers.add(new Manager(username, password, email, contactNumber));
                    break;
            }

            // Update the role file
            FileHandler.writeToRoleFile(role);
            FileHandler.read();

            JOptionPane.showMessageDialog(null, "Request approved and added to " + role + "s.txt.");
            FileHandler.writeSystemLog("(Manager) " + userName + " approve a registration request: " + username + "(" + role + ")");
        } else {
            JOptionPane.showMessageDialog(null, "Request rejected.");
            FileHandler.writeSystemLog("(Manager) " + userName + " reject a registration request: " + username + "(" + role + ")");
        }

        // Remove the selected request from WaitingList.txt
        if (removeRequestFromWaitingList(selectedRequest)) {
            // Update UI
            requestListModel.removeElement(selectedRequest);
            requestDetailsArea.setText("");
        } else {
            JOptionPane.showMessageDialog(null, "Error removing request from WaitingList.txt.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean paymentExists(String address, String month, int year, String roomType) {
        for (Payment payment : FileHandler.allPayments) {
            if (payment.getAddress().equalsIgnoreCase(address) &&
                payment.getMonth().equalsIgnoreCase(month) &&
                payment.getYear() == year && payment.getRoomType().equalsIgnoreCase(roomType)) {
                return true;
            }
        }
        return false;
    }

    private boolean removeRequestFromWaitingList(String selectedRequest) {
        File tempFile = new File("temp.txt");
        File waitingListFile = new File("WaitingList.txt");
        boolean removed = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(waitingListFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String[] selectedRequestLines = selectedRequest.split("\\n");
            String selectedUsername = selectedRequestLines[0].split(": ")[1];
            String selectedPassword = selectedRequestLines[1].split(": ")[1];
            String selectedEmail = selectedRequestLines[2].split(": ")[1];
            String selectedContactNumber = selectedRequestLines[3].split(": ")[1];
            String selectedRole = selectedRequestLines[4].split(": ")[1];
            String selectedAddress = selectedRole.equalsIgnoreCase("Resident") ? selectedRequestLines[5].split(": ")[1] : "";
            String selectedRoomType = selectedRole.equalsIgnoreCase("Resident") ? selectedRequestLines[6].split(": ")[1] : "";

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue; // Skip empty lines

                String username = line;
                String password = reader.readLine();
                String email = reader.readLine();
                String contactNumber = reader.readLine();
                String role = reader.readLine();
                String address = role.equalsIgnoreCase("Resident") ? reader.readLine() : "";
                String roomType = role.equalsIgnoreCase("Resident") ? reader.readLine() : "";

                boolean matches = username.equals(selectedUsername) &&
                                  password.equals(selectedPassword) &&
                                  email.equals(selectedEmail) &&
                                  contactNumber.equals(selectedContactNumber) &&
                                  role.equals(selectedRole) &&
                                  address.equals(selectedAddress) && 
                                  roomType.equals(selectedRoomType);

                if (matches) {
                    removed = true;
                } else {
                    writer.write(username + System.lineSeparator() +
                                 password + System.lineSeparator() +
                                 email + System.lineSeparator() +
                                 contactNumber + System.lineSeparator() +
                                 role + System.lineSeparator() +
                                 (role.equalsIgnoreCase("Resident") ? address + System.lineSeparator() : "") +
                                 (role.equalsIgnoreCase("Resident") ? roomType + System.lineSeparator() : "") + 
                                 System.lineSeparator()); // Add blank line for separation
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(requestFrame, "An error occurred while removing data from the 'WaitingList' file. Please check if the file exists and is accessible.", "File Reading Error", JOptionPane.ERROR_MESSAGE);
        }

        if (removed) {
            if (!waitingListFile.delete() || !tempFile.renameTo(waitingListFile)) {
                JOptionPane.showMessageDialog(null, "Error updating WaitingList.txt.");
                return false;
            }
        } else {
            tempFile.delete();
        }

        return removed;
    }

    private String formatUserRequest(String username, String password, String email, String contactNumber, String role, String address, String roomType) {
        if (role.equalsIgnoreCase("Resident")) {
            return String.format(
                "Username: %s\nPassword: %s\nEmail: %s\nContact Number: %s\nRole: %s\nAddress: %s\nRoom Type: %s",
                username, password, email, contactNumber, role, address, roomType
            );
        } else {
            return String.format(
                "Username: %s\nPassword: %s\nEmail: %s\nContact Number: %s\nRole: %s",
                username, password, email, contactNumber, role
            );
        }
    }

    private void searchRequest() {
        String searchQuery = searchField.getText().trim().toLowerCase();
        String selectedRole = (String) roleSelector.getSelectedItem();

        requestListModel.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader("WaitingList.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String username = line;
                String password = reader.readLine();
                String email = reader.readLine();
                String contactNumber = reader.readLine();
                String role = reader.readLine();
                String address = reader.readLine();
                String roomType  = reader.readLine();

                boolean matchesRole = selectedRole.equals("All") || role.equalsIgnoreCase(selectedRole);
                boolean matchesSearch = username.toLowerCase().contains(searchQuery);

                if (matchesRole && matchesSearch) {
                    String formattedRequest = formatUserRequest(username, password, email, contactNumber, role, address, roomType);
                    requestListModel.addElement(formattedRequest);
                    FileHandler.writeSystemLog("(Manager) " + userName + " search a registration request: " + username + "(" + role + ")");
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(requestFrame, "An error occurred while reading the 'WaitingList' file. Please check if the file exists and is accessible.", "File Reading Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
