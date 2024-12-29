package DSA.Admin;
import CSVFiles.Books_DB;
import DSA.Objects.Books;
import DSA.Objects.BorrowedHistory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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
        List<Books> loadedBooks = Books_DB.loadBooks(Books.LIBRARY_FILE);
        if (loadedBooks != null) {
            this.books.addAll(loadedBooks);
            sortBooks();
        }
    }

    public void addBook(Books book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }

        book.setBorrowed(false);
        book.setBorrower("");
        books.add(book);
        sortBooks();
        saveLibrary();
        book.addBooks(); // Persist to database
    }

    public boolean borrowBook(String title, int userId, String lastName, String author, int copies) {
        Optional<Books> bookOpt = books.stream()
                .filter(b -> b.getTitle().equals(title) && b.isAvailable() && b.getAvailableCopy() >= copies)
                .findFirst();

        if (bookOpt.isPresent()) {
            Books book = bookOpt.get();
            LocalDateTime currentTime = LocalDateTime.now();
            book.setBorrowDate(currentTime);
            book.setAvailableCopy(book.getAvailableCopy() - copies);

            // Create borrow record
            BorrowedHistory borrowedHistory = new BorrowedHistory(
                    userId, lastName, title, author, copies, "borrowed"
            );
            borrowedHistory.borrowbooks(); // Persist borrow history

            saveLibrary();
            return true;
        }
        return false;
    }

    public boolean returnBook(String title, int userId, int copies) {
        Optional<Books> bookOpt = books.stream()
                .filter(b -> b.getTitle().equals(title))
                .findFirst();

        if (bookOpt.isPresent()) {
            Books book = bookOpt.get();
            LocalDateTime returnTime = LocalDateTime.now();
            book.setAvailableCopy(book.getAvailableCopy() + copies);

            // Update return date in history
            Books_DB.updateBorrowHistoryReturnDate(title, userId, returnTime.toString());

            saveLibrary();
            return true;
        }
        return false;
    }

    public List<Books> getAvailableBooks() {
        List<Books> availableBooks = books.stream()
                .filter(Books::isAvailable)
                .sorted(Comparator.comparingInt(Books::getISBN))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        return Collections.unmodifiableList(availableBooks);
    }

    public List<Books> getBorrowedBooks() {
        List<Books> borrowedBooks = books.stream()
                .filter(b -> !b.isAvailable())
                .sorted(Comparator.comparingInt(Books::getISBN))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        return Collections.unmodifiableList(borrowedBooks);
    }

    public List<Books> searchBooks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Search keyword cannot be empty");
        }

        String searchTerm = keyword.toLowerCase();
        List<Books> results = books.stream()
                .filter(book ->
                        book.getTitle().toLowerCase().contains(searchTerm) ||
                                book.getAuthor().toLowerCase().contains(searchTerm))
                .sorted(Comparator.comparingInt(Books::getISBN))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        return Collections.unmodifiableList(results);
    }

    private void sortBooks() {
        Collections.sort(books, Comparator
                .comparingInt(Books::getISBN)
                .thenComparing(Books::getTitle, String.CASE_INSENSITIVE_ORDER));
    }

    private void saveLibrary() {
        Books_DB.saveBooks(books, Books.LIBRARY_FILE);
    }

    // Search methods delegated to Search utility class
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
}