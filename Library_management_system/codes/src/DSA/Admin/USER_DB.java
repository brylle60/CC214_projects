package DSA.Admin;

import DSA.Objects.users;
import java.sql.*;
import java.util.Hashtable;

public class USER_DB {
    private static Hashtable<Integer, users> userCache;

    public USER_DB() {
        this.userCache = new Hashtable<>();
    }

    //todo not yet fixed
    public static boolean add(users user) {
        try (Connection connection = DriverManager.getConnection(DB_Connection.url, DB_Connection.user, DB_Connection.pass);
             PreparedStatement register = connection.prepareStatement(
                     "INSERT INTO " + DB_Connection.tab +
                             "(Id, lastName, firstName, password, email, Gender, limit) VALUES(?, ?, ?, ?, ?, ?, ?)")) {

            register.setInt(1, user.getId());
            register.setString(2, user.getLastName());
            register.setString(3, user.getFirstName());
            register.setString(4, user.getPass());
            register.setString(5, user.getEmail());
            register.setString(6, user.getGender());
            register.setInt(7, user.getLimit());

            int rowsAffected = register.executeUpdate();

            if (rowsAffected > 0) {
                userCache.put(user.getId(), user);
                return true;
            }
            return false;

        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) {
                System.out.println("User with this ID already exists");
            } else {
                System.out.println("Error adding user: " + e.getMessage());
            }
            return false;
        }
    }
    //todo needs GUI for registration
    public static boolean validate(int id, String password) {
        try (Connection connection = DriverManager.getConnection(DB_Connection.url, DB_Connection.user, DB_Connection.pass);
             PreparedStatement valid = connection.prepareStatement(
                     "SELECT * FROM " + DB_Connection.tab +
                             " WHERE Id = ? AND password = ?")) {

            valid.setInt(1, id);
            valid.setString(2, password);

            ResultSet rs = valid.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Error validating user: " + e.getMessage());
            return false;
        }
    }

    //todo used to check for existing account
//    public static boolean checkUser(int id) {
//        // First check cache
//        if (userCache.containsKey(id)) {
//            return true;
//        }
//
//        try (Connection connection = DriverManager.getConnection(DB_Connection.url, DB_Connection.user, DB_Connection.pass);
//             PreparedStatement check = connection.prepareStatement(
//                     "SELECT * FROM " + DB_Connection.tab + " WHERE Id = ?")) {
//
//            check.setInt(1, id);
//            ResultSet rs = check.executeQuery();
//
//            if (rs.next()) {
//                users user = new users(
//                        rs.getInt("Id"),
//                        rs.getString("lastname"),
//                        rs.getString("firstname"),
//                        rs.getString("password"),
//                        rs.getString("email"),
//                        rs.getString("Gender"),
//                        rs.getInt("limit")
//                );
//                userCache.put(id, user);
//                return true;
//            }
//            return false;
//
//        } catch (SQLException e) {
//            System.out.println("Error checking user: " + e.getMessage());
//            return false;
//        }
//    }

    // todo used to display all users in table or for the future add delete user method
    public Hashtable<Integer, users> loadAllUsers() {
        try (Connection connection = DriverManager.getConnection(DB_Connection.url, DB_Connection.user, DB_Connection.pass);
             PreparedStatement stmt = connection.prepareStatement("SELECT * FROM " + DB_Connection.tab);
             ResultSet result = stmt.executeQuery()) {

            while (result.next()) {
                users user = new users(
                        result.getInt("Id"),
                        result.getString("lastname"),
                        result.getString("first name"),
                        result.getString("password"),
                        result.getString("email"),
                        result.getString("Gender"),
                        result.getInt("limit")
                );
                userCache.put(user.getId(), user);
            }

        } catch (SQLException e) {
            System.out.println("Error loading users: " + e.getMessage());
        }
        return userCache;
    }
    // Get a user from cache or database
    public users getUser(int id) {
        if (userCache.containsKey(id)) {
            return userCache.get(id);
        }

        try (Connection connection = DriverManager.getConnection(DB_Connection.url, DB_Connection.user, DB_Connection.pass);
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT * FROM " + DB_Connection.tab + " WHERE Id = ?")) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                users user = new users(
                        rs.getInt("Id"),
                        rs.getString("lastname"),
                        rs.getString("first name"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("Gender"),
                        rs.getInt("limit")
                );
                userCache.put(id, user);
                return user;
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving user: " + e.getMessage());
        }

        return null;
    }
    public boolean deleteUser(int id) {
        try (Connection connection = DriverManager.getConnection(DB_Connection.url, DB_Connection.user, DB_Connection.pass);
             PreparedStatement del = connection.prepareStatement("DELETE FROM " + DB_Connection.tab + " WHERE Id = ?")) {

//            // First check if user exists
//            if (!checkUser(id)) {
//                System.out.println("No user found with ID: " + id);
//                return false;
//            }

            del.setInt(1, id);
            int rowsAffected = del.executeUpdate();

            if (rowsAffected > 0) {
                // Remove from cache if successfully deleted from database
                userCache.remove(id);
                System.out.println("User deleted successfully");
                return true;
            } else {
                System.out.println("Failed to delete user with ID: " + id);
                return false;
            }

        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) { // Foreign key constraint violation
                System.out.println("Cannot delete user: User has associated records");
            } else {
                System.out.println("Error deleting user: " + e.getMessage());
            }
            return false;
        }
    }
}