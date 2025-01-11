package DSA.Admin;

import DSA.Objects.Books;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BorrowingHistory {

    // Method to add borrowed history record
    public static boolean BorrowedHistory(int ID, String userName, String bookTitle, String Author, int copies, String Status) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DB_Connection.BorrowedHistory, DB_Connection.user, DB_Connection.pass);
            connection.setAutoCommit(false);

            // First check if there's an existing record
            String checkSQL = "SELECT Status FROM " + DB_Connection.HistoryTable +
                    " WHERE Id = ? AND UserName = ? AND BookName = ? AND Status = 'PENDING'";

            try (PreparedStatement checkStmt = connection.prepareStatement(checkSQL)) {
                checkStmt.setInt(1, ID);
                checkStmt.setString(2, userName);
                checkStmt.setString(3, bookTitle);

                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    // Update existing record
                    String updateSQL = "UPDATE " + DB_Connection.HistoryTable +
                            " SET Status = ? " +
                            "WHERE Id = ? AND UserName = ? AND BookName = ? AND Status = 'PENDING'";

                    try (PreparedStatement updateStmt = connection.prepareStatement(updateSQL)) {
                        updateStmt.setString(1, Status);
                        updateStmt.setInt(2, ID);
                        updateStmt.setString(3, userName);
                        updateStmt.setString(4, bookTitle);
                        updateStmt.executeUpdate();
                    }
                } else {
                    // Insert new record
                    String insertSQL = "INSERT INTO " + DB_Connection.HistoryTable +
                            "(Id, UserName, BookName, Author, Copies, Status) " +
                            "VALUES(?, ?, ?, ?, ?, ?)";

                    try (PreparedStatement insertStmt = connection.prepareStatement(insertSQL)) {
                        insertStmt.setInt(1, ID);
                        insertStmt.setString(2, userName);
                        insertStmt.setString(3, bookTitle);
                        insertStmt.setString(4, Author);
                        insertStmt.setInt(5, copies);
                        insertStmt.setString(6, Status);
                        insertStmt.executeUpdate();
                    }
                }
            }

            connection.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Error in BorrowedHistory: " + e.getMessage());
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Enhanced method to load history by user with proper date handling
    public static List<Borrowed_requests.BorrowRequest> LoadHistoryByUser(int userId) {
        List<Borrowed_requests.BorrowRequest> userHistory = new ArrayList<>();
        String query = "SELECT h.*, b.Genre, b.Publish_date " +
                "FROM " + DB_Connection.HistoryTable + " h " +
                "LEFT JOIN Books.AddedBooks b ON h.BookName = b.Title " +
                "WHERE h.Id = ? ";

        try (Connection connection = DriverManager.getConnection(
                DB_Connection.BorrowedHistory, DB_Connection.user, DB_Connection.pass);
             PreparedStatement statement = connection.prepareStatement(query)) {

            System.out.println("Executing query for user ID: " + userId); // Debug log
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    // Create a Books object with available data
                    Books book = new Books(
                            resultSet.getInt("Id"),
                            resultSet.getString("BookName"),
                            resultSet.getString("Genre") != null ? resultSet.getString("Genre") : "Unknown",
                            resultSet.getString("Author"),
                            resultSet.getDate("Publish_date") != null ? resultSet.getDate("Publish_date") : new Date(0),
                            resultSet.getInt("Copies"),
                            resultSet.getInt("Copies") // Using Copies for total as well since it's not in history
                    );



                    Borrowed_requests.BorrowRequest request = new Borrowed_requests.BorrowRequest(
                            book,
                            resultSet.getString("UserName"),
                            resultSet.getInt("Copies"),
                            resultSet.getString("Status")
                    );
                    userHistory.add(request);
                    System.out.println("Added history record: " + request); // Debug log
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in LoadHistoryByUser: " + e.getMessage());
            e.printStackTrace();
        }
        return userHistory;
    }





    // Load borrowing history by status (Borrowed or Returned)
    public static List<Borrowed_requests.BorrowRequest> LoadHistoryByStatus(String status) {
        List<Borrowed_requests.BorrowRequest> sortedHistory = new ArrayList<>();
        String query = "SELECT * FROM " + DB_Connection.HistoryTable + " WHERE Status = ?";

        try (Connection connection = DriverManager.getConnection(DB_Connection.BorrowedHistory, DB_Connection.user, DB_Connection.pass);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, status);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int isbn = resultSet.getInt("Id");
                    String bookTitle = resultSet.getString("BookName");
                    String author = resultSet.getString("Author");
                    int copies = resultSet.getInt("Copies");

                    Books book = fetchBookByISBN(isbn);

                    if (book != null) {
                        Borrowed_requests.BorrowRequest borrowedRequest = new Borrowed_requests.BorrowRequest(
                                book, resultSet.getString("UserName"), copies, status
                        );
                        sortedHistory.add(borrowedRequest);
                    }
                }
            }
            return sortedHistory;

        } catch (SQLException e) {
            System.err.println("Error loading sorted borrow history: " + e.getMessage());
            throw new RuntimeException("Failed to load sorted borrow history", e);
        }
    }

    // Utility method to check if a borrow record exists

    public static Books fetchBookByISBN(int isbn) {
        String query = "SELECT * FROM " + DB_Connection.BookTable + " WHERE Id = ?";

        try (Connection connection = DriverManager.getConnection(DB_Connection.book, DB_Connection.user, DB_Connection.pass);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, isbn);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Date publishedDate = resultSet.getDate("Publish_date");
                    // Check if Publish_date is null
                    if (publishedDate == null) {
                        publishedDate = Date.valueOf("1900-01-01"); // Or some default value
                    }

                    return new Books(
                            resultSet.getInt("Id"),         // Changed from "ISBN" to "Id"
                            resultSet.getString("Title"),
                            resultSet.getString("Genre"),
                            resultSet.getString("Author"),
                            publishedDate,
                            resultSet.getInt("Copies"),     // Changed from "AvailableCopy" to "Copies"
                            resultSet.getInt("Total_copies") // Changed from "TotalCopy" to "Total_copies"
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching book: " + e.getMessage());
            throw new RuntimeException("Failed to fetch book", e);
        }
        return null;  // Return null if no book found
            }

}