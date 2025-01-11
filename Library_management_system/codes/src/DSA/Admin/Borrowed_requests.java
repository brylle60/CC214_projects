package DSA.Admin;

import DSA.Objects.Books;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Borrowed_requests {
    private static Queue<BorrowRequest> pendingRequests = new LinkedList<>();

    public static class BorrowRequest {
        private final Books book;
        private final String user;
        private final int copies;
        private String status;
        private final LocalDateTime borrowReqDate;
        private int id;

        public BorrowRequest(Books book, String user, int copies, String status, LocalDateTime borrowReqDate) {
            this.book = book;
            this.user = user;
            this.copies = copies;
            this.status = status;
            this.borrowReqDate = borrowReqDate;
            this.id = -1; // Default value, will be set when retrieved from database
        }

        // Getters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public Books getBook() { return book; }
        public String getUser() { return user; }
        public String getTitle() { return book.getTitle(); }
        public String getAuthor() { return book.getAuthor(); }
        public int getCopies() { return copies; }
        public String getStatus() { return status; }
        public LocalDateTime getBorrowReqDate() { return borrowReqDate; }
    }

    public static void initializeFromDatabase() {
        pendingRequests = MySQLBorrowRequestDb.loadPendingRequestsIntoQueue();
    }

    public static List<BorrowRequest> getPendingRequests() {
        return new ArrayList<>(pendingRequests);
    }

//    public static boolean confirmRequest(String bookTitle, String userName, int requestId) {
//        try {
//            // Find the request in pending requests
//            BorrowRequest request = null;
//            for (BorrowRequest req : pendingRequests) {
//                if (req.getId() == requestId) {
//                    request = req;
//                    break;
//                }
//            }
//
//            if (request == null) {
//                System.err.println("Request not found: " + requestId);
//                return false;
//            }
//
//            // Get the book details
//            Books book = request.getBook();
//
//            // Check if enough copies are available
//            if (book.getAvailableCopy() < request.getCopies()) {
//                System.err.println("Not enough copies available");
//                return false;
//            }
//
//            // Update book copies
//            int newCopies = book.getAvailableCopy() - request.getCopies();
//            MySQLbookDb.updateBookCopies(book.getISBN(), newCopies);
//
//            // Add to borrowing history
//            boolean historyAdded = BorrowingHistory.BorrowedHistory(
//                    book.getISBN(),
//                    userName,
//                    bookTitle,
//                    book.getAuthor(),
//                    request.getCopies(),
//                    "BORROWED"
//            );
//
//            if (!historyAdded) {
//                System.err.println("Failed to add to borrowing history");
//                return false;
//            }
//
//            // Update request status
//            boolean statusUpdated = MySQLBorrowRequestDb.updateRequestStatus(
//                    requestId,
//                    "BORROWED",
//                    book.getISBN(),
//                    bookTitle
//            );
//
//            if (!statusUpdated) {
//                System.err.println("Failed to update request status");
//                return false;
//            }
//
//            // Remove from pending queue
//            pendingRequests.remove(request);
//            return true;
//
//        } catch (Exception e) {
//            System.err.println("Error confirming request: " + e.getMessage());
//            e.printStackTrace();
//            return false;
//        }
//    }

//    public static boolean rejectRequest(String bookTitle, String userName) {
//        try {
//            // Find the request in pending requests
//            BorrowRequest request = null;
//            for (BorrowRequest req : pendingRequests) {
//                if (req.getTitle().equals(bookTitle) && req.getUser().equals(userName)) {
//                    request = req;
//                    break;
//                }
//            }
//
//            if (request == null) {
//                System.err.println("Request not found");
//                return false;
//            }
//
//            // Update request status to REJECTED
//            boolean statusUpdated = MySQLBorrowRequestDb.updateRequestStatus(
//                    request.getId(),
//                    "REJECTED",
//                    request.getBook().getISBN(),
//                    bookTitle
//            );
//
//            if (!statusUpdated) {
//                System.err.println("Failed to update request status");
//                return false;
//            }
//
//            // Remove from pending queue
//            pendingRequests.remove(request);
//            return true;
//
//        } catch (Exception e) {
//            System.err.println("Error rejecting request: " + e.getMessage());
//            e.printStackTrace();
//            return false;
//        }
//    }

//    2nd version
//    public static boolean confirmRequest(String bookTitle, String userName, int requestId) {
//        try {
//            // Update request status to BORROWED
//            boolean requestUpdated = MySQLBorrowRequestDb.updateRequestStatus(
//                    requestId,
//                    "BORROWED",
//                    getUserId(userName),  // You'll need to implement this helper method
//                    bookTitle
//            );
//
//            if (requestUpdated) {
//                // Update the book's available copies
//                Books book = findBookByTitle(bookTitle);  // You'll need to implement this helper method
//                if (book != null) {
//                    int newAvailableCopies = book.getAvailableCopy() - 1;
//                    //MySQLbookDb.updateBookCopies(book.getISBN(), newAvailableCopies);
//                    return true;
//                }
//            }
//            return false;
//        } catch (Exception e) {
//            System.err.println("Error confirming request: " + e.getMessage());
//            return false;
//        }
//    }

//    public static boolean rejectRequest(String bookTitle, String userName) {
//        try {
//            // Find the pending request
//            Queue<BorrowRequest> pendingRequests = MySQLBorrowRequestDb.loadPendingRequestsIntoQueue();
//            BorrowRequest targetRequest = null;
//
//            for (BorrowRequest request : pendingRequests) {
//                if (request.getTitle().equals(bookTitle) && request.getUser().equals(userName)) {
//                    targetRequest = request;
//                    break;
//                }
//            }
//
//            if (targetRequest != null) {
//                return MySQLBorrowRequestDb.updateRequestStatus(
//                        targetRequest.getId(),
//                        "REJECTED",
//                        getUserId(userName),
//                        bookTitle
//                );
//            }
//            return false;
//        } catch (Exception e) {
//            System.err.println("Error rejecting request: " + e.getMessage());
//            return false;
//        }
//    }

    // Helper method to get user ID
    private static int getUserId(String userName) {
        // Implement database query to get user ID from userName
        // This is a placeholder implementation
        String query = "SELECT id FROM users WHERE lastName = ?";
        try (Connection conn = DriverManager.getConnection(DB_Connection.url, DB_Connection.user, DB_Connection.pass);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.err.println("Error getting user ID: " + e.getMessage());
        }
        return -1;
    }
//    public static boolean confirmRequest(String bookTitle, String userName, int requestId) {
//        try {
//            // 1. Get book details and update available copies
//            Books book = findBookByTitle(bookTitle);
//            if (book == null || book.getAvailableCopy() < 1) {
//                return false;
//            }
//
//            // 2. Add to borrowing history
//            boolean historyAdded = BorrowingHistory.BorrowedHistory(
//                    book.getISBN(),
//                    userName,
//                    bookTitle,
//                    book.getAuthor(),
//                    1,  // Default to 1 copy for now
//                    "BORROWED"
//            );
//
//            if (!historyAdded) {
//                return false;
//            }
//
//            // 3. Update book copies in database
//            int newCopies = book.getAvailableCopy() - 1;
//            MySQLbookDb.updateBookCopies(book.getISBN(), newCopies);
//
//            // 4. Update request status in database
//            String updateQuery = "UPDATE " + DB_Connection.RequestTable +
//                    " SET status = 'BORROWED' WHERE request_id = ?";
//
//            try (Connection conn = DriverManager.getConnection(DB_Connection.BorrowedHistory,
//                    DB_Connection.user, DB_Connection.pass);
//                 PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
//
//                stmt.setInt(1, requestId);
//                stmt.executeUpdate();
//                return true;
//            }
//
//        } catch (SQLException e) {
//            System.err.println("Error confirming request: " + e.getMessage());
//            return false;
//        }
//    }
//
//    public static boolean rejectRequest(String bookTitle, String userName) {
//        try {
//            String updateQuery = "UPDATE " + DB_Connection.RequestTable +
//                    " SET status = 'REJECTED' WHERE book_title = ? AND user_name = ? AND status = 'PENDING'";
//
//            try (Connection conn = DriverManager.getConnection(DB_Connection.BorrowedHistory,
//                    DB_Connection.user, DB_Connection.pass);
//                 PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
//
//                stmt.setString(1, bookTitle);
//                stmt.setString(2, userName);
//                return stmt.executeUpdate() > 0;
//            }
//
//        } catch (SQLException e) {
//            System.err.println("Error rejecting request: " + e.getMessage());
//            return false;
//        }
//    }



    // Helper method to find book by title
    private static Books findBookByTitle(String title) {
        // Implement database query to get book details by title
        String query = "SELECT * FROM " + DB_Connection.BookTable + " WHERE Title = ?";
        try (Connection conn = DriverManager.getConnection(DB_Connection.book, DB_Connection.user, DB_Connection.pass);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, title);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Books(
                        rs.getInt("Id"),
                        rs.getString("Title"),
                        rs.getString("Genre"),
                        rs.getString("Author"),
                        rs.getDate("Publish_date"),
                        rs.getInt("Copies"),
                        rs.getInt("Total_copies")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error finding book: " + e.getMessage());
        }
        return null;
    }
}