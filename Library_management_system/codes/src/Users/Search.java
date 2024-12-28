package Users;

import SettersAndGetters.Books;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Search {
    public static Books searchByISBN(List<Books> books, int targetISBN) {
        // First sort the list by ISBN
        books.sort(Comparator.comparingInt(Books::getISBN));

        int left = 0;
        int right = books.size() - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            Books midBook = books.get(mid);

            if (midBook.getISBN() == targetISBN) {
                return midBook;
            }

            if (midBook.getISBN() < targetISBN) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return null;
    }

    // Binary search by title
    public static Books searchByTitle(List<Books> books, String targetTitle) {
        // First sort the list by title
        books.sort(Comparator.comparing(Books::getTitle));

        int left = 0;
        int right = books.size() - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            Books midBook = books.get(mid);

            int comparison = midBook.getTitle().compareToIgnoreCase(targetTitle);

            if (comparison == 0) {
                return midBook;
            }

            if (comparison < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return null;
    }

    // Search by author (returns multiple books since an author can have multiple books)
    public static List<Books> searchByAuthor(List<Books> books, String targetAuthor) {
        // First sort the list by author
        books.sort(Comparator.comparing(Books::getAuthor));

        List<Books> results = new ArrayList<>();
        int left = 0;
        int right = books.size() - 1;

        // Find one match using binary search
        while (left <= right) {
            int mid = left + (right - left) / 2;
            Books midBook = books.get(mid);

            int comparison = midBook.getAuthor().compareToIgnoreCase(targetAuthor);

            if (comparison == 0) {
                // Found one match, now search for more matches in both directions
                results.add(midBook);

                // Search left
                int leftIndex = mid - 1;
                while (leftIndex >= 0 &&
                        books.get(leftIndex).getAuthor().equalsIgnoreCase(targetAuthor)) {
                    results.add(books.get(leftIndex));
                    leftIndex--;
                }

                // Search right
                int rightIndex = mid + 1;
                while (rightIndex < books.size() &&
                        books.get(rightIndex).getAuthor().equalsIgnoreCase(targetAuthor)) {
                    results.add(books.get(rightIndex));
                    rightIndex++;
                }

                return results;
            }

            if (comparison < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return results;
    }

    // Search available books by title prefix
    public static List<Books> searchAvailableByTitlePrefix(List<Books> books, String prefix) {
        // First sort the list by title
        books.sort(Comparator.comparing(Books::getTitle));

        List<Books> results = new ArrayList<>();
        prefix = prefix.toLowerCase();

        // Find the first match using binary search
        int left = 0;
        int right = books.size() - 1;
        int firstMatch = -1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            Books midBook = books.get(mid);
            String midTitle = midBook.getTitle().toLowerCase();

            if (midTitle.startsWith(prefix)) {
                firstMatch = mid;
                right = mid - 1; // Keep searching left for the first occurrence
            } else if (midTitle.compareTo(prefix) < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        // If we found a match, collect all matches
        if (firstMatch != -1) {
            for (int i = firstMatch; i < books.size(); i++) {
                Books book = books.get(i);
                if (book.getTitle().toLowerCase().startsWith(prefix)) {
                    if (book.isAvailable()) {
                        results.add(book);
                    }
                } else {
                    break; // No more matches possible
                }
            }
        }

        return results;
    }
}
