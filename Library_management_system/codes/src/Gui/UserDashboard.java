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
import java.awt.event.*;
import java.util.List;

public class UserDashboard extends JFrame {
    private static final Color BACKGROUND_COLOR = new Color(240, 240, 240);
    private static final Color BUTTON_COLOR = new Color(184, 207, 229);
    private static final Color DARK_GREEN = new Color(40, 54, 44);
    private static final Color GOLD = new Color(184, 157, 102);

    private JTextField searchField;
    private JComboBox<String> sortByComboBox;
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private final users currentUser;
    private JPanel contentPanel;

    public UserDashboard(users currentUser) {
        this.currentUser = currentUser;
        setTitle("Library Management System - User Dashboard");
        setSize(1170, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create main panel with custom background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(BACKGROUND_COLOR);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(null);

        // Add dashboard title
        JLabel titleLabel = new JLabel("User Dashboard");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        titleLabel.setForeground(DARK_GREEN);
        titleLabel.setBounds(20, 20, 300, 40);
        mainPanel.add(titleLabel);

        // Add user welcome label
        JLabel welcomeLabel = new JLabel("Welcome, " +
                (currentUser != null ? currentUser.getFirstName() + " " + currentUser.getLastName() : "Guest"));
        welcomeLabel.setFont(new Font("Serif", Font.ITALIC, 16));
        welcomeLabel.setForeground(DARK_GREEN);
        welcomeLabel.setBounds(20, 60, 300, 30);
        mainPanel.add(welcomeLabel);

        // Create buttons panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(5, 1, 0, 20));
        buttonsPanel.setBounds(20, 100, 250, 400);
        buttonsPanel.setOpaque(false);

        // Create styled buttons
        JButton searchButton = createStyledButton("Search Books");
        JButton borrowButton = createStyledButton("Borrow Book");
        JButton returnButton = createStyledButton("Return Book");
        JButton historyButton = createStyledButton("Borrowing History");
        JButton profileButton = createStyledButton("Profile");

        // Add buttons to panel
        buttonsPanel.add(searchButton);
        buttonsPanel.add(borrowButton);
        buttonsPanel.add(returnButton);
        buttonsPanel.add(historyButton);
        buttonsPanel.add(profileButton);

        // Add buttons panel to main panel
        mainPanel.add(buttonsPanel);

        // Create content panel (right side)
        contentPanel = new JPanel();
        contentPanel.setBounds(290, 100, 840, 400);
        contentPanel.setBorder(BorderFactory.createLineBorder(DARK_GREEN));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setLayout(new BorderLayout());
        mainPanel.add(contentPanel);

        // Create and add the default book table view
        createBookTableView();

        // Add logout button
        JButton logoutButton = createStyledButton("Logout");
        logoutButton.setBounds(1025, 20, 100, 40);
        logoutButton.addActionListener(e -> logout());
        mainPanel.add(logoutButton);

        // Add action listeners
        searchButton.addActionListener(e -> showSearchView());
        borrowButton.addActionListener(e -> handleBorrowBook());
        returnButton.addActionListener(e -> showReturnView());
        historyButton.addActionListener(e -> showHistoryView());
        profileButton.addActionListener(e -> showProfileView());

        add(mainPanel);
        loadBooks();
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2d.setColor(BUTTON_COLOR.darker());
                } else {
                    g2d.setColor(BUTTON_COLOR);
                }

                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                g2d.setColor(DARK_GREEN);
                FontMetrics metrics = g2d.getFontMetrics();
                int x = (getWidth() - metrics.stringWidth(getText())) / 2;
                int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
                g2d.drawString(getText(), x, y);
            }
        };

        button.setPreferredSize(new Dimension(200, 40));
        button.setFont(new Font("Serif", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setForeground(DARK_GREEN.brighter());
            }

            public void mouseExited(MouseEvent e) {
                button.setForeground(DARK_GREEN);
            }
        });

        return button;
    }

    private void createBookTableView() {
        JPanel bookPanel = new JPanel(new BorderLayout(10, 10));
        bookPanel.setBackground(Color.WHITE);

        // Search panel at top
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        JButton searchBtn = createStyledButton("Search");
        String[] sortOptions = {"Genre", "Alphabetical Order", "Date", "ISBN"};
        sortByComboBox = new JComboBox<>(sortOptions);

        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(new JLabel("Sort by:"));
        searchPanel.add(sortByComboBox);

        // Table
        String[] columnNames = {"ISBN", "Title", "Author", "Genre", "Available Copies", "Total Copies"};
        tableModel = new DefaultTableModel(columnNames, 0);
        bookTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookTable);

        bookPanel.add(searchPanel, BorderLayout.NORTH);
        bookPanel.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(bookPanel);

        // Add listeners
        searchBtn.addActionListener(e -> searchBooks());
        sortByComboBox.addActionListener(e -> sortBooks());
    }

    // Existing methods remain the same
    private void loadBooks() {
        // Your existing loadBooks implementation
    }

    private void searchBooks() {
        // Your existing searchBooks implementation
    }

    private void sortBooks() {
        // Your existing sortBooks implementation
    }

    private void handleBorrowBook() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this,
                    "Please log in to borrow books.",
                    "Login Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        borrowBook(null, currentUser);
    }

    private void borrowBook(Books book, users user) {
        // Your existing borrowBook implementation
    }

    private void showSearchView() {
        contentPanel.removeAll();
        createBookTableView();
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showReturnView() {
        // Implement return view
        contentPanel.removeAll();
        // Add return book functionality
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showHistoryView() {
        // Implement history view
        contentPanel.removeAll();
        // Add history functionality
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showProfileView() {
        // Implement profile view
        contentPanel.removeAll();
        // Add profile functionality
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void logout() {
        int response = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            dispose();
            new LibraryGUI().setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new UserDashboard(null).setVisible(true);
        });
    }
}