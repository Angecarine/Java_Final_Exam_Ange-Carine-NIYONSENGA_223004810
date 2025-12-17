package com.Form;

import com.util.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RegisterForm extends JFrame implements ActionListener {

    private JTextField usernameField, emailField;
    private JPasswordField passwordField;
    private JComboBox<String> roleCombo;
    private JButton registerButton, backButton;

    public RegisterForm() {
        setTitle("User Registration");
        setSize(420, 380);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ====== TITLE BAR ======
        JLabel titleLabel = new JLabel("REGISTER NEW USER", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(65, 105, 225)); // Royal Blue
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setPreferredSize(new Dimension(420, 60));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(titleLabel, BorderLayout.NORTH);

        // ====== FORM PANEL ======
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        formPanel.setBackground(new Color(245, 245, 245));

        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(lblUsername);

        usernameField = new JTextField();
        formPanel.add(usernameField);

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(lblEmail);

        emailField = new JTextField();
        formPanel.add(emailField);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(lblPassword);

        passwordField = new JPasswordField();
        formPanel.add(passwordField);

        JLabel lblRole = new JLabel("Role:");
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(lblRole);

        roleCombo = new JComboBox<String>(new String[]{"Admin", "Editor", "Viewer"});
        formPanel.add(roleCombo);

        add(formPanel, BorderLayout.CENTER);

        // ====== BUTTON PANEL ======
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(230, 230, 250));

        registerButton = createStyledButton("Register", new Color(46, 139, 87)); // Sea Green
        backButton = createStyledButton("Back", new Color(220, 20, 60));         // Crimson

        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    // ====== CREATE COLORED BUTTON WITH HOVER EFFECT ======
    private JButton createStyledButton(final String text, final Color baseColor) {
        final JButton btn = new JButton(text);
        btn.setBackground(baseColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(baseColor.darker());
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(baseColor);
            }
        });

        btn.addActionListener(this);
        return btn;
    }

    // ====== ACTION EVENTS ======
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == backButton) {
            dispose();
            new LoginForm();
            return;
        }

        if (src == registerButton) {
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String role = (String) roleCombo.getSelectedItem();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!");
                return;
            }

            Connection conn = null;
            PreparedStatement pst = null;

            try {
                conn = DBConnection.getConnection();
                if (conn == null) {
                    JOptionPane.showMessageDialog(this, "Database connection failed!");
                    return;
                }

                String sql = "INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, ?)";
                pst = conn.prepareStatement(sql);
                pst.setString(1, username);
                pst.setString(2, email);
                pst.setString(3, password);
                pst.setString(4, role);

                int rows = pst.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, " Registration successful!");
                    dispose();
                    new LoginForm();
                } else {
                    JOptionPane.showMessageDialog(this, " Registration failed!");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            } finally {
                try { if (pst != null) pst.close(); } catch (Exception ex) {}
                try { if (conn != null) conn.close(); } catch (Exception ex) {}
            }
        }
    }

    public static void main(String[] args) {
        new RegisterForm();
    }
}
