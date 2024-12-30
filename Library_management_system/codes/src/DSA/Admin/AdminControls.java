package DSA.Admin;

import DSA.Objects.Books;
import DSA.Objects.BorrowedHistory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.sql.SQLException;

public class AdminControls {
    private final List<Books> books;
    private static AdminControls instance;

    private AdminControls() {
        this.books = new ArrayList<>();
        loadBooks();
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
                this.books.clear(); // Clear existing books before loading
                this.books.addAll(loadedBooks);
                sortBooks();
            }
        } catch (Exception e) {
            System.err.println("Error loading books: " + e.getMessage());
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

    public boolean borrowBook(String title, int userId, String lastName, String author, int copies) {
        Optional<Books> bookOpt = books.stream()
                .filter(b -> b.getTitle().equals(title) && b.isAvailable() && b.getAvailableCopy() >= copies)
                .findFirst();

        if (bookOpt.isPresent()) {
            Books book = bookOpt.get();

            // Update book state
            LocalDateTime currentTime = LocalDateTime.now();
            book.setBorrowDate(currentTime);
            book.setAvailableCopy(book.getAvailableCopy() - copies);

            // Update database
            try {
                // Create borrow record
                BorrowedHistory borrowedHistory = new BorrowedHistory(
                        userId, lastName, title, author, copies, "BORROWED"
                );
                borrowedHistory.borrowbooks();

                // Update book copies in database
                MySQLbookDb.updateBookCopies(book.getISBN(), book.getAvailableCopy());

                return true;
            } catch (Exception e) {
                // Rollback local changes if database update fails
                book.setAvailableCopy(book.getAvailableCopy() + copies);
                System.err.println("Error processing borrow: " + e.getMessage());
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
                DSA.UserControl.Return.ReturnResult result =
                        DSA.UserControl.Return.processReturn(title, userName);

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