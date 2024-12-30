package DSA.UserControl;

import DSA.Admin.DB_Connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateUserInfo {

    public void updateAccountInformation(int userId, String newFirstName, String newLastName, String newEmail, String newPassword) {
        try (Connection conn = DriverManager.getConnection(DB_Connection.url, DB_Connection.user, DB_Connection.pass)) {
            // Update user details: first name, last name, email, and password
            String updateQuery = "UPDATE users SET firstname = ?, lastname = ?, email = ?, password = ? WHERE Id = ?";

            try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                stmt.setString(1, newFirstName); // Set new first name
                stmt.setString(2, newLastName);  // Set new last name
                stmt.setString(3, newEmail);     // Set new email
                stmt.setString(4, newPassword); // Set new password
                stmt.setInt(5, userId);         // Set user ID

                int rowsAffected = stmt.executeUpdate(); // Execute the query

                if (rowsAffected > 0) {
                    System.out.println("User account information updated successfully.");
                } else {
                    System.out.println("No user found with the given ID.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error updating account information: " + e.getMessage());
        }
    }
}
