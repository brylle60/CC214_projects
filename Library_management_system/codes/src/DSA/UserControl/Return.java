package DSA.UserControl;

import DSA.Admin.Borrowed_requests;
import DB.DB_Connection;
import DSA.Objects.Books;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

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

            // 1. Verify book is borrowed by this user
            Books book = findBorrowedBook(conn, bookTitle, userName);
            if (book == null) {
                return new ReturnResult(false,
                        "No record found of this book being borrowed by " + userName, false);
            }

            // 2. Check if book is overdue
            boolean isOverdue = isBookOverdue(String.valueOf(book.getBorrowDate()));

            // 3. Update book status
            updateBookStatus(conn, book);

            // 4. Update borrowing history
            updateBorrowingHistory(conn, book, userName, "RETURNED");

            // 5. Notify waitlisted users if any
            notifyWaitlistedUsers(book);

            String message = isOverdue ?
                    "Book returned successfully. Note: This book was returned after the 2-week borrow period." :
                    "Book returned successfully";

            return new ReturnResult(true, message, isOverdue);
        } catch (SQLException e) {
            return new ReturnResult(false,
                    "Error processing return: " + e.getMessage(), false);
        }
    }

    private static Books findBorrowedBook(Connection conn, String bookTitle,
                                          String userName) throws SQLException {
        String query = "SELECT * FROM " + DB_Connection.HistoryTable +
                " WHERE BookName = ? AND UserName = ? AND Status = 'BORROWED'";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, bookTitle);
            stmt.setString(2, userName);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Books book = new Books();
                book.setTitle(rs.getString("Title"));
                book.setAuthor(rs.getString("Author"));
                book.setBorrowDate(LocalDateTime.parse(rs.getString("BorrowDate")));
                return book;
            }
        }
        return null;
    }

    private static boolean isBookOverdue(String borrowDate) {
        LocalDate borrowedDate = LocalDate.parse(borrowDate);
        LocalDate currentDate = LocalDate.now();
        long daysLoaned = ChronoUnit.DAYS.between(borrowedDate, currentDate);
        return daysLoaned > OVERDUE_DAYS_LIMIT;
    }

    private static void updateBookStatus(Connection conn, Books book)
            throws SQLException {
        String query = "UPDATE Books SET Available = true, AvailableCopies = " +
                "AvailableCopies + 1, Borrower = NULL WHERE Title = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, book.getTitle());
            stmt.executeUpdate();
        }
    }

    private static void updateBorrowingHistory(Connection conn, Books book,
                                               String userName, String status) throws SQLException {
        String query = "UPDATE " + DB_Connection.HistoryTable +
                " SET Status = ? WHERE BookName = ? AND UserName = ? AND Status = 'BORROWED'";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setString(2, book.getTitle());
            stmt.setString(3, userName);
            stmt.executeUpdate();
        }
    }

    private static void notifyWaitlistedUsers(Books book) {
        // Get waitlisted requests for this book
        List<Borrowed_requests.BorrowRequest> pendingRequests =
                Borrowed_requests.getPendingRequests().stream()
                        .filter(req -> req.getTitle().equals(book.getTitle()))
                        .sorted((r1, r2) -> r1.getBorrowReqDate().compareTo(r2.getBorrowReqDate()))
                        .toList();

        if (!pendingRequests.isEmpty()) {
            // Notify first person in waitlist
            Borrowed_requests.BorrowRequest nextRequest = pendingRequests.get(0);
            // In a real system, this would send an email or notification
            System.out.println("Notification sent to " + nextRequest.getUser() +
                    ": The book '" + book.getTitle() + "' is now available.");
        }
    }

    public static List<Books> getOverdueBooks() {
        List<Books> overdueBooks = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(
                DB_Connection.BorrowedHistory,
                DB_Connection.user,
                DB_Connection.pass)) {

            String query = "SELECT * FROM " + DB_Connection.HistoryTable +
                    " WHERE Status = 'BORROWED' AND " +
                    "DATEDIFF(CURRENT_DATE, BorrowDate) > ?";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, OVERDUE_DAYS_LIMIT);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    Books book = new Books();
                    book.setTitle(rs.getString("BookName"));
                    book.setAuthor(rs.getString("Author"));
                    book.setBorrower(rs.getString("UserName"));
                    book.setBorrowDate(LocalDateTime.parse(rs.getString("BorrowDate")));
                    overdueBooks.add(book);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving overdue books: " +
                    e.getMessage());
        }
        return overdueBooks;
    }
    public static void main(String[] args) {
        // Test data
        String bookTitle = "Java Programming";
        String userName = "TestStudent";

        System.out.println("=== Testing Library Return System ===\n");

        // Test 1: Process a return
        System.out.println("Test 1: Processing book return");
        ReturnResult result = processReturn(bookTitle, userName);
        System.out.println("Result: " + result.getMessage());
        System.out.println("Success: " + result.isSuccess());
        System.out.println("Is Overdue: " + result.isOverdue());
        System.out.println();

        // Test 2: Try to return a book that wasn't borrowed
        System.out.println("Test 2: Attempting to return non-borrowed book");
        result = processReturn("NonExistentBook", userName);
        System.out.println("Result: " + result.getMessage());
        System.out.println();

        // Test 3: Check overdue books
        System.out.println("Test 3: Checking overdue books");
        List<Books> OverdueBooks = getOverdueBooks();
        if (OverdueBooks.isEmpty()) {
            System.out.println("No overdue books found.");
        } else {
            System.out.println("Overdue books found:");
            for (Books book : OverdueBooks) {
                System.out.println("- Title: " + book.getTitle() +
                        ", Borrower: " + book.getBorrower() +
                        ", Borrow Date: " + book.getBorrowDate());
            }
        }
        System.out.println();

        // Test 4: Test database connection
        System.out.println("Test 4: Testing database connection");
        try (Connection conn = DriverManager.getConnection(
                DB_Connection.BorrowedHistory,
                DB_Connection.user,
                DB_Connection.pass)) {
            System.out.println("Database connection successful!");
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
    }
}

