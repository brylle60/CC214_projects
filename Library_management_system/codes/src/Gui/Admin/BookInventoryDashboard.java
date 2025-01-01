package Gui.Admin;
import DSA.Admin.AdminControls;
import DSA.Admin.MySQLbookDb;
import DSA.Objects.Books;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.time.Instant;
import java.time.ZoneId;

public class BookInventoryDashboard extends JPanel {
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JPanel buttonPanel;
    private final JPanel topPanel;
    private final JTextField searchField;
    private static final Color DARK_GREEN = new Color(40, 54, 44);
    private static final Color BUTTON_COLOR = new Color(184, 207, 229);

    public BookInventoryDashboard() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create top panel for search and buttons
        topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setBackground(Color.WHITE);

        // Create search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.WHITE);

        JLabel searchLabel = new JLabel("Search:");
        searchField = new JTextField(20);
        JButton searchButton = createStyledButton("Search");
        JButton reloadButton = createStyledButton("↻ Reload");

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(reloadButton);

        // Create button panel for CRUD operations
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton addButton = createStyledButton("Add Book");
        JButton removeButton = createStyledButton("Remove Book");
        JButton updateButton = createStyledButton("Update Book");

        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(updateButton);

        // Add both panels to top panel
        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        // Create table model with columns
        String[] columns = {"ID", "Title", "Genre", "Author", "Publish Date", "Available Copies", "Total Copies"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Create and configure table
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        table.getTableHeader().setBackground(DARK_GREEN);
        table.getTableHeader().setForeground(Color.BLACK);
        table.setGridColor(Color.BLACK);

        // Configure table appearance
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(DARK_GREEN));
        table.setFillsViewportHeight(true);

        // Add components to main panel
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Add button listeners
        addButton.addActionListener(e -> addBook());
        removeButton.addActionListener(e -> removeBook());
        updateButton.addActionListener(e -> updateBook());
        reloadButton.addActionListener(e -> refreshTableData());

        // Add search functionality
        searchButton.addActionListener(e -> performSearch());
        searchField.addActionListener(e -> performSearch()); // Allow search on Enter key

