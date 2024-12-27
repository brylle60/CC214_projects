package SettersAndGetters;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;


public class Books {

    public static final String LIBRARY_FILE = "library_books.csv";
    public static final String BORROW_HISTORY_FILE = "borrow_history.csv";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private int ISBN;
    private String title;
    private String author;
    private int datePublished;
    private int availableCopy;
    private int totalCopy;
    private  String currentBorrower;
    private LocalDateTime borrowDate;
    private boolean borrowed;
    private String borrower;
    private boolean isAvailable;

    public Books(int ISBN, String title, String author, int datePublished, int availableCopy, int totalCopy) {
        this.ISBN = ISBN;
        this.title = title;
        this.author = author;
        this.datePublished = datePublished;
        this.availableCopy = availableCopy;
        this.totalCopy = totalCopy;
        this.currentBorrower = "";
        this.borrowDate = LocalDateTime.now();
        this.borrowed = false;
        this.borrower = "";
    }

    // Getters and setters
    public int getISBN() {
        return ISBN;
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getDatePublished() { return datePublished; }
    public int getAvailableCopy() { return availableCopy; }
    public int getTotalCopy() { return totalCopy; }

    public void setAvailableCopy(int availableCopy) {

            this.availableCopy = availableCopy;

    }



    public String getCurrentBorrower() {
        return currentBorrower;
    }

    public LocalDateTime getBorrowDate() {
        return borrowDate;
    }

    public boolean isBorrowed() {
        return borrowed;
    }

    public void setBorrowed(boolean borrowed) {
        this.borrowed = borrowed;
    }

    public String getBorrower() {
        return borrower;
    }
    public void setBorrower(String borrower) {
        this.borrower = (borrower != null) ? borrower : "";
    }



    public boolean isAvailable() {
        return availableCopy > 0;
    }

    public String toCSV() {
        return String.join(",",
                String.valueOf(ISBN),
                escapeCSV(title),
                escapeCSV(author),
                String.valueOf(datePublished),
                String.valueOf(availableCopy),
                String.valueOf(totalCopy)
        );
    }


    public static Books fromCSV(String csvLine) {
        if (csvLine == null || csvLine.trim().isEmpty()) {
            return null;
        }

        String[] parts = csvLine.split(",");
        if (parts.length < 6) {
            throw new IllegalArgumentException("Invalid CSV format");
        }

        return new Books(
                Integer.parseInt(parts[0].trim()),
                unescapeCSV(parts[1]),
                unescapeCSV(parts[2]),
                Integer.parseInt(parts[3].trim()),
                Integer.parseInt(parts[4].trim()),
                Integer.parseInt(parts[5].trim())
        );
    }

    public static String escapeCSV(String value) {
        if (value == null) return "";
        return value.replace(",", "\\,")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    public static String unescapeCSV(String value) {
        if (value == null) return "";
        return value.replace("\\,", ",")
                .replace("\\n", "\n")
                .replace("\\r", "\r");
    }

    public void setBorrowDate(LocalDateTime borrowDate) {
        this.borrowDate = borrowDate;
    }
}


