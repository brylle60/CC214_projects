package DSA.UserControl;

import DSA.Admin.BorrowingHistory;
import DSA.Admin.DB_Connection;
import DSA.Objects.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Return {
    private static final int OVERDUE_DAYS_LIMIT = 14; // 2 weeks

    public static class ReturnResult {
        private final boolean success;
        private final String message;
        private final boolean isOverdue;

        public ReturnResult(boolean success, String message, boolean isOverdue) {
            this.success = success;
            this.message = message;
            this.isOverdue = isOverdue;
        }   

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public boolean isOverdue() { return isOverdue; }
    }

    public static ReturnResult processReturn(String bookTitle, String userName) {
        try (Connection conn = DriverManager.getConnection(
                DB_Connection.BorrowedHistory,
                DB_Connection.user,
                DB_Connection.pass)) {

            // Find the borrowed book record
            BorrowedHistory borrowedBook = findBorrowedBook(conn, bookTitle, userName);
            if (borrowedBook == null) {
                return new ReturnResult(false,
                        "No record found of this book being borrowed by " + userName, false);
            }

            // Check if book is overdue
            boolean isOverdue = isBookOverdue(borrowedBook.getBorrowedDate());

            // Update book status and history
            updateBookStatus(conn, bookTitle, borrowedBook.getBorrowedCopy());
            updateBorrowingHistory(conn, bookTitle, userName);

            String message = isOverdue ?
                    "Book returned successfully. Note: This book was returned after the 2-week loan period." :
                    "Book returned successfully";

            return new ReturnResult(true, message, isOverdue);

        } catch (SQLException e) {
            return new ReturnResult(false,
                    "Error processing return: " + e.getMessage(), false);
        }
    }

    private static BorrowedHistory findBorrowedBook(Connection conn, String bookTitle, String userName)
            throws SQLException {
        String query = "SELECT * FROM " + DB_Connection.HistoryTable +
                " WHERE BookName = ? AND UserName = ? AND Status = 'BORROWED'";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, bookTitle);
            stmt.setString(2, userName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new BorrowedHistory(
                            rs.getInt("Id"),
                            rs.getString("UserName"),
                            rs.getString("BookName"),
                            rs.getString("Author"),
                            rs.getInt("Copies"),
                            rs.getString("Status")
                    );
                }
            }
        }
        return null;
    }

    private static boolean isBookOverdue(LocalDateTime borrowDate) {
        if (borrowDate == null) {
            return false;
        }

        LocalDateTime currentDate = LocalDateTime.now();
        long daysLoaned = ChronoUnit.DAYS.between(borrowDate, currentDate);
        return daysLoaned > OVERDUE_DAYS_LIMIT;
    }

    private static void updateBookStatus(Connection conn, String bookTitle, int returnedCopies)
            throws SQLException {
        String query = "UPDATE Books SET Copies = Copies + ? WHERE Title = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, returnedCopies);
            stmt.setString(2, bookTitle);
            stmt.executeUpdate();
        }
    }

    private static void updateBorrowingHistory(Connection conn, String bookTitle, String userName)
            throws SQLException {
        String query = "UPDATE " + DB_Connection.HistoryTable +
                " SET Status = ?, ReturnDate = ? WHERE BookName = ? AND UserName = ? AND Status = 'BORROWED'";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "RETURNED");
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setString(3, bookTitle);
            stmt.setString(4, userName);
            stmt.executeUpdate();
        }
    }

    public static void main(String[] args) {
        // Test the return functionality
        System.out.println("=== Testing Library Return System ===\n");

        // Test cases
        String[][] testCases = {
                {"Java Programming", "TestStudent"},
                {"NonExistentBook", "TestStudent"},
                {"Database Design", "AnotherStudent"}
        };

        for (int i = 0; i < testCases.length; i++) {
            System.out.println("Test " + (i + 1) + ": Processing return for '" +
                    testCases[i][0] + "' by " + testCases[i][1]);

            ReturnResult result = processReturn(testCases[i][0], testCases[i][1]);
            System.out.println("Result: " + result.getMessage());
            System.out.println("Success: " + result.isSuccess());
            System.out.println("Is Overdue: " + result.isOverdue());
            System.out.println();
        }
    }
}