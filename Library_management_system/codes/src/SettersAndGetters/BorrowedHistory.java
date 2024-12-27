package SettersAndGetters;

import java.time.LocalDateTime;

public class BorrowedHistory {  private int userId;
    private String lastName;
    private String firstName;
    private String borrowedBook;
    private int borrowedCopy;
    private LocalDateTime borrowedDate;
    private LocalDateTime returnedDate;

    public BorrowedHistory(int userId, String lastName, String firstName,
                         String borrowedBook, int borrowedCopy,
                         String borrowedDate, String returnedDate) {
        this.userId = userId;
        this.lastName = lastName;
        this.firstName = firstName;
        this.borrowedBook = borrowedBook;
        this.borrowedCopy = borrowedCopy;
        this.borrowedDate = LocalDateTime.parse(borrowedDate);
        this.returnedDate = LocalDateTime.parse(returnedDate);
    }

    public String toCSV() {
        // Ensure each field goes to its proper column
        return String.format("%d,%s,%s,%s,%d,%s,%s",
                userId,
                lastName,
                firstName,
                borrowedBook,
                borrowedCopy,
                borrowedDate,
                returnedDate
        );
    }

    public static BorrowedHistory fromCSV(String line) {
        String[] parts = line.split(",");
        if (parts.length < 7) {
            throw new IllegalArgumentException("Invalid borrow history format");
        }

        return new BorrowedHistory(
                Integer.parseInt(parts[0].trim()),
                parts[1].trim(),
                parts[2].trim(),
                parts[3].trim(),
                Integer.parseInt(parts[4].trim()),
                parts[5].trim(),
                parts[6].trim()
        );
    }
}
