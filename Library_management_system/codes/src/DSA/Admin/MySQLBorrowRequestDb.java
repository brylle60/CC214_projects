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


        // Updated SQL query to use correct database references
        String sql = "SELECT r.user_id, r.book_id, r.request_date, " +
                "r.status, r.copies, b.* FROM " + DB_Connection.RequestTable + " r " +
                "JOIN  Books.AddedBooks b ON r.book_id = b.Id " +
                "WHERE r.status = 'PENDING' " +
                "ORDER BY r.request_date ASC";

        try (Connection conn = DriverManager.getConnection(
                DB_Connection.BorrowedHistory, DB_Connection.user, DB_Connection.pass)) {

            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    try {
                        // Create Books object with correct column names
                        Books book = new Books(
                                rs.getInt("Id"),
                                rs.getString("Title"),
                                rs.getString("Genre"),
                                rs.getString("Author"),
                                rs.getDate("Publish_date"),
                                rs.getInt("Copies"),
                                rs.getInt("Total_copies")
                        );

                        // Create BorrowRequest with the Books object
                        Borrowed_requests.BorrowRequest request = new Borrowed_requests.BorrowRequest(
                                book,
                                getUserNameById(rs.getInt("user_id")),
                                rs.getInt("copies"),
                                rs.getString("status"),
                                rs.getTimestamp("request_date").toLocalDateTime()
                        );
                        requestQueue.offer(request);
                    } catch (SQLException e) {
                        System.err.println("Error creating request object: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading pending requests: " + e.getMessage());
            e.printStackTrace();
        }
        return requestQueue;
    }

    // Update request status
    public static boolean updateRequestStatus(int requestId, String status, int userId) {
        String sql = "UPDATE " + DB_Connection.RequestTable + " SET status = ? WHERE user_id = ?";
        String historySql = "UPDATE requestTable SET status = ? WHERE user_id = ? AND status = 'PENDING'";

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_Connection.BorrowedHistory, DB_Connection.user, DB_Connection.pass);
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
        String sql = "SELECT lastName FROM users WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(
                DB_Connection.url, DB_Connection.user, DB_Connection.pass);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("lastName");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching username: " + e.getMessage());
        }
        return "Unknown User";
    }

}