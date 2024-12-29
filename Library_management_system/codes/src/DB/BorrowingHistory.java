package DB;

import Books.Borrowed_requests;

import java.sql.*;
import java.util.List;

public class BorrowingHistory {
public static boolean BorrowedHistory(int ID, String userName, String booktitle, String Author, int copies, String Status){
    try (Connection connection = DriverManager.getConnection(DB_Connection.BorrowedHistory, DB_Connection.user, DB_Connection.pass);
         PreparedStatement register = connection.prepareStatement("INSERT INTO " + DB_Connection.HistoryTable +
                 "(Id, UserName, BookName, Author, Copies, Status) VALUES(?, ?, ?, ?, ?, ?)"))  {

        register.setInt(1, ID);
        register.setString(2, userName);
        register.setString(3, booktitle);
        register.setString(4, Author);
        register.setInt(5, copies);
        register.setString(6, Status);

        register.executeUpdate();
        return true;
    } catch (RuntimeException | SQLException e) {
        throw new RuntimeException(e);
    }
}

// public static List<Borrowed_requests> LoadHistory() {
//        List<Borrowed_requests> historyList = new ArrayList<>();
//
//        try (Connection connection = DriverManager.getConnection(
//                DB_Connection.BorrowedHistory, DB_Connection.user, DB_Connection.pass);
//             PreparedStatement statement = connection.prepareStatement(SELECT_HISTORY);
//             ResultSet resultSet = statement.executeQuery()) {
//
//            while (resultSet.next()) {
//                Borrowed_requests borrowedRequests = new Borrowed_requests(
//                    resultSet.getInt("Id"),
//                    resultSet.getString("UserName"),
//                    resultSet.getString("BookName"),
//                    resultSet.getString("Author"),
//                    resultSet.getInt("Copies"),
//                    resultSet.getString("Status")
//                );
//                historyList.add(borrowedRequests);
//            }
//
//            return historyList;
//
//        } catch (SQLException e) {
//            System.err.println("Error loading borrow history: " + e.getMessage());
//            throw new RuntimeException("Failed to load borrow history", e);
//        }
//    }
//
//    // Utility method to check if a borrow record exists
//    public static boolean checkBorrowExists(int ID, String bookName) {
//        String query = "SELECT COUNT(*) FROM " + DB_Connection.HistoryTable +
//                      " WHERE Id = ? AND BookName = ?";
//
//        try (Connection connection = DriverManager.getConnection(
//                DB_Connection.BorrowedHistory, DB_Connection.user, DB_Connection.pass);
//             PreparedStatement stmt = connection.prepareStatement(query)) {
//
//            stmt.setInt(1, ID);
//            stmt.setString(2, bookName);
//
//            try (ResultSet rs = stmt.executeQuery()) {
//                if (rs.next()) {
//                    return rs.getInt(1) > 0;
//                }
//            }
//            return false;
//
//        } catch (SQLException e) {
//            System.err.println("Error checking borrow record: " + e.getMessage());
//            throw new RuntimeException("Failed to check borrow record", e);
//        }
//    }
    

}
