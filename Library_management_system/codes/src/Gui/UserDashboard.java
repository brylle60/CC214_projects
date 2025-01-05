package Gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.TitledBorder;

public class UserDashboard extends JFrame {
    private static final Color BACKGROUND_COLOR = new Color(240, 240, 240);
    private static final Color BUTTON_COLOR = new Color(184, 207, 229);
    private static final Color DARK_GREEN = new Color(40, 54, 44);
    private static final Color GOLD = new Color(184, 157, 102);
    private JPanel contentPanel;
    private JTextField[] profileFields;

    // Sample data - Replace with database data later
    private String[] sampleBooks = {
            "The Great Gatsby",
            "To Kill a Mockingbird",
            "1984",
            "Pride and Prejudice",
            "The Hobbit"
    };

    public UserDashboard() {
        initializeFrame();
        JPanel mainPanel = createMainPanel();
        setupComponents(mainPanel);
        add(mainPanel);
    }

    private void initializeFrame() {
        setTitle("Library Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 800);
        setLocationRelativeTo(null);
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(BACKGROUND_COLOR);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(null);
        return mainPanel;
    }

    private void setupComponents(JPanel mainPanel) {
        addDashboardTitle(mainPanel);
        addButtonsPanel(mainPanel);
        addContentPanel(mainPanel);
        addLogoutButton(mainPanel);
    }

    private void addDashboardTitle(JPanel mainPanel) {
        JLabel titleLabel = new JLabel("User Dashboard");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 32));
        titleLabel.setForeground(DARK_GREEN);
        titleLabel.setBounds(40, 30, 400, 50);
        mainPanel.add(titleLabel);
    }

    private void addButtonsPanel(JPanel mainPanel) {
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(3, 1, 0, 30));
        buttonsPanel.setBounds(40, 120, 300, 600);
        buttonsPanel.setOpaque(false);

        JButton libraryButton = createStyledButton("Library");
        JButton profileButton = createStyledButton("My Profile");
        JButton historyButton = createStyledButton("Borrowing History");

        libraryButton.addActionListener(e -> showLibraryPanel());
        profileButton.addActionListener(e -> showProfilePanel());
        historyButton.addActionListener(e -> showHistoryPanel());

        buttonsPanel.add(libraryButton);
        buttonsPanel.add(profileButton);
        buttonsPanel.add(historyButton);

        mainPanel.add(buttonsPanel);
    }

    private void addContentPanel(JPanel mainPanel) {
        contentPanel = new JPanel();
        contentPanel.setBounds(380, 120, 980, 600);
        contentPanel.setBorder(BorderFactory.createLineBorder(DARK_GREEN));
        contentPanel.setBackground(Color.WHITE);
        mainPanel.add(contentPanel);

        // Show library panel by default
        showLibraryPanel();
    }

    private void addLogoutButton(JPanel mainPanel) {
        JButton logoutButton = createStyledButton("Logout");
        logoutButton.setBounds(1240, 30, 120, 50);
        logoutButton.addActionListener(e -> handleLogout());
        mainPanel.add(logoutButton);
    }

    private void showLibraryPanel() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout(0, 20));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create main library panel
        JPanel libraryPanel = new JPanel(new BorderLayout(0, 20));

        // Add search panel at top
        JPanel searchPanel = createSearchPanel();
        libraryPanel.add(searchPanel, BorderLayout.NORTH);

        // Add books panel in center
        JPanel booksPanel = createBooksPanel();
        libraryPanel.add(booksPanel, BorderLayout.CENTER);

        contentPanel.add(libraryPanel);
        refreshPanel();
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Search Books"));

        JTextField searchField = new JTextField(30);
        searchField.setPreferredSize(new Dimension(300, 35));

        JButton searchButton = createStyledButton("Search");
        searchButton.setPreferredSize(new Dimension(100, 35));

        searchButton.addActionListener(e -> {
            // TODO: Implement search functionality
            String searchText = searchField.getText();
            JOptionPane.showMessageDialog(this,
                    "Searching for: " + searchText,
                    "Search",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        return searchPanel;
    }

    private JPanel createBooksPanel() {
        JPanel booksPanel = new JPanel(new BorderLayout(0, 10));
        booksPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Available Books"));

        // Create book list with selection listener
        JList<String> bookList = new JList<>(sampleBooks);
        bookList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookList.setFont(new Font("Serif", Font.PLAIN, 14));

        // Add double-click listener
        bookList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String selectedBook = bookList.getSelectedValue();
                    if (selectedBook != null) {
                        showBookDetailsDialog(selectedBook);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(bookList);
        booksPanel.add(scrollPane, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton viewDetailsButton = createStyledButton("View Details");
        viewDetailsButton.setPreferredSize(new Dimension(150, 35));

        viewDetailsButton.addActionListener(e -> {
            String selectedBook = bookList.getSelectedValue();
            if (selectedBook != null) {
                showBookDetailsDialog(selectedBook);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please select a book first",
                        "No Selection",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        buttonPanel.add(viewDetailsButton);
        booksPanel.add(buttonPanel, BorderLayout.SOUTH);

        return booksPanel;
    }

    private void showBookDetailsDialog(String bookTitle) {
        JDialog dialog = new JDialog(this, "Book Details", true);
        dialog.setSize(400, 491);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Details Panel
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Book details
        String[] labels = {
                "ISBN:", "Title:", "Author:", "Genre:",
                "Date Published:", "Available Copies:", "Status:"
        };
        String[] values = {
                "12345", bookTitle, "Author Name", "Genre",
                "2024-01-01", "5", "Available"
        };

        for (int i = 0; i < labels.length; i++) {
            JLabel label = new JLabel(labels[i]);
            label.setFont(new Font("Serif", Font.BOLD, 14));
            gbc.gridx = 0;
            gbc.gridy = i;
            detailsPanel.add(label, gbc);

            JLabel value = new JLabel(values[i]);
            value.setFont(new Font("Serif", Font.PLAIN, 14));
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            detailsPanel.add(value, gbc);
        }

        // Status Panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel statusLabel = new JLabel("AVAILABLE");
        statusLabel.setFont(new Font("Serif", Font.BOLD, 16));
        statusLabel.setForeground(new Color(0, 150, 0));
        statusPanel.add(statusLabel);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        JButton borrowButton = createStyledButton("Borrow");
        borrowButton.setPreferredSize(new Dimension(100, 35));
        borrowButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(dialog,
                    "Book borrowed successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });

        JButton closeButton = createStyledButton("Close");
        closeButton.setPreferredSize(new Dimension(100, 35));
        closeButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(borrowButton);
        buttonPanel.add(closeButton);

        mainPanel.add(statusPanel, BorderLayout.NORTH);
        mainPanel.add(detailsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }
    private void showProfilePanel() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        JPanel profilePanel = new JPanel(new BorderLayout(20, 20));
        profilePanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header with User Info
        JPanel headerPanel = createProfileHeaderPanel();

        // Main Content (Form + Stats)
        JPanel mainContent = new JPanel(new BorderLayout(20, 20));
        JPanel formPanel = createProfileFormPanel();
        JPanel statsPanel = createProfileStatsPanel();

        mainContent.add(formPanel, BorderLayout.CENTER);
        mainContent.add(statsPanel, BorderLayout.SOUTH);

        // Button Panel
        JPanel buttonPanel = createProfileButtonPanel();

        profilePanel.add(headerPanel, BorderLayout.NORTH);
        profilePanel.add(mainContent, BorderLayout.CENTER);
        profilePanel.add(buttonPanel, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(profilePanel);
        scrollPane.setBorder(null);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        refreshPanel();
    }

    private JPanel createProfileHeaderPanel() {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));

        /* TODO: SQL Query for user avatar
           SELECT avatar_path FROM users WHERE user_id = ?
        */

        JLabel avatarLabel = new JLabel(new ImageIcon("default_avatar.png")); // Placeholder
        avatarLabel.setPreferredSize(new Dimension(100, 100));
        headerPanel.add(avatarLabel);

        return headerPanel;
    }

    private JPanel createProfileFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 10, 10);

        String[] labels = {"First Name:", "Last Name:", "Email:", "Phone:", "Address:", "Membership Since:"};
        profileFields = new JTextField[labels.length];

        /* TODO: SQL Query for user details
           SELECT first_name, last_name, email, phone, address, membership_date
           FROM users WHERE user_id = ?
        */

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            JLabel label = new JLabel(labels[i]);
            label.setFont(new Font("Serif", Font.BOLD, 14));
            formPanel.add(label, gbc);

            gbc.gridx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            profileFields[i] = new JTextField(20);
            profileFields[i].setFont(new Font("Serif", Font.PLAIN, 14));
            profileFields[i].setEditable(false);
            formPanel.add(profileFields[i], gbc);
        }

        return formPanel;
    }

    private JPanel createProfileStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        statsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Account Statistics",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Serif", Font.BOLD, 14)
        ));

        /* TODO: SQL Queries for statistics */
        addStatLabel(statsPanel, "Currently Borrowed:", "0");
        addStatLabel(statsPanel, "Total Books Borrowed:", "0");
        addStatLabel(statsPanel, "Overdue Books:", "0");
        addStatLabel(statsPanel, "Favorite Genre:", "N/A");

        return statsPanel;
    }

    private JPanel createProfileButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton editButton = new JButton("Edit Profile");
        JButton saveButton = new JButton("Save Changes");
        saveButton.setEnabled(false);

        editButton.addActionListener(e -> enableProfileEditing(editButton, saveButton));
        saveButton.addActionListener(e -> saveProfileChanges(editButton, saveButton));

        buttonPanel.add(editButton);
        buttonPanel.add(saveButton);

        return buttonPanel;
    }

    private void enableProfileEditing(JButton editButton, JButton saveButton) {
        for (JTextField field : profileFields) {
            field.setEditable(true);
        }
        saveButton.setEnabled(true);
        editButton.setEnabled(false);
    }

    private void saveProfileChanges(JButton editButton, JButton saveButton) {
        /* TODO: SQL Update query
           UPDATE users
           SET first_name = ?, last_name = ?, email = ?,
               phone = ?, address = ?
           WHERE user_id = ?
        */

        for (JTextField field : profileFields) {
            field.setEditable(false);
        }
        saveButton.setEnabled(false);
        editButton.setEnabled(true);

        JOptionPane.showMessageDialog(contentPanel,
                "Profile updated successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showHistoryPanel() {
        contentPanel.removeAll();

        /* TODO: SQL Query for borrowing history
           SELECT b.title, br.borrow_date, br.due_date, br.return_date,
           CASE WHEN br.return_date IS NULL AND br.due_date < CURRENT_DATE
                THEN 'Overdue'
                WHEN br.return_date IS NULL
                THEN 'Borrowed'
                ELSE 'Returned'
           END as status
           FROM borrowings br
           JOIN books b ON br.book_id = b.id
           WHERE br.user_id = ?
           ORDER BY br.borrow_date DESC
        */

        JPanel historyPanel = new JPanel(new BorderLayout(20, 20));
        historyPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        historyPanel.add(new JLabel("Borrowing History Section"));

        contentPanel.add(historyPanel);
        refreshPanel();
    }

    private void handleLogout() {
        int response = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            dispose();
            LibraryGUI libraryGUI = new LibraryGUI();
            libraryGUI.setVisible(true);
        }
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

        button.setPreferredSize(new Dimension(250, 50));
        button.setFont(new Font("Serif", Font.PLAIN, 16));
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

    private void addStatLabel(JPanel panel, String label, String value) {
        JPanel statPanel = new JPanel(new BorderLayout(5, 5));
        JLabel titleLabel = new JLabel(label);
        titleLabel.setFont(new Font("Serif", Font.PLAIN, 14));
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Serif", Font.BOLD, 16));
        valueLabel.setForeground(DARK_GREEN);
        statPanel.add(titleLabel, BorderLayout.NORTH);
        statPanel.add(valueLabel, BorderLayout.CENTER);
        panel.add(statPanel);
    }

    private void refreshPanel() {
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new UserDashboard().setVisible(true);
        });
    }
}