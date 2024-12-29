package SettersAndGetters;

import DB.BorrowingHistory;

import java.time.LocalDateTime;

public class BorrowedHistory {  private int userId;
    private String lastName;
    private String booktitle;
    private String borrowedBook;
    private int borrowedCopy;
    private LocalDateTime borrowedDate;
    private LocalDateTime returnedDate;
    private String authors;
    private String status;

    public BorrowedHistory(int userId, String lastName, String booktitle, String authors, int borrowedCopy,String Satus) {
        this.userId = userId;
        this.lastName = lastName;
        this.booktitle = booktitle;
        //this.borrowedBook = borrowedBook;
        this.authors = authors;
        this.borrowedCopy = borrowedCopy;
//        this.borrowedDate = LocalDateTime.parse(borrowedDate);
//        this.returnedDate = LocalDateTime.parse(returnedDate);
        this.status = Satus;
    }

//    public String toCSV() {
//        // Ensure each field goes to its proper column
//        return String.format("%d,%s,%s,%s,%d,%s,%s",
//                userId,
//                lastName,
//                firstName,
//                borrowedBook,
//                borrowedCopy,
//                borrowedDate,
//                returnedDate
//        );
//    }

//    public static BorrowedHistory fromCSV(String line) {
//        String[] parts = line.split(",");
//        if (parts.length < 7) {
//            throw new IllegalArgumentException("Invalid borrow history format");
//        }
//
//        return new BorrowedHistory(
//                Integer.parseInt(parts[0].trim()),
//                parts[1].trim(),
//                parts[2].trim(),
//                parts[3].trim(),
//                Integer.parseInt(parts[4].trim()),
//                parts[5].trim(),
//                parts[6].trim()
//        );
//    }
    public void borrowbooks(){
        BorrowingHistory.BorrowedHistory(this.userId, this.lastName, this.booktitle, this.authors, this.borrowedCopy, this.status);
    }

    public boolean toCSV() {
        return false;
    }
}
