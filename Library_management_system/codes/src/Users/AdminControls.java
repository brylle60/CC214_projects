package Users;

import DB.Books_DB;
import SettersAndGetters.Books;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static SettersAndGetters.Books.LIBRARY_FILE;

public class AdminControls {
    private List<Books> books;
    private List<Books> allBooks;
    private static AdminControls currentInstance;

    public AdminControls() {
        this.books = new ArrayList<>();
        this.allBooks = new ArrayList<>();
        currentInstance = this;

        // Load existing books
    //    List<Books> loadedBooks = Books_DB.loadBooks(LIBRARY_FILE);
//        if (loadedBooks != null) {
//            this.books.addAll(loadedBooks);
//         //   this.books.add(Books.addBooks());
//            this.allBooks.addAll(loadedBooks);
            sortBooks(); // Sort initially loaded books
        }


//    public static void updateBookStatus(Books updatedBook) {
//        if (currentInstance != null) {
//            for (Books book : currentInstance.books) {
//                if (book.getTitle().equals(updatedBook.getTitle())) {
//                    book.setBorrowed(updatedBook.isBorrowed());
//                    book.setBorrower(updatedBook.getBorrower());
//                    break;
//                }
//            }
//            currentInstance.saveLibrary();
//        }
//    }

    public void addBook(Books book) {
        if (book != null) {
            // Set default values for new books
            book.setBorrowed(false);
            book.setBorrower("");
           // books.add(book);
           // allBooks.add(book);
            sortBooks(); // Sort after adding new book
            saveLibrary();
            book.addBooks();  // Call instance method instead of static
        }
    }

    private void sortBooks() {
        Collections.sort(books, new Comparator<Books>() {
            @Override
            public int compare(Books book1, Books book2) {
                // Primary sort by bookID
                int idCompare = Integer.compare(book1.getISBN(), book2.getISBN());
                if (idCompare != 0) {
                    return idCompare;
                }
                // Secondary sort by title
                return book1.getTitle().compareToIgnoreCase(book2.getTitle());
            }
        });
    }

    public boolean borrowBook(String title, int userId, String lastName, String firstName, int copies) {
        for (Books book : books) {
            if (book.getTitle().equals(title) && book.isAvailable() && book.getAvailableCopy() >= copies) {
                LocalDateTime currentTime = LocalDateTime.now();
                book.setBorrowDate(currentTime);
                int availableCopy = book.getAvailableCopy() - copies;
                book.setAvailableCopy(availableCopy);

                Books_DB.logBorrowHistory(
                        userId,
                        lastName,
                        firstName,
                        title,
                        copies,
                        currentTime.toString(),
                        currentTime.toString()
                );

                saveLibrary();
                return true;
            }
        }
        return false;
    }

    public boolean returnBook(String title, int userId, int copies) {
        for (Books book : books) {
            if (book.getTitle().equals(title)) {
                LocalDateTime returnTime = LocalDateTime.now();
                int availableCopy = book.getAvailableCopy() + copies;
                book.setAvailableCopy(availableCopy);

                // Update borrow history with return date
                Books_DB.updateBorrowHistoryReturnDate(title, userId, returnTime.toString());

                saveLibrary();
                return true;
            }
        }
        return false;
    }

    public List<Books> getAvailableBooks() {
        List<Books> availableBooks = new ArrayList<>();
        for (Books book : books) {
            if (book.isAvailable()) {
                availableBooks.add(book);
            }
        }
        Collections.sort(availableBooks, (b1, b2) ->
                Integer.compare(b1.getISBN(), b2.getISBN()));
        return availableBooks;
    }

    public List<Books> getBorrowedBooks() {
        List<Books> borrowedBooks = new ArrayList<>();
        for (Books book : books) {
            if (!book.isAvailable()) {
                borrowedBooks.add(book);
            }
        }
        Collections.sort(borrowedBooks, (b1, b2) ->
                Integer.compare(b1.getISBN(), b2.getISBN()));
        return borrowedBooks;
    }

    private void saveLibrary() {
        Books_DB.saveBooks(books, LIBRARY_FILE);
    }

    // Additional utility methods
//    public Books findBookByTitle(String title) {
//        return books.stream()
//                .filter(book -> book.getTitle().equalsIgnoreCase(title))
//                .findFirst()
//                .orElse(null);
//    }

    public List<Books> searchBooks(String keyword) {
        List<Books> results = new ArrayList<>();
        String searchTerm = keyword.toLowerCase();

        for (Books book : books) {
            if (book.getTitle().toLowerCase().contains(searchTerm) ||
                    book.getAuthor().toLowerCase().contains(searchTerm)) {
                results.add(book);
            }
        }
        Collections.sort(results, (b1, b2) ->
                Integer.compare(b1.getISBN(), b2.getISBN()));
        return results;
    }
    // In your AdminControls class or wherever you need to search
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