package DB;

import SettersAndGetters.Books;
import SettersAndGetters.BorrowedHistory;

import java.io.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static SettersAndGetters.Books.*;
//import static com.mysql.cj.util.TimeUtil.DATE_FORMATTER;

public class Books_DB { // need to change this into db but before that we'll rest fuck this shit ass bugs

    public static List<Books> loadBooks(String filename) {
        List<Books> books = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line = reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                try {
                    Books book = Books.fromCSV(line);
                    if (book != null) {
                        books.add(book);
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing line: " + line + ". " + e.getMessage());
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Library file not found. Creating a new one.");
        } catch (IOException e) {
            System.err.println("Error loading books: " + e.getMessage());
        }
        return books;
    }

    public static void saveBooks(List<Books> books, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Write header
            writer.println("bookID,title,author,date published,available copy,total copy");

            // Write book data
            for (Books book : books) {
                if (book != null) {
                    writer.println(book.toCSV());
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving books: " + e.getMessage());
        }
    }

//    public static void logBorrowHistory(int userId, String lastName, String firstName, String bookTitle,
//                                        int borrowedCopy, String borrowedDate, String returnedDate) {
//        try (PrintWriter writer = new PrintWriter(new FileWriter(Books.BORROW_HISTORY_FILE, true))) {
//            // If file is empty, write header
//            File file = new File(Books.BORROW_HISTORY_FILE);
//            if (file.length() == 0) {
//                writer.println("userid,last name, first name,borrowed book,borrowed copy,borrowed date,returned date");
//            }
//
//            // Create and write borrow history entry
//            BorrowedHistory history = new BorrowedHistory(userId, lastName, firstName, bookTitle,
//                    borrowedCopy, borrowedDate, returnedDate);
//            writer.println(history.toCSV());
//        } catch (IOException e) {
//            System.err.println("Error logging borrow history: " + e.getMessage());
//        }
//    }

    public static void createBorrowHistoryFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(Books.BORROW_HISTORY_FILE))) {
            writer.println("userid,last name, first name,borrowed book,borrowed copy,borrowed date,returned date");
        } catch (IOException e) {
            System.err.println("Error creating borrow history file: " + e.getMessage());
        }
    }

    public static void updateBorrowHistoryReturnDate(String title, int userId, String returnDate) {
        String tempFile = BORROW_HISTORY_FILE + ".tmp";

        try {
            // Create a temporary file to write the updated history
            BufferedReader reader = new BufferedReader(new FileReader(BORROW_HISTORY_FILE));
            PrintWriter writer = new PrintWriter(new FileWriter(tempFile));

            String line;
            boolean found = false;

            // Copy header if exists
            if ((line = reader.readLine()) != null) {
                writer.println(line);
            }

            // Process each line
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                // Check if this is the relevant borrow record (matching title and user)
                if (!found && parts.length >= 6 &&
                        parts[1].equals(escapeCSV(title)) &&
                        parts[5].equals(String.valueOf(userId)) &&
                        !line.contains("RETURNED")) {
                    // Add return date to the existing borrow record
                    writer.println(line + "," + returnDate);
                    found = true;
                } else {
                    writer.println(line);
                }
            }

            reader.close();
            writer.close();

            // Replace original file with updated file
            Files.move(Paths.get(tempFile), Paths.get(BORROW_HISTORY_FILE),
                    StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            System.err.println("Error updating borrow history: " + e.getMessage());
        }
    }
}



