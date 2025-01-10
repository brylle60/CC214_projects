package Gui.Admin;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import DSA.Admin.BorrowingHistory;
import DSA.Admin.Borrowed_requests;
import DSA.Admin.Borrowed_requests.BorrowRequest;
import java.time.LocalDateTime;

public class ReportsDashboard extends JPanel {
    private final JTable borrowHistoryTable;
    private final DefaultTableModel borrowHistoryModel;
    private final JTable activityLogsTable;
    private final DefaultTableModel activityLogsModel;
    private final JTable requestsTable;
    private final DefaultTableModel requestsModel;
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
        statusFilter = new JComboBox<>(new String[]{"All", "Borrowed", "Returned"});
        JButton refreshButton = createStyledButton("↻ Refresh");

        filterPanel.add(filterLabel);
        filterPanel.add(statusFilter);
        filterPanel.add(refreshButton);

        // Create tables
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

        // Add tabbed pane to main panel
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

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton acceptButton = createStyledButton("Accept Request");
        JButton declineButton = createStyledButton("Decline Request");
        JButton refreshRequestsButton = createStyledButton("↻ Refresh");

        buttonPanel.add(acceptButton);
        buttonPanel.add(declineButton);
        buttonPanel.add(refreshRequestsButton);

        // Add action listeners
        acceptButton.addActionListener(e -> handleRequestAction(true));
        declineButton.addActionListener(e -> handleRequestAction(false));
        refreshRequestsButton.addActionListener(e -> refreshRequests());

        panel.add(new JScrollPane(requestsTable), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void configureTable(JTable table) {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        table.getTableHeader().setBackground(DARK_GREEN);
        table.getTableHeader().setForeground(Color.BLACK);
        table.setGridColor(Color.LIGHT_GRAY);
        table.setFont(new Font("Serif", Font.PLAIN, 14));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

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

    private void handleRequestAction(boolean isAccept) {
        int selectedRow = requestsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a request first",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int requestId = (int) requestsTable.getValueAt(selectedRow, 0);
        String bookTitle = (String) requestsTable.getValueAt(selectedRow, 2);
        String user = (String) requestsTable.getValueAt(selectedRow, 1);

        boolean success;
        if (isAccept) {
            success = Borrowed_requests.confirmRequest( bookTitle, user,requestId);
            if (success) {
                addActivityLog(
                        LocalDateTime.now().toString(),
                        "Admin",
                        "Accept Request",
                        "Accepted book request: " + bookTitle + " for user: " + user
                );
            }
        } else {
            success = Borrowed_requests.rejectRequest(bookTitle, user);
            if (success) {
                addActivityLog(
                        LocalDateTime.now().toString(),
                        "Admin",
                        "Reject Request",
                        "Rejected book request: " + bookTitle + " for user: " + user
                );
            }
        }

        if (success) {
            refreshRequests();
            JOptionPane.showMessageDialog(this,
                    "Request " + (isAccept ? "accepted" : "rejected") + " successfully",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to " + (isAccept ? "accept" : "reject") + " request",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshData() {
        refreshBorrowingHistory();
        refreshActivityLogs();
        refreshRequests();
    }

    private void refreshBorrowingHistory() {
        borrowHistoryModel.setRowCount(0);
        List<BorrowRequest> historyList = BorrowingHistory.LoadAllHistory();

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
    }

    private void refreshRequests() {
        requestsModel.setRowCount(0);

        // Initialize requests from database first
        Borrowed_requests.initializeFromDatabase();

        // Then get the requests
        List<Borrowed_requests.BorrowRequest> requests = Borrowed_requests.getPendingRequests();

        for (BorrowRequest request : requests) {
            Object[] row = new Object[]{
                    request.getId(),
                    request.getUser(),
                    request.getTitle(),
                    request.getAuthor(),
                    request.getCopies(),
                    request.getBorrowReqDate(),
                    request.getStatus()
            };
            requestsModel.addRow(row);
        }
    }

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