package DSA.Admin;

import DSA.Objects.Books;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Borrowed_requests {
    private static Queue<BorrowRequest> pendingRequests = new LinkedList<>();

    public static class BorrowRequest {
        private final Books book;
        private final String user;
        private final int copies;
        private String status;
        //private final LocalDateTime borrowReqDate;
        private int id;

        public BorrowRequest(Books book, String user, int copies, String status) {
            this.book = book;
            this.user = user;
            this.copies = copies;
            this.status = status;
            //this.borrowReqDate = borrowReqDate;
            this.id = -1; // Default value, will be set when retrieved from database
        }

        // Getters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Books getBook() {
            return book;
        }

        public String getUser() {
            return user;
        }

        public String getTitle() {
            return book.getTitle();
        }

        public String getAuthor() {
            return book.getAuthor();
        }

        public int getCopies() {
            return copies;
        }

        public String getStatus() {
            return status;
        }
//        public LocalDateTime getBorrowReqDate() { return borrowReqDate; }
//    }



        // Helper method to get user ID





    }
}