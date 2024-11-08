package DB;
import java.io.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;

public class USER_DB {


    public static boolean Register(String password, int Id, String Name){
            try{

                Connection connection = DriverManager.getConnection(DB_Connection.url,DB_Connection.user, DB_Connection.pass);
                PreparedStatement register = connection.prepareStatement("INSERT INTO "+DB_Connection.tab+"(Id, name, password)"+"VALUES(?, ?, ?)");
                register.setInt(1,Id);
                register.setString(2, Name);
                register.setString(3, password);

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
}
