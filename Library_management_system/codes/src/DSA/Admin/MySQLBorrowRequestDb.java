package DSA.Admin;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class MySQLBorrowRequestDb {

    public static boolean addRequest(int userId, int bookId, int copies) {
        String sql = "INSERT INTO " + DB_Connection.RequestTable +
                " (user_id, book_id, request_date, status, copies) " +
                "VALUES (?, ?, ?, 'PENDING', ?)";

        try (Connection conn = DriverManager.getConnection(DB_Connection.book, DB_Connection.user, DB_Connection.pass);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, bookId);
            pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(4, copies);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Queue<Borrowed_requests.BorrowRequest> loadPendingRequestsIntoQueue() {
        Queue<Borrowed_requests.BorrowRequest> requestQueue = new LinkedList<>();

        String sql = "SELECT r.request_id, u.last_name, b.Title, b.Author, r.copies, " +
                "r.request_date, r.status FROM " + DB_Connection.RequestTable + " r " +
                "JOIN users u ON r.user_id = u.id " +
                "JOIN " + DB_Connection.BookTable + " b ON r.book_id = b.Id " +
                "WHERE r.status = 'PENDING' " +
                "ORDER BY r.request_date ASC";  // Order by date to maintain FIFO

        try (Connection conn = DriverManager.getConnection(DB_Connection.book, DB_Connection.user, DB_Connection.pass);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Borrowed_requests.BorrowRequest request = new Borrowed_requests.BorrowRequest(
                        rs.getInt("request_id"),
                        rs.getString("last_name"),
                        rs.getString("Title"),
                        rs.getString("Author"),
                        rs.getInt("copies"),
                        rs.getString("status"),
                        rs.getTimestamp("request_date").toLocalDateTime()
                );
                requestQueue.offer(request);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requestQueue;
    }

    public static boolean updateRequestStatus(int requestId, String status) {
        String sql = "UPDATE " + DB_Connection.RequestTable +
                " SET status = ? WHERE request_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_Connection.book, DB_Connection.user, DB_Connection.pass);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, requestId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}