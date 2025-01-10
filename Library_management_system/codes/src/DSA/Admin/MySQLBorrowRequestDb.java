package DSA.Admin;

import DSA.Objects.Books;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Queue;

public class MySQLBorrowRequestDb {

    // Add a new borrow request to the database
    public static boolean addRequest(int userId, int bookId, int copies, String status) {
        String sql = "INSERT INTO " + DB_Connection.RequestTable + " (user_id, book_id, request_date, status, copies) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(
                DB_Connection.BorrowedHistory, DB_Connection.user, DB_Connection.pass);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, bookId);
            pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setString(4, status);  // Set the status parameter
            pstmt.setInt(5, copies);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding borrow request: " + e.getMessage());
            return false;
        }
    }

    // Load pending requests from database into queue
    public static Queue<Borrowed_requests.BorrowRequest> loadPendingRequestsIntoQueue() {
        Queue<Borrowed_requests.BorrowRequest> requestQueue = new LinkedList<>();

        String sql = "SELECT r.user_id, r.book_id, r.request_date, " +
                "r.status, r.copies, b.* FROM requestTable r " +
                "JOIN " + DB_Connection.RequestTable + " b ON r.book_id = b.ISBN " +
                "WHERE r.status = 'PENDING' " +
                "ORDER BY r.request_date ASC";

        try (Connection conn = DriverManager.getConnection(
                DB_Connection.BorrowedHistory, DB_Connection.user, DB_Connection.pass);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // Create Books object
                Books book = new Books(
                        rs.getInt("ISBN"),
                        rs.getString("Title"),
                        rs.getString("Genre"),
                        rs.getString("Author"),
                        rs.getDate("DatePublished"),
                        rs.getInt("AvailableCopy"),
                        rs.getInt("TotalCopy")
                );

                // Create BorrowRequest with the Books object
                Borrowed_requests.BorrowRequest request = new Borrowed_requests.BorrowRequest(
                        book,
                        getUserNameById(rs.getInt("user_id")), // Get username from user_id
                        rs.getInt("copies"),
                        rs.getString("status"),
                        rs.getTimestamp("request_date").toLocalDateTime()
                );
                requestQueue.offer(request);
            }
        } catch (SQLException e) {
            System.err.println("Error loading pending requests: " + e.getMessage());
        }
        return requestQueue;
    }

    // Update request status
    public static boolean updateRequestStatus(int requestId, String status, int userId, String bookTitle) {
        String sql = "UPDATE " + DB_Connection.RequestTable + " SET status = ? WHERE request_id = ?";
        String historySql = "UPDATE borrowing_history SET status = ? WHERE user_id = ? AND book_title = ? AND status = 'PENDING'";

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_Connection.book, DB_Connection.user, DB_Connection.pass);
            conn.setAutoCommit(false);  // Start transaction

            // Update request status
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, status);
                pstmt.setInt(2, requestId);
                pstmt.executeUpdate();
            }

            // Update history status
            try (PreparedStatement pstmt = conn.prepareStatement(historySql)) {
                pstmt.setString(1, status);
                pstmt.setInt(2, userId);
                pstmt.setString(3, bookTitle);
                pstmt.executeUpdate();
            }

            conn.commit();  // Commit transaction
            return true;
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();  // Rollback on error
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Error updating request status: " + e.getMessage());
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    // Helper method to get username by user_id
    private static String getUserNameById(int userId) {
        String sql = "SELECT last_name FROM users WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(
                DB_Connection.book, DB_Connection.user, DB_Connection.pass);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("last_name");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching username: " + e.getMessage());
        }
        return "Unknown User";
    }
}