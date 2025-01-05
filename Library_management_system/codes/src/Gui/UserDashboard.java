package Gui;

import DSA.Admin.MySQLbookDb;
import DSA.Objects.Books;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

public class UserDashboard extends JFrame {
    private JTextField searchField;
    private JComboBox<String> sortByComboBox;
    private JPanel mainPanel;
    private JTable bookTable;
    private DefaultTableModel tableModel;

    public UserDashboard() {
        setTitle("Library Management System");
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
        borrowButton.addActionListener(e -> borrowBook(new Books(), "User")); // Example usage of borrowBook
        returnButton.addActionListener(e -> returnBook());
        profileButton.addActionListener(e -> showProfile());
        historyButton.addActionListener(e -> showHistory());
        logoutButton.addActionListener(e -> logout());
        sortByComboBox.addActionListener(e -> sortBooks());
        loadBooks();
    }

    private void loadBooks() {
        // Clear previous data from table
        tableModel.setRowCount(0);

        // Fetch available books from the database
        List<Books> books = MySQLbookDb.LoadBooks();
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

    private void borrowBook(Books book, String borrowerName) {
        // Implement borrowing logic
        if (book.isAvailable()) {
            book.setAvailableCopy(book.getAvailableCopy() - 1);
            JOptionPane.showMessageDialog(this, "Book borrowed successfully!");
        } else {
            JOptionPane.showMessageDialog(this, "Sorry, this book is currently unavailable.");
        }
    }

    private void returnBook() {
        JOptionPane.showMessageDialog(this, "Return functionality to be implemented");
    }

    private void showProfile() {
        JOptionPane.showMessageDialog(this, "Profile view to be implemented");
    }

    private void showHistory() {
        JOptionPane.showMessageDialog(this, "History view to be implemented");
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
        SwingUtilities.invokeLater(() -> {
            UserDashboard userDashboard = new UserDashboard();
            userDashboard.setVisible(true);
        });
    }
}
