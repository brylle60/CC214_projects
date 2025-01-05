package DSA.UserControl;

import DSA.Objects.users;
import DSA.Admin.DB_Connection;
import java.sql.*;
import java.util.Hashtable;

public class UserHash {

    private static Hashtable<Integer, users> userCache;

    public UserHash() {
        this.userCache = new Hashtable<>();
    }

    // Add user to the hash table and database
    public static boolean addUser(users user) {
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

    // Retrieve user from the hash table or from the database if not cached
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
                        rs.getString("lastName"),
                        rs.getString("firstName"),
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

    // Validate user credentials (ID and password)
    public static boolean validateUser(int id, String password) {
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

    // Check if user exists in the database and load from cache
    public static boolean checkUserExists(int id) {
        // Check if the user is already in the cache
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
                        rs.getString("lastName"),
                        rs.getString("firstName"),
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

    // Retrieve all users from the database and load them into the cache
    public Hashtable<Integer, users> loadAllUsers() {
        try (Connection connection = DriverManager.getConnection(DB_Connection.url, DB_Connection.user, DB_Connection.pass);
             PreparedStatement stmt = connection.prepareStatement("SELECT * FROM " + DB_Connection.tab);
             ResultSet result = stmt.executeQuery()) {

            while (result.next()) {
                users user = new users(
                        result.getInt("Id"),
                        result.getString("lastName"),
                        result.getString("firstName"),
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

    // Delete user from the database and cache
    public boolean deleteUser(int id) {
        try (Connection connection = DriverManager.getConnection(DB_Connection.url, DB_Connection.user, DB_Connection.pass);
             PreparedStatement del = connection.prepareStatement("DELETE FROM " + DB_Connection.tab + " WHERE Id = ?")) {

            del.setInt(1, id);
            int rowsAffected = del.executeUpdate();

            if (rowsAffected > 0) {
                // Remove from cache if successfully deleted from the database
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

    // Update user details in the database and cache
    public boolean updateUser(users updatedUser) {
        try (Connection connection = DriverManager.getConnection(DB_Connection.url, DB_Connection.user, DB_Connection.pass);
             PreparedStatement update = connection.prepareStatement(
                     "UPDATE " + DB_Connection.tab +
                             " SET lastName = ?, firstName = ?, password = ?, email = ?, Gender = ?, Limit = ? " +
                             "WHERE Id = ?")) {

            // Ensure user exists before updating
            if (!checkUserExists(updatedUser.getId())) {
                System.out.println("No user found with ID: " + updatedUser.getId());
                return false;
            }

            update.setString(1, updatedUser.getLastName());
            update.setString(2, updatedUser.getFirstName());
            update.setString(3, updatedUser.getPass());
            update.setString(4, updatedUser.getEmail());
            update.setString(5, updatedUser.getGender());
            update.setInt(6, updatedUser.getLimit());
            update.setInt(7, updatedUser.getId());

            int rowsAffected = update.executeUpdate();

            if (rowsAffected > 0) {
                userCache.put(updatedUser.getId(), updatedUser);
                System.out.println("User updated successfully");
                return true;
            } else {
                System.out.println("Failed to update user with ID: " + updatedUser.getId());
                return false;
            }

        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) {
                System.out.println("Error updating user: Constraint violation");
            } else {
                System.out.println("Error updating user: " + e.getMessage());
            }
            return false;
        }
    }

    public static void main(String[] args) {
        // Testing the functionality of the UserHash class

        // Create a new UserHash object
        UserHash userHash = new UserHash();

        // Create a user object to add to the hash table and database
        users newUser = new users(1, "Doe", "John", "password123", "john.doe@example.com", "Male", 5);

        // Add user to the hash table and database
        if (addUser(newUser)) {
            System.out.println("User added successfully.");
        }

        // Retrieve the user by ID
        users retrievedUser = userHash.getUser(1);
        if (retrievedUser != null) {
            System.out.println("User retrieved: " + retrievedUser.getFirstName() + " " + retrievedUser.getLastName());
        }

        // Update user details
        newUser.setFirstName("John Updated");
        if (userHash.updateUser(newUser)) {
            System.out.println("User updated successfully.");
        }

        // Delete user
        if (userHash.deleteUser(1)) {
            System.out.println("User deleted successfully.");
        }
    }
}
