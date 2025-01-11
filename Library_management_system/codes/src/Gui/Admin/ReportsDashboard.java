package Gui.Admin;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

import DSA.Admin.*;
import DSA.Admin.Borrowed_requests.BorrowRequest;

import java.time.LocalDateTime;

public class ReportsDashboard extends JPanel {
    private JTextField searchField;
    private JComboBox<String> sortByComboBox;
    private final JTable requestsTable;
    private final DefaultTableModel requestsModel;
    private final DefaultTableModel borrowHistoryModel;
    private final DefaultTableModel activityLogsModel;
    private final JTable borrowHistoryTable;
    private final JTable activityLogsTable;
    private final JComboBox<String> statusFilter;

    private static final Color DARK_GREEN = new Color(40, 54, 44);
    private static final Color BUTTON_COLOR = new Color(184, 207, 229);

    public ReportsDashboard() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Initialize table models
        String[] requestColumns = {"Request ID", "User", "Book Title", "Author", "Copies", "Request Date", "Status"};
        requestsModel = new DefaultTableModel(requestColumns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        String[] historyColumns = {"ID", "User Name", "Book Title", "Author", "Copies", "Status"};
        borrowHistoryModel = new DefaultTableModel(historyColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        String[] logColumns = {"Time", "User", "Action", "Details"};
        activityLogsModel = new DefaultTableModel(logColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Initialize tables
        requestsTable = new JTable(requestsModel);
        borrowHistoryTable = new JTable(borrowHistoryModel);
        activityLogsTable = new JTable(activityLogsModel);

        // Initialize status filter
        statusFilter = new JComboBox<>(new String[]{"All", "PENDING", "BORROWED", "RETURNED", "REJECTED"});
        statusFilter.addActionListener(e -> filterBorrowingHistory());

        // Configure tables
        configureTable(requestsTable);
        configureTable(borrowHistoryTable);
        configureTable(activityLogsTable);

        // Create main layout
        createLayout();

        // Add action listeners
        setupActionListeners();

        // Initial data load
        refreshData();
    }

    private void createLayout() {
        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create and add top panel
        mainPanel.add(createTopPanel(), BorderLayout.NORTH);

        // Create and add center panel with tables
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Pending Requests", createRequestsPanel());
        tabbedPane.addTab("Borrowing History", createHistoryPanel());
        tabbedPane.addTab("Activity Logs", createLogsPanel());
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Add main panel to frame
        add(mainPanel);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        String[] sortOptions = {"Genre", "Alphabetical Order", "Date", "Status"};
        sortByComboBox = new JComboBox<>(sortOptions);
        sortByComboBox.setPreferredSize(new Dimension(150, 25));

        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(new JLabel("Sort by:"));
        topPanel.add(sortByComboBox);
        topPanel.add(statusFilter);

        return topPanel;
    }

    private JPanel createRequestsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(requestsTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(createStyledButton("Confirm Request"));
        buttonPanel.add(createStyledButton("Reject Request"));
        buttonPanel.add(createStyledButton("Refresh"));
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(borrowHistoryTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createLogsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(activityLogsTable), BorderLayout.CENTER);
        return panel;
    }

    private void setupActionListeners() {
        // Add button action listeners
        findButton("Confirm Request").addActionListener(e -> handleRequestAction(true));
        findButton("Reject Request").addActionListener(e -> handleRequestAction(false));
        findButton("Refresh").addActionListener(e -> refreshData());

        // Add table selection listener
        requestsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtonStates();
            }
        });
    }

    private JButton findButton(String text) {
        // Get the main panel
        JPanel mainPanel = (JPanel) getComponent(0);

        // Get the tabbed pane
        JTabbedPane tabbedPane = (JTabbedPane) mainPanel.getComponent(1);

        // Get the requests panel (first tab)
        JPanel requestsPanel = (JPanel) tabbedPane.getComponent(0);

        // Get the button panel (at SOUTH position)
        JPanel buttonPanel = (JPanel) ((BorderLayout) requestsPanel.getLayout())
                .getLayoutComponent(BorderLayout.SOUTH);

        // Find the button with matching text
        for (Component comp : buttonPanel.getComponents()) {
            if (comp instanceof JButton && ((JButton) comp).getText().equals(text)) {
                return (JButton) comp;
            }
        }

        throw new RuntimeException("Button not found: " + text);
    }

    private void updateButtonStates() {
        boolean rowSelected = requestsTable.getSelectedRow() != -1;
        findButton("Confirm Request").setEnabled(rowSelected);
        findButton("Reject Request").setEnabled(rowSelected);
    }
    private void configureTable(JTable table) {
        table.setRowHeight(30);
        table.setFont(new Font("Serif", Font.PLAIN, 14));

        // Configure header
        JTableHeader header = table.getTableHeader();
        header.setBackground(DARK_GREEN);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Serif", Font.BOLD, 14));

        // Configure selection
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(232, 242, 254));
        table.setSelectionForeground(Color.BLACK);

        // Configure column widths based on table type
        TableColumnModel columnModel = table.getColumnModel();
        int columnCount = columnModel.getColumnCount();

        if (table == requestsTable) {
            // Request table has 7 columns
            int[] widths = {80, 100, 200, 150, 80, 150, 100};
            for (int i = 0; i < Math.min(columnCount, widths.length); i++) {
                columnModel.getColumn(i).setPreferredWidth(widths[i]);
            }
        } else if (table == borrowHistoryTable) {
            // Borrowing history table has 6 columns
            int[] widths = {80, 100, 200, 150, 80, 100};
            for (int i = 0; i < Math.min(columnCount, widths.length); i++) {
                columnModel.getColumn(i).setPreferredWidth(widths[i]);
            }

        } else if (table == activityLogsTable) {
            // Activity logs table has 4 columns
            int[] widths = {150, 100, 100, 250};
            for (int i = 0; i < Math.min(columnCount, widths.length); i++) {
                columnModel.getColumn(i).setPreferredWidth(widths[i]);
            }

        }

        // Center align columns based on table type
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        if (table == requestsTable) {
            // Center align ID, Copies, and Status columns for requests table
            table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // ID
            table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); // Copies
            table.getColumnModel().getColumn(6).setCellRenderer(centerRenderer); // Status
        } else if (table == borrowHistoryTable) {
            // Center align ID, Copies, and Status columns for history table
            table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // ID
            table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); // Copies
            table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer); // Status
        } else if (table == activityLogsTable) {
            // Center align Time and Action columns for logs table
            table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Time
            table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); // Action
        }
    }

    private void handleRequestAction(boolean isConfirm) {
        int selectedRow = requestsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a request to process",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int modelRow = requestsTable.convertRowIndexToModel(selectedRow);
            int requestId = (int) requestsModel.getValueAt(modelRow, 0);
            String userName = (String) requestsModel.getValueAt(modelRow, 1);
            String bookTitle = (String) requestsModel.getValueAt(modelRow, 2);
            String author = (String) requestsModel.getValueAt(modelRow, 3);
            int requestedCopies = (int) requestsModel.getValueAt(modelRow, 4);

            String newStatus = isConfirm ? "BORROWED" : "REJECTED";
            boolean success = MySQLBorrowRequestDb.updateRequestStatus(
                    requestId,
                    newStatus,
                    requestId
            );

            if (success) {

                success = BorrowingHistory.BorrowedHistory(
                        requestId,
                        userName,
                        bookTitle,
                        author,
                        requestedCopies,
                        isConfirm ? "BORROWED" : "REJECTED"
                );

                if (isConfirm) {
                    success = MySQLbookDb.updateBookCopies(bookTitle, requestedCopies, false);
                }

                if (success) {
                    String message = isConfirm ? "Request confirmed successfully" : "Request rejected successfully";
                    JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshRequests();
                }

            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error processing request: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleReturnBook() {
        int viewRow = borrowHistoryTable.getSelectedRow();

        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a book to return",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = borrowHistoryTable.convertRowIndexToModel(viewRow);
        String status = (String) borrowHistoryTable.getModel().getValueAt(modelRow, 5);

        if (!"BORROWED".equals(status)) {
            JOptionPane.showMessageDialog(this,
                    "Can only return books with 'BORROWED' status",
                    "Invalid Status",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int bookId = (int) borrowHistoryTable.getModel().getValueAt(modelRow, 0);
            String userName = (String) borrowHistoryTable.getModel().getValueAt(modelRow, 1);
            String bookTitle = (String) borrowHistoryTable.getModel().getValueAt(modelRow, 2);
            String author = (String) borrowHistoryTable.getModel().getValueAt(modelRow, 3);
            int copies = (int) borrowHistoryTable.getModel().getValueAt(modelRow, 4);

            if (BorrowingHistory.BorrowedHistory(bookId, userName, bookTitle, author, copies, "RETURNED")) {
                deleteReturnedBook(bookId, bookTitle, userName);

                JOptionPane.showMessageDialog(this,
                        "Book returned successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

//                addActivityLog(
//                        LocalDateTime.now().toString(),
//                        userName,
//                        "Return Book",
//                        "Returned book: " + bookTitle
//                );

                refreshData();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to return book",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error processing return: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteReturnedBook(int bookId, String bookTitle, String userName) {
        // Implementation to delete the returned book from the history table
        try {
            Connection conn = DriverManager.getConnection(DB_Connection.BorrowedHistory, DB_Connection.user, DB_Connection.pass);
            String deleteSql = "DELETE FROM " + DB_Connection.HistoryTable +
                    " WHERE Id = ? AND BookName = ? AND UserName = ? AND Status = 'RETURNED'";

            PreparedStatement pstmt = conn.prepareStatement(deleteSql);
            pstmt.setInt(1, bookId);
            pstmt.setString(2, bookTitle);
            pstmt.setString(3, userName);

            pstmt.executeUpdate();

            conn.close();
        } catch (SQLException e) {
            System.err.println("Error deleting returned book: " + e.getMessage());
        }
    }
    // Update the refresh method to properly populate the table

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(BUTTON_COLOR);
        button.setForeground(DARK_GREEN);
        button.setFocusPainted(false);
        button.setFont(new Font("Serif", Font.PLAIN, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DARK_GREEN),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return button;
    }


    private void refreshRequests() {
        requestsModel.setRowCount(0);
        try (Connection connection = DriverManager.getConnection(
                DB_Connection.BorrowedHistory, DB_Connection.user, DB_Connection.pass)) {

            String query = "SELECT r.user_id as request_id, u.lastName, b.Title, b.Author, " +
                    "r.copies, r.request_date, r.status " +
                    "FROM borrow_requests r " +
                    "JOIN books.addedBooks b ON r.book_id = b.Id " +
                    "JOIN user.users u ON r.user_id = u.id " +
                    "WHERE r.status = 'PENDING' " +
                    "ORDER BY r.request_date ASC";

            try (PreparedStatement stmt = connection.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

                boolean hasRequests = false;
                while (rs.next()) {
                    hasRequests = true;
                    Object[] row = new Object[]{
                            rs.getInt("request_id"),
                            rs.getString("lastName"),
                            rs.getString("Title"),
                            rs.getString("Author"),
                            rs.getInt("copies"),
                            formatDateTime(rs.getTimestamp("request_date")),
                            rs.getString("status")
                    };
                    requestsModel.addRow(row);
                }

                if (!hasRequests) {
                    requestsModel.addRow(new Object[]{
                            -1, "--", "No pending requests", "--", 0, "--", "PENDING"
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Error refreshing requests: " + e.getMessage());
            requestsModel.addRow(new Object[]{
                    -1, "--", "Error loading requests", "--", 0, "--", "ERROR"
            });
        }
    }

    // Helper method to format datetime
    private String formatDateTime(Timestamp timestamp) {
        if (timestamp == null) return "--";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return timestamp.toLocalDateTime().format(formatter);
    }
    private void refreshData() {
        refreshBorrowingHistory();
        refreshRequests();
    }

    // In ReportsDashboard.java
    private void refreshBorrowingHistory() {
        borrowHistoryModel.setRowCount(0);

        try (Connection connection = DriverManager.getConnection(DB_Connection.BorrowedHistory, DB_Connection.user, DB_Connection.pass)) {
            String query = "SELECT h.Id, u.lastName, h.BookName, h.Author, h.Copies, h.Status " +
                    "FROM " + DB_Connection.HistoryTable + " h " +
                    "JOIN user.users u ON h.UserName = u.lastName " +
                    "ORDER BY h.Id DESC";

            try (PreparedStatement stmt = connection.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

                boolean hasHistory = false;
                while (rs.next()) {
                    hasHistory = true;
                    Object[] row = new Object[]{
                            rs.getInt("Id"),          // ID
                            rs.getString("lastName"), // User Name
                            rs.getString("BookName"), // Book Title
                            rs.getString("Author"),   // Author
                            rs.getInt("Copies"),      // Copies
                            rs.getString("Status")    // Status
                    };
                    borrowHistoryModel.addRow(row);
                }

                if (!hasHistory) {
                    // Add placeholder row when no history exists
                    borrowHistoryModel.addRow(new Object[]{
                            -1, "--", "No borrowing history", "--", 0, "--"
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Error refreshing borrowing history: " + e.getMessage());
            e.printStackTrace();
            // Add error indication row
            borrowHistoryModel.addRow(new Object[]{
                    -1, "--", "Error loading history", "--", 0, "ERROR"
            });
        }

        borrowHistoryTable.repaint();
    }

    //
//    private void refreshRequests() {
//        requestsModel.setRowCount(0);
//        System.out.println("Cleared request table rows");
//
//        // Initialize requests from database
//        Borrowed_requests.initializeFromDatabase();
//        System.out.println("Initialized requests from database");
//
//        // Get pending requests
//        List<Borrowed_requests.BorrowRequest> requests = Borrowed_requests.getPendingRequests();
//        System.out.println("Retrieved " + requests.size() + " pending requests");
//
//        if (requests.isEmpty()) {
//            System.out.println("No pending requests found");
//            // Maybe add a "No requests" message to the table
//            requestsModel.addRow(new Object[]{"No pending requests", "", "", "", "", "", ""});
//            return;
//        }
//
//        for (BorrowRequest request : requests) {
//            try {
//                Object[] row = new Object[]{
//                        request.getId(),
//                        request.getUser(),
//                        request.getTitle(),
//                        request.getAuthor(),
//                        request.getCopies(),
//                        request.getBorrowReqDate(),
//                        request.getStatus()
//                };
//                requestsModel.addRow(row);
//                System.out.println("Added request: " + request.getTitle() + " by " + request.getUser());
//            } catch (Exception e) {
//                System.err.println("Error adding row for request: " + e.getMessage());
//                e.printStackTrace();
//            }
//        }
//        System.out.println("Final table row count: " + requestsModel.getRowCount());
//    }



    private void filterBorrowingHistory() {
        String selectedStatus = (String) statusFilter.getSelectedItem();
        if (selectedStatus.equals("All")) {
            refreshBorrowingHistory();
            return;
        }

        borrowHistoryModel.setRowCount(0);
        List<BorrowRequest> filteredHistory = BorrowingHistory.LoadHistoryByStatus(selectedStatus);

        for (BorrowRequest request : filteredHistory) {
            Object[] row = new Object[]{
                    request.getId(),
                    request.getUser(),
                    request.getTitle(),
                    request.getAuthor(),
                    request.getCopies(),
                    request.getStatus()
            };
            borrowHistoryModel.addRow(row);
        }
    }
    private void navigateBack() {
        // Get the parent window
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof JFrame) {
            window.dispose();
            // Create and show admin dashboard
            JFrame adminFrame = new JFrame("Admin Dashboard");
            adminFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            AdminDashboard adminDashboard = new AdminDashboard();

            adminDashboard.setVisible(true);
        }
    }

//    public void addActivityLog(String time, String user, String action, String details) {
//        Object[] row = new Object[]{time, user, action, details};
//        activityLogsModel.addRow(row);
//    }
    public static void main(String[] args) {
        // Run GUI code on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            // Create a new JFrame
            JFrame frame = new JFrame("Reports Dashboard");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Create and add the ReportsDashboard panel
            ReportsDashboard dashboard = new ReportsDashboard();
            frame.getContentPane().add(dashboard);

            // Set frame size and make it visible
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null); // Center on screen
            frame.setVisible(true);
        });
    }

}