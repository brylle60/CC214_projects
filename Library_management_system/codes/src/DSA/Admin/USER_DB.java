package DSA.Admin;

import DSA.Objects.users;
import java.sql.*;
import java.util.Hashtable;

public class USER_DB {
    private Hashtable<Integer, users> userCache;

    public USER_DB() {
        this.userCache = new Hashtable<>();
    }

    //todo not yet fixed
    public boolean add(users user) {
        try (Connection connection = DriverManager.getConnection(DB_Connection.url, DB_Connection.user, DB_Connection.pass);
             PreparedStatement register = connection.prepareStatement(
                     "INSERT INTO " + DB_Connection.tab +
                             "(Id, firstname,lastname, password, email, Borrowing_limit) VALUES(?, ?, ?, ?, ?)")) {

            register.setInt(1, user.getId());
            register.setString(2, user.getLastName());
            register.setString(3, user.getFirstName());
            register.setString(4, user.getPass());
            register.setString(5, user.getEmail());
            register.setString(6, user.getGender());
            register.setInt(7, user.getLimit());


            int rowsAffected = register.executeUpdate();

            if (rowsAffected > 0) {
                // Add to cache if database insert was successful
                userCache.put(user.getId(), user);
                return true;
            }
            return false;

        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) { // Duplicate entry
                System.out.println("User with this ID already exists");
            } else {
                System.out.println("Error adding user: " + e.getMessage());
            }
            return false;
        }
    }

    //todo needs GUI for registration
    public boolean validate(int id, String name, String password) {
        try (Connection connection = DriverManager.getConnection(DB_Connection.url, DB_Connection.user, DB_Connection.pass);
             PreparedStatement valid = connection.prepareStatement(
                     "SELECT * FROM " + DB_Connection.tab +
                             " WHERE Id = ? AND name = ? AND password = ?")) {

            valid.setInt(1, id);
            valid.setString(2, name);
            valid.setString(3, password);

            ResultSet rs = valid.executeQuery();
            return rs.next(); // Returns true if user exists with given credentials

        } catch (SQLException e) {
            System.out.println("Error validating user: " + e.getMessage());
            return false;
        }
    }

    //todo used to check for existing account
    public boolean checkUser(int id) {
        // First check cache
        if (userCache.containsKey(id)) {
            return true;
        }

        try (Connection connection = DriverManager.getConnection(DB_Connection.url, DB_Connection.user, DB_Connection.pass);
             PreparedStatement check = connection.prepareStatement(
                     "SELECT * FROM " + DB_Connection.tab + " WHERE Id = ?")) {

            check.setInt(1, id);
            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                users user = new users(
                        rs.getInt("Id"),
                        rs.getString("lastname"),
                        rs.getString("firstname"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("Gender"),
                        rs.getInt("limit")
                );
                userCache.put(id, user);
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.out.println("Error checking user: " + e.getMessage());
            return false;
        }
    }

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
}