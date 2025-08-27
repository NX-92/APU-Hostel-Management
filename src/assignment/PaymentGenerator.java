package assignment;

import java.time.LocalDate;

public class PaymentGenerator {
    public void generatePayments() {
        // Get the current date
        LocalDate currentDate = LocalDate.now();
        String currentMonth = currentDate.getMonth().toString().toUpperCase();
        int currentYear = currentDate.getYear();

        // Generate payments for all residents
        for (Resident resident : FileHandler.allResidents) {
            String address = resident.getAddress();
            String roomType = resident.getRoomType();

            // Check if a payment already exists for this address, month, and year
            if (!paymentExists(address, roomType, currentMonth, currentYear)) {
                // Generate payment if it doesn't exist
                Payment newPayment = new Payment(
                    generatePaymentId(),
                    address,
                    resident.getRoomType(),
                    resident.getName(),
                    currentMonth,
                    currentYear, 
                    Payment.getPaymentRateByRoomType(roomType), 
                    Payment.getPaymentRateByRoomType(roomType), 
                    "Unsuccessful"
                );

                resident.addPayment(newPayment); // Add payment to the resident's records
                FileHandler.allPayments.add(newPayment); // Add to global payment records
                FileHandler.writePaymentsToFile(); // Persist updated payments to the file
                
                FileHandler.writeSystemLog("(System) A " + currentMonth + ", " + currentYear + " payment for " + address + " (" + roomType + ") has been generated");
            }
        }
    }

    private boolean paymentExists(String address, String roomType, String month, int year) {
        for (Payment payment : FileHandler.allPayments) {
            if (payment.getAddress().equalsIgnoreCase(address) && 
                payment.getRoomType().equalsIgnoreCase(roomType) && 
                payment.getMonth().equalsIgnoreCase(month) && 
                payment.getYear() == year) {

                // Payment exists, do something here
                return true; // or whatever logic you need
            }
        }
        return false; // No matching payment found
    }

    private int generatePaymentId() {
        return FileHandler.allPayments.size() + 10001;
    }
}
