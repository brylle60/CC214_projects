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
import DSA.Admin.BorrowingHistory;
import DSA.Admin.Borrowed_requests;
import DSA.Admin.Borrowed_requests.BorrowRequest;
import DSA.Admin.DB_Connection;

import java.time.LocalDateTime;

public class ReportsDashboard extends JPanel {
    private final JTable borrowHistoryTable;
    private final DefaultTableModel borrowHistoryModel;
    private final JTable activityLogsTable;
    private final DefaultTableModel activityLogsModel;
    private JTable requestsTable;
    private DefaultTableModel requestsModel;
    private final JComboBox<String> statusFilter;
    private static final Color DARK_GREEN = new Color(40, 54, 44);
    private static final Color BUTTON_COLOR = new Color(184, 207, 229);

    public ReportsDashboard() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create tabbed pane for different views
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Serif", Font.PLAIN, 14));

        // Add Book Requests Panel
        JPanel bookRequestsPanel = createBookRequestsPanel();
        tabbedPane.addTab("Book Requests", bookRequestsPanel);

        // Create Borrowing History panel
        JPanel borrowingHistoryPanel = new JPanel(new BorderLayout(10, 10));
        borrowingHistoryPanel.setBackground(Color.WHITE);

        // Create filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);

        JLabel filterLabel = new JLabel("Filter by Status:");
        statusFilter = new JComboBox<>(new String[]{"All", "Borrowed", "Returned", "Rejected"});
        JButton refreshButton = createStyledButton("↻ Refresh");
        JButton returnButton = createStyledButton("Return Book");

        filterPanel.add(filterLabel);
        filterPanel.add(statusFilter);
        filterPanel.add(refreshButton);
        filterPanel.add(returnButton);

        // Add return button action listener
        returnButton.addActionListener(e -> handleReturnBook());

