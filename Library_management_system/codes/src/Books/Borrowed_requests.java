package Books;

import SettersAndGetters.Books;
import Admin.AdminControls;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Borrowed_requests {
    private static Queue<BorrowRequest> borrowRequests = new LinkedList<>();
    private static List<BorrowRequest> confirmedRequests = new ArrayList<>();


    // Class to represent a borrow request
    public static class BorrowRequest {
//        private Books book;
        private int Id;
        private String title;
        private String user;
        private String author;
        private String status;
        private int copies;

        public BorrowRequest(int id, String title, String user, String author, int copies, String Status) {
//            this.book = book;
            this.Id = id;
            this.title = title;
            this.user = user;
            this.author = author;
            this.status = Status;
            this.copies=copies;
        }

        // Getters
//        public Books getBook() { return book; }

        public int getId() {
            return Id;
        }

        public String getTitle() {
            return title;
        }

        public String getUser() { return user; }
        public String getAuthor() { return author;}
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public int getCopies() {
            return copies;
        }
    }
    public static boolean addBorrowRequest(Books book, String user) {
        if (book != null && user != null && !user.trim().isEmpty()) {
            if (book.isAvailable()) {  // Check availability before adding request
                String currentDate = LocalDate.now().toString();
                int copy = book.getAvailableCopy();
               // BorrowRequest request = new BorrowRequest(book, user, currentDate,copy);
             //   borrowRequests.offer(request);
                return true;
            }
        }
        return false;
    }

    public static boolean confirmRequest(String bookTitle, String user, int copy) {
        BorrowRequest request = findPendingRequest(bookTitle, user);
//        if (request != null && request.getBook().isAvailable()) {  // Verify availability
            request.setStatus("CONFIRMED");
            borrowRequests.remove(request);
            confirmedRequests.add(request);

//            Books book = request.ge;
//            book.setBorrowed(true);
//            book.setBorrower(user);
//            book.setAvailableCopy(request.copies-copy);


            //AdminControls.updateBookStatus(book);
            return true;
//        }
//        return false;
    }

    public static boolean rejectRequest(String bookTitle, String user) {
        BorrowRequest request = findPendingRequest(bookTitle, user);
        if (request != null) {
            request.setStatus("REJECTED");
            borrowRequests.remove(request);
//            request.getTitle();
            return true;
        }
        return false;
    }

    private static BorrowRequest findPendingRequest(String bookTitle, String user) {
        return borrowRequests.stream()
                .filter(req -> req.getTitle().equals(bookTitle)
                        && req.getUser().equals(user))
                .findFirst()
                .orElse(null);
    }

    public static List<BorrowRequest> getPendingRequests() {
        return new ArrayList<>(borrowRequests);
    }

    public static List<BorrowRequest> getConfirmedRequests() {
        return new ArrayList<>(confirmedRequests);
    }
    // Process the next request in queue
    public static BorrowRequest processNextRequest() {
        return borrowRequests.poll();
    }

    // Get number of pending requests
    public static int getPendingRequestsCount() {
        return borrowRequests.size();
    }

    // Check if there are any pending requests
    public static boolean hasPendingRequests() {
        return !borrowRequests.isEmpty();
    }

    // View next request without removing it
    public static BorrowRequest peekNextRequest() {
        return borrowRequests.peek();
    }

    // Clear all requests
    public static void clearAllRequests() {
        borrowRequests.clear();
    }

}
