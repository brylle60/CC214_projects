package DSA.Objects;

import DSA.Admin.BorrowingHistory;

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

    public int getUserId() {
        return userId;
    }

    public int getBorrowedCopy() {
        return borrowedCopy;
    }

    public String getBorrowedBook() {
        return borrowedBook;
    }

    public LocalDateTime getBorrowedDate() {
        return borrowedDate;
    }

    public String getBooktitle() {
        return booktitle;
    }

    public LocalDateTime getReturnedDate() {
        return returnedDate;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAuthors() {
        return authors;
    }

    public String getStatus() {
        return status;
    }

    public void setBooktitle(String booktitle) {
        this.booktitle = booktitle;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public void setBorrowedCopy(int borrowedCopy) {
        this.borrowedCopy = borrowedCopy;
    }

    public void setBorrowedBook(String borrowedBook) {
        this.borrowedBook = borrowedBook;
    }

    public void setBorrowedDate(LocalDateTime borrowedDate) {
        this.borrowedDate = borrowedDate;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setReturnedDate(LocalDateTime returnedDate) {
        this.returnedDate = returnedDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

}
