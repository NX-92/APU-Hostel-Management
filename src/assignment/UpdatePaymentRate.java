package assignment;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class UpdatePaymentRate implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backButton) {
            paymentRateFrame.dispose(); // Close the update frame when the back button is clicked
            ManagerFunction managerFunction = new ManagerFunction();
            managerFunction.openManagerFunction(userName);
        } else if (e.getSource() == updateButton) {
            handleUpdatePaymentRate();
        }
    }

    // UI Components
    private JFrame paymentRateFrame;
    private JPanel oldRatePanel, newRatePanel, buttonPanel, roomPanel;
    private JLabel oldRateLabel, newRateLabel, roomTypeLabel;
    private JTextField oldRateField, newRateField;
    private JButton updateButton, backButton;
    private JComboBox<String> roomTypeComboBox;

    // User info
    private String userName;

    // Default Payment Rates
    private final double DEFAULT_SMALL_ROOM_RATE = 300.00;
    private final double DEFAULT_NORMAL_ROOM_RATE = 500.00;
    private final double DEFAULT_MASTER_ROOM_RATE = 600.00;

    public void openUpdatePaymentRate(String username) {
       this.userName = username;
      
       FileHandler.writeSystemLog("(Manager) " + username + " enter into openUpdatePaymentRate manager function.");

       // Initialize frame
       paymentRateFrame = new JFrame("Change Payment Rate");
       paymentRateFrame.setSize(400, 300);
       paymentRateFrame.setLayout(new BorderLayout(10, 10)); // Add spacing between components
       paymentRateFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Proper close operation

       // Create main panels
       roomPanel = new JPanel();
       roomPanel.setBackground(Color.LIGHT_GRAY);
       roomPanel.setLayout(new BorderLayout());

       JPanel ratePanel = new JPanel();
       ratePanel.setLayout(new BoxLayout(ratePanel, BoxLayout.Y_AXIS)); // Stack panels vertically

       oldRatePanel = new JPanel();
       oldRatePanel.setBackground(Color.LIGHT_GRAY);
       oldRatePanel.setLayout(new BorderLayout());

       newRatePanel = new JPanel();
       newRatePanel.setBackground(Color.WHITE);
       newRatePanel.setLayout(new BorderLayout());

       buttonPanel = new JPanel();
       buttonPanel.setBackground(Color.DARK_GRAY);
       buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

       // Room Type Dropdown
       roomTypeLabel = new JLabel("Select Room Type:", SwingConstants.CENTER);
       roomTypeComboBox = new JComboBox<>(new String[]{"Small Room", "Normal Room", "Master Room"});
       roomTypeComboBox.setSelectedIndex(0); // Default selection

       // Old Rate Label and Text Field (readonly)
       oldRateLabel = new JLabel("Old Payment Rate: $", SwingConstants.CENTER);
       oldRateField = new JTextField();
       oldRateField.setEditable(false); // Make this field uneditable
       oldRateField.setHorizontalAlignment(JTextField.CENTER);
       updateOldRateField();

       // New Rate Label and Text Field
       newRateLabel = new JLabel("Enter New Payment Rate (min $200):", SwingConstants.CENTER);
       newRateField = new JTextField();
       newRateField.setHorizontalAlignment(JTextField.CENTER);

       // Update Button
       updateButton = new JButton("Update");
       updateButton.setBackground(Color.GREEN);

       // Back Button
       backButton = new JButton("Back");
       backButton.setBackground(Color.RED);

       // Add ActionListeners for buttons
       updateButton.addActionListener(this);
       backButton.addActionListener(this);

       // Add components to the panels
       roomPanel.add(roomTypeLabel, BorderLayout.NORTH);
       roomPanel.add(roomTypeComboBox, BorderLayout.CENTER);

       oldRatePanel.add(oldRateLabel, BorderLayout.NORTH);
       oldRatePanel.add(oldRateField, BorderLayout.CENTER);

       newRatePanel.add(newRateLabel, BorderLayout.NORTH);
       newRatePanel.add(newRateField, BorderLayout.CENTER);

       // Add old and new rate panels to the ratePanel (vertical stacking)
       ratePanel.add(oldRatePanel);
       ratePanel.add(newRatePanel);

       buttonPanel.add(updateButton);
       buttonPanel.add(backButton);

       // Add panels to the frame
       paymentRateFrame.add(roomPanel, BorderLayout.NORTH);
       paymentRateFrame.add(ratePanel, BorderLayout.CENTER); // Center will now hold the old and new rate panels
       paymentRateFrame.add(buttonPanel, BorderLayout.PAGE_END);

       // Listen for room type changes
       roomTypeComboBox.addActionListener(e -> updateOldRateField());

       // Center the frame on the screen
       paymentRateFrame.setLocationRelativeTo(null);

       // Make the frame visible
       paymentRateFrame.setVisible(true);
   }

    private void updateOldRateField() {
        String selectedRoom = (String) roomTypeComboBox.getSelectedItem();
        oldRateField.setText(String.valueOf(Payment.getPaymentRateByRoomType(selectedRoom)));
    }

    private void handleUpdatePaymentRate() {
        try {
            // Retrieve the new payment rate entered by the manager
            double newRate = Double.parseDouble(newRateField.getText());

            // Check if the new rate is valid (greater than or equal to $200)
            if (newRate >= 200.00) {
                // Get the selected room type
                String selectedRoom = (String) roomTypeComboBox.getSelectedItem();

                // Update the payment rate for the selected room type
                Payment.updatePaymentRate(selectedRoom, newRate);

                // Show a confirmation message
                JOptionPane.showMessageDialog(paymentRateFrame, "Payment rate updated successfully for " + selectedRoom + "!");

                // Close the frame after updating
                paymentRateFrame.dispose();
                ManagerFunction managerFunction = new ManagerFunction();
                managerFunction.openManagerFunction(userName);
                FileHandler.writeSystemLog("(Manager) " + userName + " update the " + selectedRoom + " payment rate from " + oldRateField.getText() + " to " + newRate);
            } else {
                JOptionPane.showMessageDialog(paymentRateFrame, "Please enter a valid payment rate of $200 or more.", "Invalid Rate", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(paymentRateFrame, "Please enter a valid number for the payment rate.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }
}
