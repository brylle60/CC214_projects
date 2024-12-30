package Gui;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class AboutUs extends JFrame {
    private static final Color DARK_GREEN = new Color(40, 54, 44);
    private static final Color GOLD = new Color(184, 157, 102);
    private static final Color BRONZE = new Color(176, 141, 87);

    private LibraryGUI libraryGUI;

    public AboutUs(LibraryGUI libraryGUI) {
        this.libraryGUI = libraryGUI;

        setTitle("About Us - Library Management System");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        // Main container
        JPanel mainContainer = new JPanel(new GridLayout(1, 2));

        // Left Panel (Dark green with image and title)
        JPanel leftPanel = new JPanel(null);
        leftPanel.setBackground(DARK_GREEN);

        // Adding top space
        leftPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

        // Back button
        JButton backButton = createStyledButton("Back");
        backButton.setBounds(50, 20, 100, 30);
        backButton.addActionListener(e -> {
            libraryGUI.setVisible(true);
            dispose();
        });
        leftPanel.add(backButton);

        // "ABOUT US" text
        JLabel aboutUsLabel = new JLabel("ABOUT US");
        aboutUsLabel.setForeground(GOLD);
        aboutUsLabel.setFont(new Font("Serif", Font.BOLD, 48));
        aboutUsLabel.setBounds(50, 70, 300, 60);
        leftPanel.add(aboutUsLabel);

        // Add image - positioned on the left
        try {
            ImageIcon imageIcon = new ImageIcon(ImageIO.read(new File("C:\\Users\\janlo\\IdeaProjects\\LIBRARY MANAGEMENT\\src\\AboutUs.png")));
            Image image = imageIcon.getImage().getScaledInstance(400, 300, Image.SCALE_SMOOTH);  // Adjusted size for better fit
            JLabel imageLabel = new JLabel(new ImageIcon(image));
            imageLabel.setBounds(50, 150, 400, 300);  // Positioned on the left side
            leftPanel.add(imageLabel);
        } catch (IOException e) {
            System.out.println("Error loading image: " + e.getMessage());
            e.printStackTrace();
        }

        // Right Panel (Bronze colored with text)
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(BRONZE);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(40, 30, 40, 30));

        // Welcome text
        JLabel welcomeLabel = createStyledLabel(
                "<html><div style='width: 300px;'>Welcome to the Library Management System—your one-stop solution for managing library operations efficiently and effectively!</div></html>",
                18
        );

        // Description text
        JLabel descriptionLabel = createStyledLabel(
                "<html><div style='width: 300px;'>Our system is designed to streamline the management of books, users, borrowing processes, and more, enabling libraries to serve their communities better. Whether you're a librarian, student, or avid reader, our system offers a user-friendly interface and powerful features to make your library experience seamless.</div></html>",
                18
        );

        // Add components to right panel with spacing
        rightPanel.add(welcomeLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        rightPanel.add(descriptionLabel);

        // Add panels to main container
        mainContainer.add(leftPanel);
        mainContainer.add(rightPanel);

        add(mainContainer);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(GOLD);
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                g2d.setFont(getFont());
                FontMetrics metrics = g2d.getFontMetrics();
                int x = (getWidth() - metrics.stringWidth(getText())) / 2;
                int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
                g2d.drawString(getText(), x, y);
            }
        };
        button.setForeground(GOLD);
        button.setFont(new Font("Serif", Font.PLAIN, 16));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setForeground(GOLD.brighter());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setForeground(GOLD);
            }
        });
        return button;
    }

    private JLabel createStyledLabel(String text, int fontSize) {
        JLabel label = new JLabel(text);
        label.setForeground(DARK_GREEN);
        label.setFont(new Font("SansSerif", Font.PLAIN, fontSize));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new AboutUs(new LibraryGUI()).setVisible(true);
        });
    }
}
