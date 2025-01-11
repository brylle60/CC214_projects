package DSA.Admin;

import DSA.Objects.Books;
import DSA.Objects.BorrowedHistory;
import java.util.*;
import java.util.List;
import java.util.Optional;
import java.sql.*;

public class AdminControls {
    private static List<Books> books = new ArrayList<>();  // Initialize the list here
    private static AdminControls instance;

    private AdminControls() {
        loadBooks();  // Load books in constructor
    }

    public static AdminControls getInstance() {
        if (instance == null) {
            instance = new AdminControls();
        }
        return instance;
    }

    private void loadBooks() {
        try {
            List<Books> loadedBooks = MySQLbookDb.LoadBooks();
            if (loadedBooks != null) {
                books.clear();  // Clear existing books before loading
                books.addAll(loadedBooks);
                sortBooks();
            }
        } catch (Exception e) {
            System.err.println("Error loading books: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void addBook(Books book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }

        // Validate book data
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Book title cannot be empty");
        }
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            throw new IllegalArgumentException("Book author cannot be empty");
        }
        if (book.getTotalCopy() < 0) {
            throw new IllegalArgumentException("Total copies cannot be negative");
        }

        // Initialize book state
        book.setBorrowed(false);
        book.setBorrower("");
        book.setAvailableCopy(book.getTotalCopy());

        // Add to local list and database
        books.add(book);
        sortBooks();
        book.addBooks(); // Persist to database
    }

    // In AdminControls.java, update the borrowBook method:
//    public static boolean borrowBook(int userId, String title, String lastName, String author, int copies) {
//        if (title == null || title.trim().isEmpty() || lastName == null || lastName.trim().isEmpty()) {
//            throw new IllegalArgumentException("Invalid input parameters");
//        }
//
//        try {
//            // First, get current book details from database
//            List<Books> allBooks = MySQLbookDb.LoadBooks();
//            Optional<Books> bookOpt = allBooks.stream()
//                    .filter(b -> b.getTitle().equals(title))
//                    .findFirst();
//
//            if (bookOpt.isPresent()) {
//                Books book = bookOpt.get();
//
//                // Verify enough copies are available
//                if (book.getAvailableCopy() < copies) {
//                    return false;
//                }
//
//                // Calculate new available copies
//                int newAvailableCopies = book.getAvailableCopy() - copies;
//
//                // Update database first
//                MySQLbookDb.updateBookCopies(book.getISBN(), newAvailableCopies);
//
//                // Record the borrow transaction in history with PENDING status
//                BorrowingHistory.BorrowedHistory(
//                        userId,
//                        lastName,
//                        title,
//                        author,
//                        copies,
//                        "PENDING"  // Set initial status as PENDING
//                );
//
//                return true;
//            }
//            return false;
//        } catch (Exception e) {
//            System.err.println("Error in borrowBook: " + e.getMessage());
//            e.printStackTrace();
//            return false;
//        }
//    }

//    public static void updateBookCopies(int isbn, int newAvailableCopies) {
//        try {
//            if (newAvailableCopies < 0) {
//                throw new IllegalArgumentException("Available copies cannot be negative");
//            }
//            MySQLbookDb.updateBookCopies(isbn, newAvailableCopies);
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to update book copies: " + e.getMessage());
//        }
//    }
public static boolean returnBook(int userId, String bookTitle, String userName, String author) {
    Connection connection = null;
    try {
        connection = DriverManager.getConnection(DB_Connection.BorrowedHistory, DB_Connection.user, DB_Connection.pass);
        connection.setAutoCommit(false);  // Start transaction

        // 1. Verify the book is currently borrowed
        if (!checkIfBookIsBorrowed(userId, bookTitle)) {
            System.out.println("This book has already been returned or was never borrowed.");
            return false;
        }

        // 2. Get the book details to update available copies
        Books book = MySQLbookDb.findBookByTitle(bookTitle);
        if (book == null) {
            throw new SQLException("Book not found: " + bookTitle);
        }

        // 3. Update the borrow history status
        String historyUpdateQuery = "UPDATE " + DB_Connection.HistoryTable +
                " SET Status = 'RETURNED' " +
                "WHERE Id = ? AND BookName = ? AND Status = 'BORROWED'";

        try (PreparedStatement stmt = connection.prepareStatement(historyUpdateQuery)) {
            stmt.setInt(1, userId);
            stmt.setString(2, bookTitle);
            stmt.executeUpdate();
        }

        // 4. Update available copies in the books table
        boolean copiesUpdated = MySQLbookDb.updateBookCopies(bookTitle, 1, true);
        if (!copiesUpdated) {
            throw new SQLException("Failed to update book copies");
        }

        connection.commit();
        System.out.println("Book returned successfully.");
        return true;

    } catch (SQLException e) {
        try {
            if (connection != null) {
                connection.rollback();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        System.err.println("Error returning book: " + e.getMessage());
        e.printStackTrace();
        return false;
    } finally {
        try {
            if (connection != null) {
                connection.setAutoCommit(true);
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

    public static boolean checkIfBookIsBorrowed(int userId, String bookTitle) {
        String query = "SELECT COUNT(*) FROM " + DB_Connection.HistoryTable +
                " WHERE Id = ? AND BookName = ? AND Status = ?";

        try (Connection connection = DriverManager.getConnection(
                DB_Connection.BorrowedHistory, DB_Connection.user, DB_Connection.pass);
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, userId);
            stmt.setString(2, bookTitle);
            stmt.setString(3, "BORROWED");

            System.out.println("Checking borrow status for User ID: " + userId + ", Book: " + bookTitle);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    System.out.println("Found " + count + " borrowed records");
                    return count > 0;
                }
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error checking borrow status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    private void sortBooks() {
        Collections.sort(books, Comparator
                .comparingInt(Books::getISBN)
                .thenComparing(Books::getTitle, String.CASE_INSENSITIVE_ORDER));
    }

    // Search methods
    public Books findBookByISBN(int isbn) {
        return Search.searchByISBN(this.books, isbn);
    }

    public Books findBookByTitle(String title) {
        return Search.searchByTitle(this.books, title);
    }

    public List<Books> findBooksByAuthor(String author) {
        return Search.searchByAuthor(this.books, author);
    }

    public List<Books> findAvailableBooksByPrefix(String prefix) {
        return Search.searchAvailableByTitlePrefix(this.books, prefix);
    }

    // Method to refresh book list from database
    public void refreshBooks() {
        loadBooks();
    }
}