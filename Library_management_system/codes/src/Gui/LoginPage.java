package Gui;

import javax.swing.*;
import java.awt.*;

public class LoginPage extends JFrame {
    private static final Color GOLD = new Color(0xAF, 0x8C, 0x53);
    private static final Color TEXT_COLOR = new Color(0x25, 0x29, 0x26);
    private static final Color INPUT_TEXT_COLOR = GOLD; // Color for user input text
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginPage() {
        setTitle("Sign In");
        setSize(400, 491);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);


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


        JLabel titleLabel = new JLabel("SIGN IN");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        titleLabel.setForeground(GOLD);
        titleLabel.setBounds(150, 50, 150, 30);
        mainPanel.add(titleLabel);


        usernameField = createTransparentTextField();
        usernameField.setBounds(75, 150, 250, 35);
        mainPanel.add(usernameField);


        JLabel usernamePlaceholder = new JLabel("Enter Username");
        usernamePlaceholder.setFont(new Font("Serif", Font.PLAIN, 14));
        usernamePlaceholder.setForeground(GOLD);
        usernamePlaceholder.setBounds(75, 130, 250, 20);
        mainPanel.add(usernamePlaceholder);


        passwordField = createTransparentPasswordField();
        passwordField.setBounds(75, 230, 250, 35);
        mainPanel.add(passwordField);


        JLabel passwordPlaceholder = new JLabel("Enter Password");
        passwordPlaceholder.setFont(new Font("Serif", Font.PLAIN, 14));
        passwordPlaceholder.setForeground(GOLD);
        passwordPlaceholder.setBounds(75, 210, 250, 20);
        mainPanel.add(passwordPlaceholder);

        JButton loginButton = createStyledButton("LOG IN", true);
        loginButton.setBounds(75, 300, 250, 40);
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(LoginPage.this,
                        "Please enter both username and password",
                        "Login Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            System.out.println("Login attempt - Username: " + username);
        });
        mainPanel.add(loginButton);

        JButton createAccountButton = createStyledButton("No Account? Click to Create", false);
        createAccountButton.setBounds(75, 360, 250, 40);
        createAccountButton.addActionListener(e -> {
            CreateAccountPage createAccountPage = new CreateAccountPage();
            createAccountPage.setVisible(true);
        });
        mainPanel.add(createAccountButton);

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
        field.setForeground(INPUT_TEXT_COLOR); // Set the text color for user input
        field.setCaretColor(INPUT_TEXT_COLOR); // Set the cursor color
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
        field.setForeground(INPUT_TEXT_COLOR); // Set the text color for user input
        field.setCaretColor(INPUT_TEXT_COLOR); // Set the cursor color
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
    }
}
