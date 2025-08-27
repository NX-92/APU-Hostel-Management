package assignment;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class SystemLogView {
    
    private JComboBox<String> yearComboBox;
    private JComboBox<String> monthComboBox;
    private JTextArea logArea;
    private JLabel statusLabel;
    
    private String userName;
    
    private String selectedYear;  // To store the selected year
    private String selectedMonth; // To store the selected month
    
    private static final String[] months = {
        "JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE",
        "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"
    };
    
    private static final String[] years = {
        "2020", "2021", "2022", "2023", "2024", "2025", "2026", 
        "2027", "2028", "2029", "2030"
    };

    // Create and show the GUI
    public void openSystemLogView(String username) {
        this.userName = username;
        FileHandler.writeSystemLog("(Manager) " + userName + " opened the system logs manager function.");

        // Initialize the selected year and month with default values
        selectedYear = years[0];
        selectedMonth = months[0];

        JFrame frame = new JFrame("System Log Viewer");
        frame.setSize(800, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Filter Panel (top section)
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new FlowLayout());
        filterPanel.setBorder(BorderFactory.createTitledBorder("Log Filter"));

        yearComboBox = new JComboBox<>(years);
        monthComboBox = new JComboBox<>(months);

        filterPanel.add(new JLabel("Year:"));
        filterPanel.add(yearComboBox);
        filterPanel.add(new JLabel("Month:"));
        filterPanel.add(monthComboBox);

        // Status Panel (bottom section)
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        statusLabel = new JLabel("Showing logs: 0");
        statusPanel.add(statusLabel);

        // Log Area (middle section)
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Clear Button (reset filters)
        JButton clearButton = new JButton("Clear Filter");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearSystemLogs(selectedYear, String.format("%02d", getMonthNumber(selectedMonth)));
            }
        });

        // Back Button (go back to the previous screen)
        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                ManagerFunction managerFunction = new ManagerFunction();
                managerFunction.openManagerFunction(userName);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(clearButton);
        buttonPanel.add(backButton);

        // Add components to the frame
        frame.add(filterPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(statusPanel, BorderLayout.SOUTH);
        frame.add(buttonPanel, BorderLayout.EAST);

        // Update the selected year and month when combo boxes are changed
        yearComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedYear = (String) yearComboBox.getSelectedItem();
                filterLogs();
            }
        });

        monthComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedMonth = (String) monthComboBox.getSelectedItem();
                filterLogs();
            }
        });

        // Display the initial set of logs based on the default selection
        filterLogs();

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void filterLogs() {
        String intMonth = String.format("%02d", getMonthNumber(selectedMonth)); // Ensure two digits for month

        logArea.setText(""); // Clear existing logs in the text area

        try (BufferedReader reader = new BufferedReader(new FileReader("SystemLogs.txt"))) {
            String line;
            int logCount = 0;

            while ((line = reader.readLine()) != null) {
                String timestamp = line.split(" ")[0] + " " + line.split(" ")[1]; // yyyy-MM-dd HH:mm:ss
                String date = timestamp.split(" ")[0]; // yyyy-MM-dd
                String logYear = date.split("-")[0];
                String logMonth = date.split("-")[1];

                // Check if the log matches the selected year and month
                if (logYear.equals(selectedYear) && logMonth.equals(intMonth)) {
                    logArea.append(line + "\n");
                    logCount++;
                }
            }

            statusLabel.setText("Showing logs: " + logCount);

            if (logCount == 0) {
                logArea.append("\nNo logs found for " + selectedMonth + " " + selectedYear);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error reading log file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearSystemLogs(String year, String month) {
        int yearInt = Integer.parseInt(year);
        FileHandler.clearLogFile(yearInt, month);
        FileHandler.writeSystemLog("(Manager) " + userName + " clear the system logs of " + month + ", " + year);

        yearComboBox.setSelectedIndex(0);  // Reset yearComboBox
        monthComboBox.setSelectedIndex(0); // Reset monthComboBox
        
        filterLogs();
    }
    
    public static int getMonthNumber(String monthName) {
        switch (monthName.toUpperCase()) {
            case "JANUARY": 
                return 1;
            case "FEBRUARY": 
                return 2;
            case "MARCH": 
                return 3;
            case "APRIL": 
                return 4;
            case "MAY": 
                return 5;
            case "JUNE": 
                return 6;
            case "JULY": 
                return 7;
            case "AUGUST": 
                return 8;
            case "SEPTEMBER": 
                return 9;
            case "OCTOBER": 
                return 10;
            case "NOVEMBER": 
                return 11;
            case "DECEMBER": 
                return 12;
            default: 
                return -1;
        }
    }
}
