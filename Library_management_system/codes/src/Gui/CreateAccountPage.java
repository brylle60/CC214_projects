package Gui;

import javax.swing.*;
import java.awt.*;

public class CreateAccountPage extends JFrame {
    private static final Color GOLD = new Color(0xAF, 0x8C, 0x53);
    private static final Color TEXT_COLOR = new Color(0x25, 0x29, 0x26);
    private static final Color INPUT_TEXT_COLOR = GOLD;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField emailField;

    public CreateAccountPage() {
        setTitle("Create Account");
        setSize(400, 591);  // Made taller to accommodate additional fields
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);  // Only close this window, not the whole application
        setLocationRelativeTo(null);
        setResizable(false);

        // Create main panel with background image
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon imageIcon = new ImageIcon("Library_management_system/codes/src/Images/login.jpg");
                Image image = imageIcon.getImage();
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            }
        };
        mainPanel.setLayout(null);

        // Create Account Label
        JLabel titleLabel = new JLabel("CREATE ACCOUNT");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        titleLabel.setForeground(GOLD);
        titleLabel.setBounds(110, 50, 250, 30);
        mainPanel.add(titleLabel);

        // Username Field
        usernameField = createTransparentTextField();
        usernameField.setBounds(75, 130, 250, 35);
        mainPanel.add(usernameField);

        // Username placeholder
        JLabel usernamePlaceholder = new JLabel("Enter Username");
        usernamePlaceholder.setFont(new Font("Serif", Font.PLAIN, 14));
        usernamePlaceholder.setForeground(GOLD);
        usernamePlaceholder.setBounds(75, 110, 250, 20);
        mainPanel.add(usernamePlaceholder);

        // Email Field
        emailField = createTransparentTextField();
        emailField.setBounds(75, 200, 250, 35);
        mainPanel.add(emailField);

        // Email placeholder
        JLabel emailPlaceholder = new JLabel("Enter Email");
        emailPlaceholder.setFont(new Font("Serif", Font.PLAIN, 14));
        emailPlaceholder.setForeground(GOLD);
        emailPlaceholder.setBounds(75, 180, 250, 20);
        mainPanel.add(emailPlaceholder);

        // Password Field
        passwordField = createTransparentPasswordField();
        passwordField.setBounds(75, 270, 250, 35);
        mainPanel.add(passwordField);

        // Password placeholder
        JLabel passwordPlaceholder = new JLabel("Enter Password");
        passwordPlaceholder.setFont(new Font("Serif", Font.PLAIN, 14));
        passwordPlaceholder.setForeground(GOLD);
        passwordPlaceholder.setBounds(75, 250, 250, 20);
        mainPanel.add(passwordPlaceholder);

        // Confirm Password Field
        confirmPasswordField = createTransparentPasswordField();
        confirmPasswordField.setBounds(75, 340, 250, 35);
        mainPanel.add(confirmPasswordField);

        // Confirm Password placeholder
        JLabel confirmPasswordPlaceholder = new JLabel("Confirm Password");
        confirmPasswordPlaceholder.setFont(new Font("Serif", Font.PLAIN, 14));
        confirmPasswordPlaceholder.setForeground(GOLD);
        confirmPasswordPlaceholder.setBounds(75, 320, 250, 20);
        mainPanel.add(confirmPasswordPlaceholder);

        // Create Account Button
        JButton createAccountButton = createStyledButton("CREATE ACCOUNT", true);
        createAccountButton.setBounds(75, 410, 250, 40);
        createAccountButton.addActionListener(e -> {
            String username = usernameField.getText();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            // Add your account creation logic here
            System.out.println("Create account attempt - Username: " + username + ", Email: " + email);
        });
        mainPanel.add(createAccountButton);

        // Back to Login Button
        JButton backButton = createStyledButton("Back to Login", false);
        backButton.setBounds(75, 470, 250, 40);
        backButton.addActionListener(e -> {
            dispose(); // Close the create account window
        });
        mainPanel.add(backButton);

        add(mainPanel);
    }

    private JTextField createTransparentTextField() {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(255, 255, 255, 0));
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        field.setOpaque(false);
        field.setForeground(INPUT_TEXT_COLOR);
        field.setCaretColor(INPUT_TEXT_COLOR);
        field.setFont(new Font("Serif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        return field;
    }

    private JPasswordField createTransparentPasswordField() {
        JPasswordField field = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(255, 255, 255, 0));
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        field.setOpaque(false);
        field.setForeground(INPUT_TEXT_COLOR);
        field.setCaretColor(INPUT_TEXT_COLOR);
        field.setFont(new Font("Serif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        return field;
    }

    private JButton createStyledButton(String text, boolean filled) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                if (filled) {
                    if (getModel().isPressed()) {
                        g2d.setColor(GOLD.darker());
                    } else {
                        g2d.setColor(GOLD);
                    }
                    g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 25, 25);
                    g2d.setColor(TEXT_COLOR);
                } else {
                    if (getModel().isPressed()) {
                        g2d.setColor(GOLD.darker());
                    } else {
                        g2d.setColor(GOLD);
                    }
                    g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 25, 25);
                }

                FontMetrics metrics = g2d.getFontMetrics();
                int x = (getWidth() - metrics.stringWidth(getText())) / 2;
                int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
                g2d.drawString(getText(), x, y);
            }
        };

        button.setForeground(filled ? TEXT_COLOR : GOLD);
        button.setFont(new Font("Serif", Font.PLAIN, 16));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setForeground(filled ? TEXT_COLOR.brighter() : GOLD.brighter());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setForeground(filled ? TEXT_COLOR : GOLD);
            }
        });

        return button;
    }
}
