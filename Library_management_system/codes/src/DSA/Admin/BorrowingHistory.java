package DSA.Admin;

import DSA.Objects.Books;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BorrowingHistory {

    // Method to add borrowed history record
    public static boolean BorrowedHistory(int ID, String userName, String booktitle, String Author, int copies, String Status) {
        try (Connection connection = DriverManager.getConnection(DB_Connection.BorrowedHistory, DB_Connection.user, DB_Connection.pass)) {
            // Start transaction
            connection.setAutoCommit(false);

            try {
                // Add timestamp to history record
                String insertSQL = "INSERT INTO " + DB_Connection.HistoryTable +
                        "(Id, UserName, BookName, Author, Copies, Status) " +
                        "VALUES(?, ?, ?, ?, ?)";

                try (PreparedStatement register = connection.prepareStatement(insertSQL)) {
                    register.setInt(1, ID);
                    register.setString(2, userName);
                    register.setString(3, booktitle);
                    register.setString(4, Author);
                    register.setInt(5, copies);
                    register.setString(6, Status);

                    register.executeUpdate();
                    connection.commit();
                    return true;
                }
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("Error inserting borrowed history: " + e.getMessage());
            return false;
        }
    }

    // Enhanced method to load history by user with proper date handling
    public static List<Borrowed_requests.BorrowRequest> LoadHistoryByUser(int userId) {
        List<Borrowed_requests.BorrowRequest> userHistory = new ArrayList<>();
        String query = "SELECT h.*, b.Genre, b.Publish_date, b.Copies as AvailableCopies, " +
                "b.Total_copies, h.TransactionDate " +
                "FROM " + DB_Connection.HistoryTable + " h " +
                "LEFT JOIN Books.AddedBooks b ON h.Id = b.Id " +
                "WHERE h.UserName = ?";

        try (Connection connection = DriverManager.getConnection(DB_Connection.BorrowedHistory, DB_Connection.user, DB_Connection.pass);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Books book = new Books(
                            resultSet.getInt("Id"),
                            resultSet.getString("BookName"),
                            resultSet.getString("Genre"),
                            resultSet.getString("Author"),
                            resultSet.getDate("Publish_date"),
                            resultSet.getInt("AvailableCopies"),
                            resultSet.getInt("Total_copies")
                    );

                    Borrowed_requests.BorrowRequest request = new Borrowed_requests.BorrowRequest(
                            book,
                            resultSet.getString("UserName"),
                            resultSet.getInt("Copies"),
                            resultSet.getString("Status"),
                            resultSet.getTimestamp("TransactionDate").toLocalDateTime()
                    );
                    userHistory.add(request);
                }
            }
            return userHistory;

        } catch (SQLException e) {
            System.err.println("Error loading user borrow history: " + e.getMessage());
            return new ArrayList<>(); // Return empty list instead of throwing exception
        }
    }

    // Method to update history status
    public static boolean updateHistoryStatus(int bookId, String userName, String newStatus) {
        String updateSQL = "UPDATE " + DB_Connection.HistoryTable +
                " SET Status = ?, TransactionDate = NOW() " +
                "WHERE Id = ? AND UserName = ? AND Status != ?";

        try (Connection conn = DriverManager.getConnection(DB_Connection.BorrowedHistory, DB_Connection.user, DB_Connection.pass);
             PreparedStatement stmt = conn.prepareStatement(updateSQL)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, bookId);
            stmt.setString(3, userName);
            stmt.setString(4, newStatus);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating history status: " + e.getMessage());
            return false;
        }
    }
    // Load all borrowing and return history
    public static List<Borrowed_requests.BorrowRequest> LoadAllHistory() {
        List<Borrowed_requests.BorrowRequest> historyList = new ArrayList<>();
        String query = "SELECT * FROM " + DB_Connection.HistoryTable;

        try (Connection connection = DriverManager.getConnection(DB_Connection.BorrowedHistory, DB_Connection.user, DB_Connection.pass);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                // Fetch the Book object using ISBN or ID
                int isbn = resultSet.getInt("Id");  // Assuming 'Id' is ISBN, or adjust accordingly.
                String bookTitle = resultSet.getString("BookName");
                String author = resultSet.getString("Author");
                int copies = resultSet.getInt("Copies");
                String status = resultSet.getString("Status");
                String userName = resultSet.getString("UserName");

                // Fetch the Books object
                Books book = fetchBookByISBN(isbn);

                // Create BorrowRequest if book is found
                if (book != null) {
                    Borrowed_requests.BorrowRequest borrowedRequest = new Borrowed_requests.BorrowRequest(
                            book, userName, copies, status, LocalDateTime.now()  // Use current date or add borrowReqDate if available
                    );
                    historyList.add(borrowedRequest);
                }
            }
            return historyList;

        } catch (SQLException e) {
            System.err.println("Error loading borrow history: " + e.getMessage());
            throw new RuntimeException("Failed to load borrow history", e);
        }
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
                                book, resultSet.getString("UserName"), copies, status, LocalDateTime.now()
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
    public static boolean checkBorrowExists(int ID, int bookRequestID) {
        String query = "SELECT COUNT(*) FROM " + DB_Connection.HistoryTable + " WHERE Id = ? AND bookRequestID = ?";

        try (Connection connection = DriverManager.getConnection(DB_Connection.BorrowedHistory, DB_Connection.user, DB_Connection.pass);
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, ID);
            stmt.setInt(2, bookRequestID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error checking borrow record: " + e.getMessage());
            throw new RuntimeException("Failed to check borrow record", e);
        }
    }

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