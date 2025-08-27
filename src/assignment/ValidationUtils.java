 package assignment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class ValidationUtils {
    
    public static boolean validateUser(String username, String password, String role) {
        switch (role) {
            case "Resident":
                for (Resident resident : FileHandler.allResidents) {
                    if (resident.getName().equals(username) && resident.getPassword().equals(password)) {
                        return true;
                    }
                }
                break;
            case "Staff":
                for (Staff staff : FileHandler.allStaffs) {
                    if (staff.getName().equals(username) && staff.getPassword().equals(password)) {
                        return true;
                    }
                }
                break;
            case "Manager":
                for (Manager manager : FileHandler.allManagers) {
                    if (manager.getName().equals(username) && manager.getPassword().equals(password)) {
                        return true;
                    }
                }
                break;
        }
        return false;
    }

    public static boolean validateInput(String username, String password, String email, String contactNumber, String role, String address) {
        if (username.isEmpty() || username.length() > 20){
            JOptionPane.showMessageDialog(null, "Username cannot be empty and cannot over 20 of length");
            return false;
        }
        if (password.length() < 8 || password.length() > 20) {
            JOptionPane.showMessageDialog(null, "Password must be between 8 and 20 characters.");
            return false;
        }
        int letterCount = 0;
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                letterCount++;
            }
        }

        if (letterCount < 3) {
            JOptionPane.showMessageDialog(null, "Password must contain at least 3 alphabetic characters.");
            return false;
        }
        if (!email.matches("^[^@]{1,12}@.*$")) {
            JOptionPane.showMessageDialog(null, "Email must have a maximum of 12 characters before the '@' and be in the correct format.");
            return false;
        }
        if (!email.endsWith("@gmail.com") && !email.endsWith("@email.com")) {
            JOptionPane.showMessageDialog(null, "Email must end with @gmail.com or @email.com.");
            return false;
        }
        if (!contactNumber.matches("^\\d{3}-\\d{7}$") && !contactNumber.matches("^\\d{3}-\\d{8}$")) {
            JOptionPane.showMessageDialog(null, "Contact number must be in the format 012-1234567 or 011-12345678.");
            return false;
        }
        return true;
    }
    
    public static boolean validateInputForField(String field, String input, String role) {
        switch (field) {
            case "Name":
                return !input.trim().isEmpty() && input.length() <= 20; // Name should not be empty
            case "Password":
                return input.length() >= 8 && input.length() <= 20; // Password length check
            case "Email":
                return input.matches("^[^@]{1,12}@.*$") && 
                       (input.endsWith("@gmail.com") || input.endsWith("@email.com")); // Email validation
            case "Contact Number":
                return input.matches("^\\d{3}-\\d{7}$") || input.matches("^\\d{3}-\\d{8}$"); // Contact number format
            case "Address":
                // Address validation only for residents, for others it's optional or empty
                return role.equalsIgnoreCase("Resident") || input.trim().isEmpty();
            default:
                return false;
        }
    }

    public static String formatContactNumber(String contactNumber) {
        if (contactNumber.matches("^\\d{3}\\d{7}$")) {
            return contactNumber.substring(0, 3) + "-" + contactNumber.substring(3);
        }
        return contactNumber;
    }

    public static boolean checkForDuplicates(String username, String email, String contactNumber, String address, String roomType, String role) {
        String roleFileName = role.toLowerCase() + "s.txt"; // Corresponding role file

        // Check username and address in WaitingList.txt and corresponding role file
        if (checkFileForDuplicates("WaitingList.txt", username, email, contactNumber, address, roomType, role, true) ||
            checkFileForDuplicates(roleFileName, username, email, contactNumber, address, roomType, role, true)) {
            return true;
        }

        // Check email and contact number across all files
        if (checkFileForDuplicates("WaitingList.txt", username, email, contactNumber, address, roomType, role, false) ||
            checkFileForDuplicates("residents.txt", username, email, contactNumber, address, roomType, role, false) ||
            checkFileForDuplicates("staffs.txt", username, email, contactNumber, address, roomType, role, false) ||
            checkFileForDuplicates("managers.txt", username, email, contactNumber, address, roomType, role, false)) {
            return true;
        }

        return false;
    }

    private static boolean checkFileForDuplicates(String fileName, String username, String email, String contactNumber, String address, String roomType, String role, boolean isRoleSpecific) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String fileUsername = line.trim();
                String filePassword = reader.readLine();
                String fileEmail = reader.readLine();
                String fileContactNumber = reader.readLine();
                String fileRole = reader.readLine();
                String fileAddress = null;
                String fileRoomType = null;

                if ("Resident".equalsIgnoreCase(fileRole)) {
                    fileAddress = reader.readLine();
                    fileRoomType = reader.readLine();
                }

                filePassword = filePassword != null ? filePassword.trim() : "";
                fileEmail = fileEmail != null ? fileEmail.trim() : "";
                fileContactNumber = fileContactNumber != null ? fileContactNumber.trim() : "";
                fileRole = fileRole != null ? fileRole.trim() : "";
                fileAddress = fileAddress != null ? fileAddress.trim() : "";
                fileRoomType = fileRoomType != null ? fileRoomType.trim() : "";

                if (isRoleSpecific && !role.equalsIgnoreCase(fileRole)) continue;

                if (isRoleSpecific && fileUsername.equalsIgnoreCase(username)) {
                    JOptionPane.showMessageDialog(null, "Username already exists in " + role + " role" + "!");
                    return true;
                }
                if (isRoleSpecific && "Resident".equals(role) && fileAddress.equalsIgnoreCase(address) && fileRoomType.equalsIgnoreCase(roomType)) {
                    JOptionPane.showMessageDialog(null, "Address or Room Type already exists in " + role + " role" + "!");
                    return true;
                }
                if (fileEmail.equalsIgnoreCase(email)) {
                    JOptionPane.showMessageDialog(null, "Email already exists in " + role + " role" + "!");
                    return true;
                }
                if (fileContactNumber.equalsIgnoreCase(contactNumber)) {
                    JOptionPane.showMessageDialog(null, "Contact number already exists in " + role + " role" + "!");
                    return true;
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "An error occurred while reading the" + fileName + "file. Please check if the file exists and is accessible.", "File Reading Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
    
    public static String formatUserDetails(User user) {
        if (user instanceof Resident) {
            Resident resident = (Resident) user;
            return String.format(
                "Username: %s\nPassword: %s\nEmail: %s\nContact Number: %s\nRole: Resident\nAddress: %s",
                resident.getName(), 
                resident.getPassword(), 
                resident.getEmailAddress(), 
                resident.getContactNumber(), 
                resident.getAddress()
            );
        } else if (user instanceof Staff) {
            return String.format(
                "Username: %s\nPassword: %s\nEmail: %s\nContact Number: %s\nRole: Staff",
                user.getName(), 
                user.getPassword(), 
                user.getEmailAddress(), 
                user.getContactNumber()
            );
        } else if (user instanceof Manager) {
            return String.format(
                "Username: %s\nPassword: %s\nEmail: %s\nContact Number: %s\nRole: Manager",
                user.getName(), 
                user.getPassword(), 
                user.getEmailAddress(), 
                user.getContactNumber()
            );
        }
        return "Invalid User";
    }
    
    public static boolean checkDuplicateExceptCurrent(String username, String email, String contactNumber, String address, String roomType, String role, String excludeUsername) {
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
                return false;
        }

        for (Object obj : userList) {
            User user = (User) obj;
            if (user.getName().equalsIgnoreCase(excludeUsername)) {
                continue; // Skip the user being modified
            }

            // Check for duplicates in modified fields only
            if (username != null && user.getName().equalsIgnoreCase(username)) {
                JOptionPane.showMessageDialog(null, "Duplicate name found, please enter another username and try again.");
                return true;
            }
            if (email != null && user.getEmailAddress().equalsIgnoreCase(email)) {
                JOptionPane.showMessageDialog(null, "Duplicate email address found, please enter another email address and try again.");
                return true;
            }
            if (contactNumber != null && user.getContactNumber().equalsIgnoreCase(contactNumber)) {
                JOptionPane.showMessageDialog(null, "Duplicate contact number found, please enter another contact number and try again.");
                return true;
            }

            // Address check only for Residents
            if (user instanceof Resident && role.equalsIgnoreCase("Resident")) {
                Resident resident = (Resident) user;
                if (address != null && roomType != null && resident.getAddress().equalsIgnoreCase(address) && resident.getRoomType().equalsIgnoreCase(roomType)) {
                    JOptionPane.showMessageDialog(null, "Duplicate address found, please enter another address and try again.");
                    return true;
                }
            }
            
            if (checkForDuplicates(username, email, contactNumber, address, roomType, role)) {
                return true;
            }
        }
        
        return false;
    }
}