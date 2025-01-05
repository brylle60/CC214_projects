package DSA.Admin;

import DSA.Objects.Books;
import DSA.Objects.BorrowedHistory;
import DSA.UserControl.Return;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.sql.SQLException;

public class AdminControls {
    private static List<Books> books;
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
            } else {
                books = new ArrayList<>();  // Initialize empty list if load fails
            }
        } catch (Exception e) {
            System.err.println("Error loading books: " + e.getMessage());
            e.printStackTrace();
            books = new ArrayList<>();  // Initialize empty list if exception occurs
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
    public static boolean borrowBook(int userId, String title, String lastName, String author, int copies) {
        if (title == null || title.trim().isEmpty() || lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid input parameters");
        }

        Optional<Books> bookOpt = books.stream()
                .filter(b -> b.getTitle().equals(title) && b.isAvailable())
                .findFirst();

        if (bookOpt.isPresent()) {
            Books book = bookOpt.get();

            try {
                // Calculate new available copies
                int newAvailableCopies = book.getAvailableCopy() - 1;  // Always borrow 1 copy

                // Update database first
                System.out.println("Updating book copies - ISBN: " + book.getISBN() + ", New copies: " + newAvailableCopies);
                MySQLbookDb.updateBookCopies(book.getISBN(), newAvailableCopies);

                // If database update successful, update book object
                book.setAvailableCopy(newAvailableCopies);

                // Create borrow record
                BorrowedHistory borrowedHistory = new BorrowedHistory(
                        userId,
                        lastName,
                        title,
                        author,
                        1,  // Always borrow 1 copy
                        "BORROWED"
                );

                // Record the borrow transaction
                borrowedHistory.borrowbooks();

                return true;
            } catch (Exception e) {
                System.err.println("Error in borrowBook: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public boolean returnBook(String title, int userId, String userName, int copies) {
        Optional<Books> bookOpt = books.stream()
                .filter(b -> b.getTitle().equals(title))
                .findFirst();

        if (bookOpt.isPresent()) {
            Books book = bookOpt.get();

            try {
                // Use the Return class we fixed earlier
                Return.ReturnResult result =
                        Return.processReturn(title, userName);

                if (result.isSuccess()) {
                    // Update local state
                    book.setAvailableCopy(book.getAvailableCopy() + copies);

                    // Additional handling for overdue books if needed
                    if (result.isOverdue()) {
                        // Could implement overdue fine calculation here
                        System.out.println("Note: Book was returned overdue");
                    }

                    return true;
                }
            } catch (Exception e) {
                System.err.println("Error processing return: " + e.getMessage());
            }
        }
        return false;
    }

    public List<Books> getAvailableBooks() {
        return Collections.unmodifiableList(
                books.stream()
                        .filter(Books::isAvailable)
                        .sorted(Comparator.comparingInt(Books::getISBN))
                        .toList()
        );
    }

    public List<Books> getBorrowedBooks() {
        return Collections.unmodifiableList(
                books.stream()
                        .filter(b -> !b.isAvailable())
                        .sorted(Comparator.comparingInt(Books::getISBN))
                        .toList()
        );
    }

    public List<Books> searchBooks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Search keyword cannot be empty");
        }

        String searchTerm = keyword.toLowerCase();
        return Collections.unmodifiableList(
                books.stream()
                        .filter(book ->
                                book.getTitle().toLowerCase().contains(searchTerm) ||
                                        book.getAuthor().toLowerCase().contains(searchTerm))
                        .sorted(Comparator.comparingInt(Books::getISBN))
                        .toList()
        );
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