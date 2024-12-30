package DSA.Admin;

import DSA.Objects.Books;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    //todo applly this in the admin control class

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
    public static List<Books> LoadBooks(){
        List<Books> book = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(DB_Connection.book, DB_Connection.user, DB_Connection.pass);
            PreparedStatement BoookInfo = connection.prepareStatement("SELECT * FROM "+DB_Connection.BookTable);

            ResultSet resultSet = BoookInfo.executeQuery();

            while(resultSet.next()){
                int id = resultSet.getInt("Id");
                String title = resultSet.getString("Title");
                String genre = resultSet.getString("Genre");
                String author = resultSet.getString("Author");
                Date datepub = resultSet.getDate("Date Published");
                int copies = resultSet.getInt("Copies");
                int totalCopies = resultSet.getInt("Total Copies");

                Books books = new Books(id, title, author, datepub, copies, totalCopies);

            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        return book;
    }
    public static void updateBookCopies(int isbn, int availableCopies) {
        try (Connection connection = DriverManager.getConnection(DB_Connection.book, DB_Connection.user, DB_Connection.pass);
             PreparedStatement update = connection.prepareStatement(
                     "UPDATE " + DB_Connection.BookTable + " SET Copies = ? WHERE Id = ?")) {

            update.setInt(1, availableCopies);
            update.setInt(2, isbn);
            update.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update book copies: " + e.getMessage(), e);
        }
    }
}