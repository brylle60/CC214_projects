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


}
