package DSA.Admin;

import DSA.Objects.Books;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Queue;

public class MySQLBorrowRequestDb {

    // Add a new borrow request to the database
    public static boolean addRequest(int userId, int bookId, int copies) {
        String sql = "INSERT INTO borrow_requests (user_id, book_id, request_date, status, copies) " +
                "VALUES (?, ?, ?, 'PENDING', ?)";

        try (Connection conn = DriverManager.getConnection(
                DB_Connection.book, DB_Connection.user, DB_Connection.pass);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, bookId);
            pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(4, copies);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding borrow request: " + e.getMessage());
            return false;
        }
    }

    // Load pending requests from database into queue
    public static Queue<Borrowed_requests.BorrowRequest> loadPendingRequestsIntoQueue() {
        Queue<Borrowed_requests.BorrowRequest> requestQueue = new LinkedList<>();

        String sql = "SELECT r.request_id, r.user_id, r.book_id, r.request_date, " +
                "r.status, r.copies, b.* FROM borrow_requests r " +
                "JOIN " + DB_Connection.BookTable + " b ON r.book_id = b.ISBN " +
                "WHERE r.status = 'PENDING' " +
                "ORDER BY r.request_date ASC";

        try (Connection conn = DriverManager.getConnection(
                DB_Connection.book, DB_Connection.user, DB_Connection.pass);
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
    public static boolean updateRequestStatus(int requestId, String status) {
        String sql = "UPDATE borrow_requests SET status = ? WHERE request_id = ?";

        try (Connection conn = DriverManager.getConnection(
                DB_Connection.book, DB_Connection.user, DB_Connection.pass);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, requestId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating request status: " + e.getMessage());
            return false;
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