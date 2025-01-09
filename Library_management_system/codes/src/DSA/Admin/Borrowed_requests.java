package DSA.Admin;

import DSA.Objects.Books;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Borrowed_requests {
    private static Queue<BorrowRequest> pendingRequests = new LinkedList<>();
    private static List<BorrowRequest> confirmedRequests = new ArrayList<>();
    private static List<BorrowRequest> rejectedRequests = new ArrayList<>();

    public static class BorrowRequest {
        private int id;
        private String title;
        private String user;
        private String author;
        private String status;
        private int copies;
        private LocalDateTime requestDate;
        private LocalDateTime processedDate;

        public BorrowRequest(int id, String user, String title, String author,
                             int copies, String status, LocalDateTime requestDate) {
            this.id = id;
            this.title = title;
            this.user = user;
            this.author = author;
            this.status = status;
            this.copies = copies;
            this.requestDate = requestDate;
        }

        // Getters and setters
        public int getId() { return id; }
        public String getTitle() { return title; }
        public String getUser() { return user; }
        public String getAuthor() { return author; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public int getCopies() { return copies; }
        public LocalDateTime getRequestDate() { return requestDate; }
        public LocalDateTime getProcessedDate() { return processedDate; }
        public void setProcessedDate(LocalDateTime date) { this.processedDate = date; }
    }

    // Initialize/refresh queue from database
    public static void refreshQueue() {
        pendingRequests.clear();
        Queue<BorrowRequest> newRequests = MySQLBorrowRequestDb.loadPendingRequestsIntoQueue();
        pendingRequests.addAll(newRequests);
    }

    // Add new borrow request
    public static boolean addRequest(Books book, String userId) {
        if (book == null || userId == null || userId.trim().isEmpty()) {
            return false;
        }

        if (!book.isAvailable()) {
            return false;
        }

        // Create database entry
        boolean added = MySQLBorrowRequestDb.addRequest(
                Integer.parseInt(userId),
                book.getISBN(),
                1  // Default to 1 copy
        );

        if (added) {
            refreshQueue();
            return true;
        }
        return false;
    }

    // Process the next request in queue
    public static BorrowRequest processNextRequest() {
        return pendingRequests.poll();
    }

    // Confirm a borrow request
    public static boolean confirmRequest(int requestId) {
        BorrowRequest request = findRequestById(requestId);
        if (request == null) {
            return false;
        }

        // Update database status
        if (MySQLBorrowRequestDb.updateRequestStatus(requestId, "CONFIRMED")) {
            // Remove from pending queue
            pendingRequests.remove(request);

            // Update request details
            request.setStatus("CONFIRMED");
            request.setProcessedDate(LocalDateTime.now());
            confirmedRequests.add(request);

            // Process the actual book borrowing
            Books book = AdminControls.getInstance().findBookByTitle(request.getTitle());
            if (book != null) {
                boolean borrowed = AdminControls.borrowBook(
                        Integer.parseInt(request.getUser()),
                        request.getTitle(),
                        request.getUser(),
                        request.getAuthor(),
                        request.getCopies()
                );

                if (borrowed) {
                    // Add to borrowing history
                    BorrowingHistory.BorrowedHistory(
                            Integer.parseInt(request.getUser()),
                            request.getUser(),
                            request.getTitle(),
                            request.getAuthor(),
                            request.getCopies(),
                            "BORROWED"
                    );
                    return true;
                }
            }
        }
        return false;
    }

    // Reject a borrow request
    public static boolean rejectRequest(int requestId) {
        BorrowRequest request = findRequestById(requestId);
        if (request == null) {
            return false;
        }

        // Update database status
        if (MySQLBorrowRequestDb.updateRequestStatus(requestId, "REJECTED")) {
            // Remove from pending queue
            pendingRequests.remove(request);

            // Update request details
            request.setStatus("REJECTED");
            request.setProcessedDate(LocalDateTime.now());
            rejectedRequests.add(request);

            return true;
        }
        return false;
    }

    // Helper method to find request by ID
    private static BorrowRequest findRequestById(int requestId) {
        for (BorrowRequest request : pendingRequests) {
            if (request.getId() == requestId) {
                return request;
            }
        }
        return null;
    }

    // Get all pending requests
    public static List<BorrowRequest> getPendingRequests() {
        return new ArrayList<>(pendingRequests);
    }

    // Get confirmed requests
    public static List<BorrowRequest> getConfirmedRequests() {
        return new ArrayList<>(confirmedRequests);
    }

    // Get rejected requests
    public static List<BorrowRequest> getRejectedRequests() {
        return new ArrayList<>(rejectedRequests);
    }

    // Get number of pending requests
    public static int getPendingRequestsCount() {
        return pendingRequests.size();
    }

    // Check if there are any pending requests
    public static boolean hasPendingRequests() {
        return !pendingRequests.isEmpty();
    }
}