        // Configure tables
        String[] borrowColumns = {"ID", "User Name", "Book Title", "Author", "Copies", "Status"};
        borrowHistoryModel = new DefaultTableModel(borrowColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        borrowHistoryTable = new JTable(borrowHistoryModel);
        configureTable(borrowHistoryTable);

        String[] requestColumns = {"Request ID", "User", "Book Title", "Author", "Copies", "Request Date", "Status"};
        requestsModel = new DefaultTableModel(requestColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        requestsTable = new JTable(requestsModel);
        configureTable(requestsTable);

        String[] logColumns = {"Time", "User", "Action", "Details"};
        activityLogsModel = new DefaultTableModel(logColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        activityLogsTable = new JTable(activityLogsModel);
        configureTable(activityLogsTable);

        // Add components to panels
        borrowingHistoryPanel.add(filterPanel, BorderLayout.NORTH);
        borrowingHistoryPanel.add(new JScrollPane(borrowHistoryTable), BorderLayout.CENTER);

        JPanel activityLogsPanel = new JPanel(new BorderLayout());
        activityLogsPanel.setBackground(Color.WHITE);
        activityLogsPanel.add(new JScrollPane(activityLogsTable), BorderLayout.CENTER);

        // Add panels to tabbed pane
        tabbedPane.addTab("Borrowing History", borrowingHistoryPanel);
        tabbedPane.addTab("Activity Logs", activityLogsPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Add listeners
        statusFilter.addActionListener(e -> filterBorrowingHistory());
        refreshButton.addActionListener(e -> refreshData());

        // Initial data load
        refreshData();
    }

    private JPanel createBookRequestsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);

        // Create table model with columns
        String[] columns = {
                "Request ID", "User", "Book Title", "Author", "Copies", "Request Date", "Status"
        };
        requestsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Create and configure the table
        requestsTable = new JTable(requestsModel);
        configureTable(requestsTable);

        // Add table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(requestsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        // In your createBookRequestsPanel method
        // Create buttons
        JButton acceptButton = createStyledButton("Accept Request");
        JButton declineButton = createStyledButton("Decline Request");

        // Create a selection listener that maintains the selection
        ListSelectionListener selectionListener = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int viewRow = requestsTable.getSelectedRow();
                    if (viewRow >= 0) {
                        // Store the selected row
                        final int selectedRow = viewRow;
                        System.out.println("Selection stored: " + selectedRow);
                    }
                }
            }
        };

        requestsTable.getSelectionModel().addListSelectionListener(selectionListener);

        // Use action listeners that preserve selection
        acceptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Make sure table maintains focus
                requestsTable.requestFocusInWindow();
                handleRequestAction(true);
            }
        });

        declineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Make sure table maintains focus
                requestsTable.requestFocusInWindow();
                handleRequestAction(false);
            }
        });
        JButton refreshRequestsButton = createStyledButton("↻ Refresh");
        refreshRequestsButton.addActionListener(e -> refreshRequests());

        buttonPanel.add(acceptButton);
        buttonPanel.add(declineButton);
        buttonPanel.add(refreshRequestsButton);

        // Add components to main panel
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Initial load of requests
        refreshRequests();

        return panel;
    }

    private void configureTable(JTable table) {
        table.setRowHeight(30);


        // Configure table header
        JTableHeader header = table.getTableHeader();
        header.setBackground(DARK_GREEN);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Serif", Font.BOLD, 14));

        // Configure table appearance
        table.setFont(new Font("Serif", Font.PLAIN, 14));
        table.setGridColor(Color.LIGHT_GRAY);
        table.setShowGrid(true);
        table.setSelectionBackground(new Color(232, 242, 254));
        table.setSelectionForeground(Color.BLACK);

        // Configure column widths
        TableColumnModel columnModel = table.getColumnModel();
        int[] columnWidths = determineColumnWidths(table);

        for (int i = 0; i < Math.min(columnWidths.length, columnModel.getColumnCount()); i++) {
            columnModel.getColumn(i).setPreferredWidth(columnWidths[i]);
        }
        // Set up selection mode

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowSelectionAllowed(true);
        table.setCellSelectionEnabled(false);
        table.setColumnSelectionAllowed(false);

        // Make sure selection persists when table loses focus
        table.putClientProperty("terminateEditOnFocusLost", Boolean.FALSE);

        // Ensure the table can maintain selection
        table.setFocusable(true);

        // Add a focus listener to debug focus issues
        table.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                System.out.println("Table lost focus");
            }

            @Override
            public void focusGained(FocusEvent e) {
                System.out.println("Table gained focus");
            }
        });
        // Center align relevant columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        applyCenterAlignment(table, centerRenderer);
    }

    private int[] determineColumnWidths(JTable table) {
        if (table == borrowHistoryTable) {
            return new int[]{80, 100, 200, 150, 80, 100};
        } else if (table == requestsTable) {
            return new int[]{80, 100, 200, 150, 80, 150, 100};
        } else if (table == activityLogsTable) {
            return new int[]{150, 100, 100, 250};
        }
        return new int[]{};
    }

    private void applyCenterAlignment(JTable table, DefaultTableCellRenderer centerRenderer) {
        if (table == borrowHistoryTable || table == requestsTable) {
            table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
            table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
            table.getColumnModel().getColumn(table.getColumnCount() - 1).setCellRenderer(centerRenderer);
        } else if (table == activityLogsTable) {
            table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
            table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        }
    }


    private void handleRequestAction(boolean isAccept) {
        // Get the selected row index from the VIEW perspective
        int viewRow = requestsTable.getSelectedRow();

        // Debug output
        System.out.println("Selected row index: " + viewRow);
        System.out.println("Total rows in table: " + requestsTable.getRowCount());

        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a request first",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Convert the view row index to model row index (in case table is sorted)
       // Request ID column

        try {
          //  int modelRow = requestsTable.convertRowIndexToModel(viewRow);
            int modelRow = requestsTable.convertRowIndexToModel(viewRow);


            // Check if the selected row is a "No pending requests" placeholder
            String bookTitle = (String) requestsTable.getModel().getValueAt(modelRow, 2);
            if ("No pending requests".equals(bookTitle)) {
                JOptionPane.showMessageDialog(this,
                        "Please select a valid request",
                        "Invalid Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Get the actual request ID from the table
            Object requestIdObj = requestsTable.getModel().getValueAt(modelRow, 0);
            if (requestIdObj == null || requestIdObj.toString().equals("-1")) {
                JOptionPane.showMessageDialog(this,
                        "Invalid request ID",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            String user = (String) requestsTable.getModel().getValueAt(modelRow, 1);
            int requestId = Integer.parseInt(requestsModel.getValueAt(modelRow, 0).toString());


            boolean success;
            if (isAccept) {
                success = Borrowed_requests.confirmRequest(bookTitle, user, requestId);
            } else {
                success = Borrowed_requests.rejectRequest(bookTitle, user);
            }

            if (success) {
                refreshRequests();
                String action = isAccept ? "accepted" : "rejected";
                JOptionPane.showMessageDialog(this,
                        "Request " + action + " successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                addActivityLog(
                        LocalDateTime.now().toString(),
                        "Admin",
                        isAccept ? "Accept Request" : "Reject Request",
                        (isAccept ? "Accepted" : "Rejected") + " book request: " + bookTitle + " for user: " + user
                );
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to " + (isAccept ? "accept" : "reject") + " request. " +
                                "Please check if the book is available.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
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

                addActivityLog(
                        LocalDateTime.now().toString(),
                        userName,
                        "Return Book",
                        "Returned book: " + bookTitle
                );

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

        try (Connection connection = DriverManager.getConnection(DB_Connection.BorrowedHistory, DB_Connection.user, DB_Connection.pass)) {
            // Modified query to include proper column names and table joins
            String query = "SELECT r.user_id as request_id, u.lastName, b.Title, b.Author, " +
                    "r.copies, r.request_date, r.status " +
                    "FROM requestTable r " +
                    "JOIN Books.AddedBooks b ON r.book_id = b.Id " +
                    "JOIN user.users u ON r.user_id = u.id " +
                    "WHERE r.status = 'PENDING' " +
                    "ORDER BY r.request_date ASC";

            try (PreparedStatement stmt = connection.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

                boolean hasRequests = false;
                while (rs.next()) {
                    hasRequests = true;
                    Object[] row = new Object[]{
                            rs.getInt("request_id"),      // Using aliased column name
                            rs.getString("lastName"),     // User's last name
                            rs.getString("Title"),        // Book title
                            rs.getString("Author"),       // Author
                            rs.getInt("copies"),          // Number of copies
                            formatDateTime(rs.getTimestamp("request_date")), // Formatted date
                            rs.getString("status")        // Status
                    };
                    requestsModel.addRow(row);
                }

                if (!hasRequests) {
                    // Add placeholder row when no requests exist
                    requestsModel.addRow(new Object[]{
                            -1, "--", "No pending requests", "--", 0, "--", "PENDING"
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Error refreshing requests: " + e.getMessage());
            e.printStackTrace();
            // Add error indication row
            requestsModel.addRow(new Object[]{
                    -1, "--", "Error loading requests", "--", 0, "--", "ERROR"
            });
        }

        requestsTable.repaint();
    }

    // Helper method to format datetime
    private String formatDateTime(Timestamp timestamp) {
        if (timestamp == null) return "--";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return timestamp.toLocalDateTime().format(formatter);
    }
    private void refreshData() {
        refreshBorrowingHistory();
        refreshActivityLogs();
        refreshRequests();
    }

    // In ReportsDashboard.java
    private void refreshBorrowingHistory() {
        borrowHistoryModel.setRowCount(0);
        List<BorrowRequest> historyList = BorrowingHistory.LoadAllHistory();

        System.out.println("Loaded " + historyList.size() + " history records");

        for (BorrowRequest request : historyList) {
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
        System.out.println("Added " + borrowHistoryModel.getRowCount() + " rows to table");
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

    private void refreshActivityLogs() {
        activityLogsModel.setRowCount(0);
        Object[] row1 = new Object[]{"2025-01-01 10:00", "John Doe", "Login", "User logged in successfully"};
        Object[] row2 = new Object[]{"2025-01-01 10:15", "John Doe", "Borrow", "Borrowed 'The Great Gatsby'"};
        activityLogsModel.addRow(row1);
        activityLogsModel.addRow(row2);
    }

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

    public void addActivityLog(String time, String user, String action, String details) {
        Object[] row = new Object[]{time, user, action, details};
        activityLogsModel.addRow(row);
    }
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