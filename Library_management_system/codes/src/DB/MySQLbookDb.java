package DB;

import java.sql.*;
import java.time.LocalDateTime;

public class MySQLbookDb {
    public static boolean AddBooks(int Id, String title, String genre, String author, LocalDateTime Datepub, int copies, int totalCopies) {
        try (Connection connection = DriverManager.getConnection(DB_Connection.book, DB_Connection.user, DB_Connection.pass);
             PreparedStatement register = connection.prepareStatement("INSERT INTO " + DB_Connection.BookTable +
                     "(Id, Title, Genre, Author, Publish_date, Copies, Total_copies) VALUES(?, ?, ?, ?, ?, ?, ?)")) {

            register.setInt(1, Id);
            register.setString(2, title);
            register.setString(3, genre);
            register.setString(4, author);
            // Convert LocalDateTime to SQL Timestamp
            register.setTimestamp(5, Timestamp.valueOf(Datepub));
            register.setInt(6, copies);
            register.setInt(7, totalCopies);

            // Use executeUpdate() for INSERT, not executeQuery()
            register.executeUpdate();
            return true;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean validateaddedBooks(int Id, String Title) {
        try (Connection connection = DriverManager.getConnection(DB_Connection.book, DB_Connection.user, DB_Connection.pass);
             PreparedStatement valid = connection.prepareStatement("SELECT * FROM " + DB_Connection.BookTable +
                     " WHERE Id = ? AND Title = ?")) {

            valid.setInt(1, Id);
            valid.setString(2, Title);

            // Store the result to check if the query returned any rows
            ResultSet rs = valid.executeQuery();
            return rs.next(); // Returns true if book exists, false otherwise

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean delete(int ID) {
        try (Connection connection = DriverManager.getConnection(DB_Connection.book, DB_Connection.user, DB_Connection.pass);
             // Remove the * from DELETE query - it's not needed
             PreparedStatement del = connection.prepareStatement("DELETE FROM " + DB_Connection.BookTable +
                     " WHERE Id = ?")) {

            del.setInt(1, ID);
            int rowsAffected = del.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Row deleted Successfully");
                return true;
            } else {
                System.out.println("No rows found with ID: " + ID);
                return false;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}