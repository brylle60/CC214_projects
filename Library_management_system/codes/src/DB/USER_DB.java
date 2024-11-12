package DB;
import User.users;

import java.sql.*;
import java.util.*;

public class USER_DB {


    public static boolean Register(String password, int Id, String Name, String email){
            try{

                Connection connection = DriverManager.getConnection(DB_Connection.url,DB_Connection.user, DB_Connection.pass);
                PreparedStatement register = connection.prepareStatement("INSERT INTO "+DB_Connection.tab+"(Id, name, password, email)"+"VALUES(?, ?, ?, ?)");
                register.setInt(1,Id);
                register.setString(2, Name);
                register.setString(3, password);
                register.setString(4, email);

                register.executeUpdate();

                return true;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            //return false;
        }

        public static boolean validate(int Id, String Name, String password){
        try {
            Connection connection = DriverManager.getConnection(DB_Connection.url, DB_Connection.user, DB_Connection.pass);
            PreparedStatement valid = connection.prepareStatement("SELECT * FROM "+DB_Connection.tab+" WHERE Id = ? AND password = ?");

            valid.setInt(1, Id);
            valid.setString(2, Name);
            valid.setString(3, password);

            valid.executeQuery();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static boolean checkuser(int Id){
        try {
            Connection connection = DriverManager.getConnection(DB_Connection.url, DB_Connection.user, DB_Connection.pass);
            PreparedStatement check = connection.prepareStatement("SELECT * FROM "+DB_Connection.tab+" WHERE Id = ?");

            check.setInt(1, Id);

            check.executeQuery();
            return true;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    public static Hashtable<Integer, users> hash() {
        Hashtable<Integer, users> hashmap = new Hashtable<>();

        try (Connection connection = DriverManager.getConnection(DB_Connection.url, DB_Connection.user, DB_Connection.pass);
             PreparedStatement books_borrowed = connection.prepareStatement("SELECT * FROM " + DB_Connection.tab + " WHERE Id IS NOT NULL");
             ResultSet result = books_borrowed.executeQuery()) {

            while (result.next()) {
                int Id = result.getInt("Id");
                String Name = result.getString("name");
                String pass = result.getString("password");
                String email = result.getString("email");

                users user = new users(Id, Name, pass, email);
                hashmap.put(Id, user);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        }

        return hashmap;
    }
}
