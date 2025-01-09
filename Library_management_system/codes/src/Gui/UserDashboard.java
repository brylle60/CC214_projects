package Gui;

import DSA.Admin.AdminControls;
import DSA.Admin.Borrowed_requests;
import DSA.Admin.BorrowingHistory;
import DSA.Admin.MySQLbookDb;
import DSA.Objects.Books;
import DSA.Objects.users;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import java.util.List;

import DSA.Admin.USER_DB;
import DSA.Objects.users;



public class UserDashboard extends JFrame {
    private JTextField searchField;
    private JComboBox<String> sortByComboBox;
    private JPanel mainPanel;

    private JTable bookTable;
    private DefaultTableModel tableModel;
    private final users currentUser;

    private int currentUserId;

    public void setCurrentUser(int userId) {
        this.currentUserId = userId;
    }


    public UserDashboard(users currentUser) {
        this.currentUser = currentUser;
        setTitle("Library Management System - Welcome " +
                (currentUser != null ? currentUser.getFirstName() + " " + currentUser.getLastName() : "Guest"));
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create main panel with BorderLayout
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create left panel for buttons
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        leftPanel.setPreferredSize(new Dimension(150, 0));

        // Profile section
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        JLabel profilePicture = new JLabel(new ImageIcon()); // Placeholder for profile picture
        profilePicture.setPreferredSize(new Dimension(100, 100));
        profilePicture.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JTextField nameField = new JTextField("Name");
        nameField.setMaximumSize(new Dimension(150, 25));
        if (currentUser != null) {
            nameField.setText(currentUser.getFirstName() + " " + currentUser.getLastName());
            nameField.setEditable(false);
        }

        profilePanel.add(profilePicture);
        profilePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        profilePanel.add(nameField);
        profilePanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Create buttons
        JButton borrowButton = new JButton("Borrow");
        JButton returnButton = new JButton("Return");
        JButton profileButton = new JButton("Profile");
        JButton historyButton = new JButton("History");
        JButton logoutButton = new JButton("Logout");

        // Add buttons to left panel
        leftPanel.add(profilePanel);
        leftPanel.add(borrowButton);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        leftPanel.add(returnButton);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        leftPanel.add(profileButton);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        leftPanel.add(historyButton);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        leftPanel.add(logoutButton);

        // Create top panel for search and sort
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        String[] sortOptions = {"Genre", "Alphabetical Order", "Date", "ISBN"};
        sortByComboBox = new JComboBox<>(sortOptions);
        sortByComboBox.setPreferredSize(new Dimension(150, 25));

        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(new JLabel("Sort by:"));
        topPanel.add(sortByComboBox);

        // Create center panel for book table
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Available Books"));

        // Create table with column names
        String[] columnNames = {"ISBN", "Title", "Author", "Genre", "Available Copies", "Total Copies"};
        tableModel = new DefaultTableModel(columnNames, 0);
        bookTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Add panels to main panel
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Add main panel to frame
        add(mainPanel);


        // Add action listeners
        searchButton.addActionListener(e -> searchBooks());
        borrowButton.addActionListener(e -> {
            if (currentUser != null) {
                borrowBook(null, currentUser);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please log in to borrow books.",
                        "Login Required",
                        JOptionPane.WARNING_MESSAGE);
            }
        }); // Example usage of borrowBook
        returnButton.addActionListener(e -> returnBook());
        profileButton.addActionListener(e -> showProfile());
        historyButton.addActionListener(e -> showHistory());
        logoutButton.addActionListener(e -> logout());
        sortByComboBox.addActionListener(e -> sortBooks());
        loadBooks();
    }

    private void loadBooks() {
        try {
            // Clear previous data from table
            tableModel.setRowCount(0);

            // Get AdminControls instance and fetch books
            List<Books> books = MySQLbookDb.LoadBooks();
            if (books != null) {
                for (Books book : books) {
                    if (book.isAvailable()) {  // Only show available books
                        tableModel.addRow(new Object[]{
                                book.getISBN(),
                                book.getTitle(),
                                book.getAuthor(),
                                book.getGenre(),
                                book.getAvailableCopy(),
                                book.getTotalCopy()
                        });
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading books: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading books. Please try again later.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    private void sortBooks() {
        String selectedSort = (String) sortByComboBox.getSelectedItem();
        List<Books> books = MySQLbookDb.LoadBooks();

        switch (selectedSort) {
            case "Genre":
                books.sort((b1, b2) -> b1.getGenre().compareTo(b2.getGenre()));
                break;
            case "Alphabetical Order":
                books.sort((b1, b2) -> b1.getTitle().compareTo(b2.getTitle()));
                break;
            case "Date":
                books.sort((b1, b2) -> b1.getDatePublished().compareTo(b2.getDatePublished()));
                break;
            case "ISBN":
                books.sort((b1, b2) -> Integer.compare(b1.getISBN(), b2.getISBN()));
                break;
        }

        // Update table after sorting
        tableModel.setRowCount(0); // Clear the existing rows
        for (Books book : books) {
            if (book.isAvailable()) {
                tableModel.addRow(new Object[]{
                        book.getISBN(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getGenre(),
                        book.getAvailableCopy(),
                        book.getTotalCopy()
                });
            }
        }
    }

    private void searchBooks() {
        String searchTerm = searchField.getText().toLowerCase();
        List<Books> allBooks = MySQLbookDb.LoadBooks();

        tableModel.setRowCount(0); // Clear previous search results
        for (Books book : allBooks) {
            if (book.getTitle().toLowerCase().contains(searchTerm) ||
                    book.getAuthor().toLowerCase().contains(searchTerm) ||
                    book.getGenre().toLowerCase().contains(searchTerm) ||
                    String.valueOf(book.getISBN()).contains(searchTerm)) {

                tableModel.addRow(new Object[]{
                        book.getISBN(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getGenre(),
                        book.getAvailableCopy(),
                        book.getTotalCopy()
                });
            }
        }
    }

    // In UserDashboard.java, update the borrowBook method:
    private void borrowBook(Books book, users user) {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to borrow.");
            return;
        }

        int isbn = (int) tableModel.getValueAt(selectedRow, 0);
        String title = (String) tableModel.getValueAt(selectedRow, 1);
        String author = (String) tableModel.getValueAt(selectedRow, 2);
        int availableCopies = (int) tableModel.getValueAt(selectedRow, 4);

        // Verify copies are available
        if (availableCopies <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Sorry, this book is currently unavailable.",
                    "No Copies Available",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            boolean success = AdminControls.borrowBook(
                    user.getId(),
                    title,
                    user.getLastName(),
                    author,
                    1  // Borrowing one copy at a time
            );

            if (success) {
                // Immediately refresh the book list to show updated counts
                loadBooks();

                JOptionPane.showMessageDialog(this,
                        "Book borrowed successfully!\nPlease return within 14 days to avoid late fees.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to borrow book. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "An error occurred: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    private void returnBook() {
        // First, load user's borrowed books
        List<Borrowed_requests.BorrowRequest> borrowedBooks = BorrowingHistory.LoadHistoryByUser(currentUser.getLastName())
                .stream()
                .filter(h -> h.getStatus().equals("BORROWED"))
                .toList();

        if (borrowedBooks.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "You don't have any books to return.",
                    "No Books to Return",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Create a list of book titles for the dropdown
        String[] bookTitles = borrowedBooks.stream()
                .map(Borrowed_requests.BorrowRequest::getTitle)
                .toArray(String[]::new);

        // Show book selection dialog
        String selectedTitle = (String) JOptionPane.showInputDialog(
                this,
                "Select a book to return:",
                "Return Book",
                JOptionPane.QUESTION_MESSAGE,
                null,
                bookTitles,
                bookTitles[0]
        );

        if (selectedTitle != null) {
            try {
                // Find the full book details from the borrowed list
                Borrowed_requests.BorrowRequest borrowedBook = borrowedBooks.stream()
                        .filter(b -> b.getTitle().equals(selectedTitle))
                        .findFirst()
                        .orElse(null);

                if (borrowedBook != null) {
                    boolean success = AdminControls.returnBook(
                            currentUser.getId(),
                            selectedTitle,
                            currentUser.getLastName(),
                            borrowedBook.getAuthor()
                    );

                    if (success) {
                        // Refresh the book list
                        loadBooks();

                        JOptionPane.showMessageDialog(this,
                                "Book returned successfully!",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Failed to return book. Please try again.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "An error occurred: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void showProfile() {


        JDialog profileDialog = new JDialog(this, "User Profile", true);
        profileDialog.setSize(400, 300);
        profileDialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel detailsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Create fields for user details
        JTextField firstNameField = new JTextField(currentUser.getFirstName());
        JTextField lastNameField = new JTextField(currentUser.getLastName());
        JTextField userIDField = new JTextField(currentUser.getId());
        JTextField emailField = new JTextField(currentUser.getEmail());

        // Get user data using your existing USER_DB class
        USER_DB userDb = new USER_DB();
        users currentUser = userDb.getUser(currentUserId);

//        if (currentUser != null) {
//            firstNameField.setText(currentUser.getFirstName());
//            lastNameField.setText(currentUser.getLastName());
//            userIDField.setText(String.valueOf(currentUser.getId()));
//            emailField.setText(currentUser.getEmail());
//        }

        // Make fields non-editable
        firstNameField.setEditable(false);
        lastNameField.setEditable(false);
        userIDField.setEditable(false);
        emailField.setEditable(false);

        // Add fields with labels
        addLabelAndField(detailsPanel, "First Name:", firstNameField, gbc);
        addLabelAndField(detailsPanel, "Last Name:", lastNameField, gbc);
        addLabelAndField(detailsPanel, "User ID:", userIDField, gbc);
        addLabelAndField(detailsPanel, "Email:", emailField, gbc);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton editButton = new JButton("Edit Profile");
        JButton closeButton = new JButton("Close");

        buttonPanel.add(editButton);
        buttonPanel.add(closeButton);

        // Edit button action
        editButton.addActionListener(e -> {
            JTextField editFirstNameField = new JTextField(firstNameField.getText());
            JTextField editLastNameField = new JTextField(lastNameField.getText());
            JTextField editEmailField = new JTextField(emailField.getText());

            JPanel inputPanel = new JPanel(new GridLayout(6, 2, 5, 5));
            inputPanel.add(new JLabel("First Name:"));
            inputPanel.add(editFirstNameField);
            inputPanel.add(new JLabel("Last Name:"));
            inputPanel.add(editLastNameField);
            inputPanel.add(new JLabel("Email:"));
            inputPanel.add(editEmailField);

            int result = JOptionPane.showConfirmDialog(profileDialog,
                    inputPanel,
                    "Edit Profile",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                // Update the user object
                currentUser.setFirstName(editFirstNameField.getText());
                currentUser.setLastName(editLastNameField.getText());
                currentUser.setEmail(editEmailField.getText());

                // Update in database
                if (USER_DB.add(currentUser)) { // Using your existing add method to update
                    firstNameField.setText(editFirstNameField.getText());
                    lastNameField.setText(editLastNameField.getText());
                    emailField.setText(editEmailField.getText());
                    JOptionPane.showMessageDialog(profileDialog,
                            "Profile updated successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(profileDialog,
                            "Error updating profile",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        closeButton.addActionListener(e -> profileDialog.dispose());

        mainPanel.add(detailsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        profileDialog.add(mainPanel);
        profileDialog.setVisible(true);
    }

    // Helper method remains the same
    private void addLabelAndField(JPanel panel, String labelText, JTextField field, GridBagConstraints gbc) {
        JLabel label = new JLabel(labelText);
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(label, gbc);

        field.setPreferredSize(new Dimension(200, 25));
        gbc.insets = new Insets(0, 5, 15, 5);
        panel.add(field, gbc);

    }

    private void showHistory() {
        JFrame historyFrame = new JFrame("Borrowing History");
        historyFrame.setSize(800, 400);
        historyFrame.setLocationRelativeTo(null);
        historyFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        historyFrame.setLayout(new BorderLayout());

        // Create table model
        DefaultTableModel historyTableModel = new DefaultTableModel();
        historyTableModel.setColumnIdentifiers(new String[]{"ISBN", "Book Title", "Author", "Genre", "Status", "Borrow Date"});

        JTable historyTable = new JTable(historyTableModel);
        JScrollPane scrollPane = new JScrollPane(historyTable);

        try {
            // Get user's borrowing history
            List<Borrowed_requests.BorrowRequest> userHistory = BorrowingHistory.LoadHistoryByUser(currentUser.getLastName());

            for (Borrowed_requests.BorrowRequest request : userHistory) {
                Books book = request.getBook();  // Now this will properly fetch the book
                if (book != null) {
                    historyTableModel.addRow(new Object[]{
                            book.getISBN(),
                            book.getTitle(),
                            book.getAuthor(),
                            book.getGenre(),
                            request.getStatus(),
                            request.getBorrowReqDate().toString()
                    });
                }
            }

            // Add status filter at the top
            JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JComboBox<String> statusFilter = new JComboBox<>(new String[]{"All", "BORROWED", "RETURNED"});
            filterPanel.add(new JLabel("Filter by Status: "));
            filterPanel.add(statusFilter);

            statusFilter.addActionListener(e -> {
                String selectedStatus = (String) statusFilter.getSelectedItem();
                filterHistoryTable(historyTableModel, userHistory, selectedStatus);
            });

            historyFrame.add(filterPanel, BorderLayout.NORTH);
            historyFrame.add(scrollPane, BorderLayout.CENTER);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading borrowing history: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        historyFrame.setVisible(true);
    }

    private void filterHistoryTable(DefaultTableModel model, List<Borrowed_requests.BorrowRequest> history, String status) {
        model.setRowCount(0); // Clear current table

        for (Borrowed_requests.BorrowRequest request : history) {
            if (status.equals("All") || request.getStatus().equals(status)) {
                Books book = request.getBook();
                if (book != null) {
                    model.addRow(new Object[]{
                            book.getISBN(),
                            book.getTitle(),
                            book.getAuthor(),
                            book.getGenre(),
                            request.getStatus(),
                            request.getBorrowReqDate().toString()
                    });
                }
            }
        }
    }





    private void logout() {
        int response = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        users loggedInUser = null;
        SwingUtilities.invokeLater(() -> {
            UserDashboard userDashboard = new UserDashboard(loggedInUser);
            userDashboard.setVisible(true);
        });
    }
}