        // Load initial data
        refreshTableData();
    }

    private void performSearch() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        if (searchTerm.isEmpty()) {
            refreshTableData();
            return;
        }
        tableModel.setRowCount(0);
        List<Books> books = MySQLbookDb.LoadBooks();
        for (Books book : books) {
            if (matchesSearch(book, searchTerm)) {
                addBookToTable(book);
            }
        }
    }

    private boolean matchesSearch(Books book, String searchTerm) {
        return book.getTitle().toLowerCase().contains(searchTerm) ||
                book.getAuthor().toLowerCase().contains(searchTerm) ||
                book.getGenre().toLowerCase().contains(searchTerm) ||
                String.valueOf(book.getISBN()).contains(searchTerm);
    }

    private void refreshTableData() {
        tableModel.setRowCount(0); // Clear existing rows
        List<Books> books = MySQLbookDb.LoadBooks();

        for (Books book : books) {
            Object[] row = new Object[]{
                    book.getISBN(),
                    book.getTitle(),
                    book.getGenre(),
                    book.getAuthor(),
                    book.getDatePublished(),
                    book.getAvailableCopy(),
                    book.getTotalCopy()
            };
            tableModel.addRow(row);
        }
    }
    private void addBookToTable(Books book) {
        Object[] row = {
                book.getISBN(),
                book.getTitle(),
                book.getGenre(),
                book.getAuthor(),
                book.getDatePublished(),
                book.getAvailableCopy(),
                book.getTotalCopy()
        };
        tableModel.addRow(row);
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

    private void addBook() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Book", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 450);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create form fields
        JTextField idField = new JTextField();
        JTextField titleField = new JTextField();
        JTextField genreField = new JTextField();
        JTextField authorField = new JTextField();
        JSpinner copiesSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        JSpinner totalCopiesSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));

        // Use JSpinner for date selection
        SpinnerDateModel dateModel = new SpinnerDateModel();
        JSpinner dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);

        // Add form components
        formPanel.add(new JLabel("ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Title:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("Genre:"));
        formPanel.add(genreField);
        formPanel.add(new JLabel("Author:"));
        formPanel.add(authorField);
        formPanel.add(new JLabel("Publish Date:"));
        formPanel.add(dateSpinner);
        formPanel.add(new JLabel("Available Copies:"));
        formPanel.add(copiesSpinner);
        formPanel.add(new JLabel("Total Copies:"));
        formPanel.add(totalCopiesSpinner);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = createStyledButton("Save");
        JButton cancelButton = createStyledButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText());
                String title = titleField.getText();
                String genre = genreField.getText();
                String author = authorField.getText();
                java.util.Date selectedDate = (java.util.Date) dateSpinner.getValue();
                LocalDateTime publishDate = selectedDate.toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDateTime();
                int copies = (Integer) copiesSpinner.getValue();
                int totalCopies = (Integer) totalCopiesSpinner.getValue();

                if (MySQLbookDb.AddBooks(id, title, genre, author, publishDate, copies, totalCopies)) {
                    JOptionPane.showMessageDialog(dialog, "Book added successfully!");
                    dialog.dispose();
                    refreshTableData();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid numbers for ID and copies.",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void updateBook() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to update.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Update Book", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 450);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Get current values
        int id = (int) table.getValueAt(selectedRow, 0);
        String currentTitle = (String) table.getValueAt(selectedRow, 1);
        String currentGenre = (String) table.getValueAt(selectedRow, 2);
        String currentAuthor = (String) table.getValueAt(selectedRow, 3);
        Object dateValue = table.getValueAt(selectedRow, 4);
        java.util.Date currentDate;
        if (dateValue instanceof java.sql.Date) {
            currentDate = (java.sql.Date) dateValue;
        } else if (dateValue instanceof LocalDateTime) {
            currentDate = java.sql.Date.valueOf(((LocalDateTime) dateValue).toLocalDate());
        } else {
            currentDate = new java.util.Date(); // fallback to current date
        }        int currentCopies = (int) table.getValueAt(selectedRow, 5);
        int currentTotalCopies = (int) table.getValueAt(selectedRow, 6);

        // Create form fields with current values
        JTextField idField = new JTextField(String.valueOf(id));
        idField.setEditable(false); // ID shouldn't be changed
        JTextField titleField = new JTextField(currentTitle);
        JTextField genreField = new JTextField(currentGenre);
        JTextField authorField = new JTextField(currentAuthor);
        JSpinner copiesSpinner = new JSpinner(new SpinnerNumberModel(currentCopies, 0, 1000, 1));
        JSpinner totalCopiesSpinner = new JSpinner(new SpinnerNumberModel(currentTotalCopies, 0, 1000, 1));

        // Date spinner
        SpinnerDateModel dateModel = new SpinnerDateModel();
        dateModel.setValue(currentDate);
        JSpinner dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);

        // Add form components
        formPanel.add(new JLabel("ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Title:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("Genre:"));
        formPanel.add(genreField);
        formPanel.add(new JLabel("Author:"));
        formPanel.add(authorField);
        formPanel.add(new JLabel("Publish Date:"));
        formPanel.add(dateSpinner);
        formPanel.add(new JLabel("Available Copies:"));
        formPanel.add(copiesSpinner);
        formPanel.add(new JLabel("Total Copies:"));
        formPanel.add(totalCopiesSpinner);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = createStyledButton("Save");
        JButton cancelButton = createStyledButton("Cancel");
        saveButton.addActionListener(e -> {
            try {
                java.util.Date selectedDate = (java.util.Date) dateSpinner.getValue();
                // Convert java.util.Date to LocalDateTime
                Instant instant = selectedDate.toInstant();
                LocalDateTime publishDate = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();

                int copies = (Integer) copiesSpinner.getValue();
                int totalCopies = (Integer) totalCopiesSpinner.getValue();

                // Use UpdateBook instead of AddBooks
                if (MySQLbookDb.UpdateBook(id, titleField.getText(), genreField.getText(),
                        authorField.getText(), publishDate, copies, totalCopies)) {
                    JOptionPane.showMessageDialog(dialog, "Book updated successfully!");
                    dialog.dispose();
                    refreshTableData();
                } else {
                    JOptionPane.showMessageDialog(dialog, "No book was updated. Please check the ID.",
                            "Update Error", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error updating book: " + ex.getMessage(),
                        "Update Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void removeBook() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a book to remove.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) table.getValueAt(selectedRow, 0);
        String title = (String) table.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to remove the book:\n" +
                        "Title: " + title + "\nID: " + id,
                "Confirm Removal",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (MySQLbookDb.delete(id)) {
                    // Also update the AdminControls instance to maintain consistency
                    AdminControls.getInstance().refreshBooks();

                    // Refresh the table data
                    refreshTableData();

                    JOptionPane.showMessageDialog(this,
                            "Book removed successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to remove the book. Please try again.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error removing book: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}