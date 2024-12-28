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

        register.executeQuery();
        return true;
    } catch (RuntimeException | SQLException e) {
        throw new RuntimeException(e);
    }
}
public static List<Borrowed_requests> LoadHistory(){
    try{
        Connection connection = DriverManager.getConnection(DB_Connection.BorrowedHistory, DB_Connection.user, DB_Connection.pass);
        PreparedStatement statement = connection.prepareStatement("ELECT Id, UserName, BookName, Author, Copies, Status FROM " + DB_Connection.HistoryTable);
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()){
            int Id = resultSet.getInt("Id");
            String UserName = resultSet.getString("UserName");
            String BookName = resultSet.getString("BookName");
            String Author = resultSet.getString("Author");
            int copies = resultSet.getInt("copies");
            String status = resultSet.getString("Status");

            // todo change the borrowed request constructors wahhahahahahahahha

            Borrowed_requests borrowedRequests = new Borrowed_requests();

            LoadHistory().add(borrowedRequests);
        }

    } catch (Exception e) {
        throw new RuntimeException(e);
    }
    return LoadHistory();
}
}
