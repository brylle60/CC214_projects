package Gui;

import DB.Books_DB;
import SettersAndGetters.Books;
import Users.AdminControls;
import Books.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class main {
    private static AdminControls librarySystem;
    //   private static final String CSV_FILE = "books.csv";

    public static void main(String[] args) {
        try {
            librarySystem = new AdminControls();  // Initialize User object first
            // Load or initialize library
            if (initializeLibrary()) {
                performLibraryOperations();
                handleBorrowRequests();
                displayLibraryStatus();
            }
        } catch (Exception e) {
            System.err.println("Fatal error: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for debugging
        }
    }

    private static boolean initializeLibrary() {
        try {
            // Create initial book list
            List<Books> initialBooks = new ArrayList<>();
            initialBooks.add(new Books(001, "Java Basics", "John Doe", LocalDateTime.now(), 13, 20));
            initialBooks.add(new Books(002, "Python Programming", "Jane Smith",LocalDateTime.now(), 11, 30));
            initialBooks.add(new Books(003, "Advanced Algorithms", "Jane Smith", LocalDateTime.now(), 12, 20));

            // Add books one by one
            for (Books book : initialBooks) {
                librarySystem.addBook(book);
                System.out.println("Added book: " + book.getTitle());
            }

            // Try to load existing books from CSV
            try {
                List<Books> csvBooks = Books_DB.loadBooks(Books.LIBRARY_FILE);
                if (csvBooks != null) {
                    for (Books book : csvBooks) {
                        if (book != null) {
                            librarySystem.addBook(book);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Note: No existing books loaded from CSV");
            }

            System.out.println("Library initialized successfully with " +
                    librarySystem.getAvailableBooks().size() + " books.");
            return true;

        } catch (Exception e) {
            System.err.println("Error initializing library: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static void performLibraryOperations() {
        try {
            String firstName = "Alice";
            String LastName ="w";
            String bookTitle = "Java Basics";


            // Try to borrow a book
            boolean borrowed = librarySystem.borrowBook(bookTitle, 123, firstName,LastName, 2);
            System.out.println("Book '" + bookTitle + "' borrowed by " + firstName + ": " + borrowed);

            // Try to return the book
            boolean returned = librarySystem.returnBook(bookTitle, 123, 2);
            System.out.println("Book '" + bookTitle + "' returned: " + returned);

            // Save current state to CSV
            List<Books> currentBooks = librarySystem.getAvailableBooks();
            if (currentBooks != null && !currentBooks.isEmpty()) {
                Books_DB.saveBooks(new ArrayList<>(currentBooks), Books.LIBRARY_FILE); // Create new ArrayList to avoid modification
            }

        } catch (Exception e) {
            System.err.println("Error in library operations: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void handleBorrowRequests() {
        try {
            Books requestedBook = new Books(005, "Data Structures", "Alan Smith", LocalDateTime.now(), 11, 28);
            String requestingUser = "John Doe";

            boolean requestAdded = Borrowed_requests.addBorrowRequest(requestedBook, requestingUser);
            System.out.println("Borrow request added: " + requestAdded);

            // Add the book to the library system first
            librarySystem.addBook(requestedBook);

            // Confirm the request
            boolean confirmed = Borrowed_requests.confirmRequest("Data Structures", requestingUser,3);
            System.out.println("\nRequest confirmation " + (confirmed ? "successful" : "failed"));

            System.out.println("Pending borrow requests: " + Borrowed_requests.getPendingRequestsCount());

        } catch (Exception e) {
            System.err.println("Error handling borrow requests: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private static void displayLibraryStatus() {
        try {
            System.out.println("\nCurrent Library Status:");

            // Display available books
            List<Books> availableBooks = librarySystem.getAvailableBooks();
            System.out.println("Available Books:");
            if (availableBooks != null && !availableBooks.isEmpty()) {
                for (Books book : availableBooks) {
                    if (book != null) {
                        System.out.println("- " + book.getTitle() + " by " + book.getAuthor());
                    }
                }
            } else {
                System.out.println("No available books.");
            }

            // Display borrowed books
            List<Books> borrowedBooks = librarySystem.getBorrowedBooks();
            System.out.println("\nBorrowed Books:");
            if (borrowedBooks != null && !borrowedBooks.isEmpty()) {
                for (Books book : borrowedBooks) {
                    if (book != null) {
                        System.out.println("- " + book.getTitle() + " by " + book.getAuthor());
                    }
                }
            } else {
                System.out.println("No borrowed books.");
            }

        } catch (Exception e) {
            System.err.println("Error displaying library status: " + e.getMessage());
            e.printStackTrace();
        }
    }
}