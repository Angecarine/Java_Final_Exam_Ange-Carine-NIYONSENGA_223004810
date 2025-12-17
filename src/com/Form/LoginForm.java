package com.Form;

import com.util.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginForm extends JFrame implements ActionListener {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton;
    private JComboBox<String> roleCombo;

    public LoginForm() {
        setTitle("Login Form");
        setSize(420, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ====== HEADER ======
        JLabel title = new JLabel("LOGIN FORM", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setOpaque(true);
        title.setBackground(new Color(70, 130, 180)); // blue header
        title.setForeground(Color.WHITE);
        title.setPreferredSize(new Dimension(420, 50));
        add(title, BorderLayout.NORTH);

        // ====== FORM PANEL ======
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        formPanel.setBackground(new Color(245, 245, 245));

        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        formPanel.add(emailField);

        formPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);

        formPanel.add(new JLabel("Role:"));
        roleCombo = new JComboBox<String>(new String[]{"Admin", "Editor", "Viewer"});
        formPanel.add(roleCombo);

        add(formPanel, BorderLayout.CENTER);

        // ====== BUTTON PANEL ======
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(230, 230, 230));

        loginButton = createStyledButton("Login", new Color(60, 179, 113));     // Green
        registerButton = createStyledButton("Register", new Color(100, 149, 237)); // Cornflower blue

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    // ====== STYLED BUTTON CREATOR ======
    private JButton createStyledButton(final String text, final Color baseColor) {
        final JButton btn = new JButton(text);
        btn.setBackground(baseColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

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

    public void actionPerformed(ActionEvent ev) {
        Object src = ev.getSource();

        if (src == registerButton) {
            dispose();
            new RegisterForm();
            return;
        }

        if (src == loginButton) {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String selectedRole = (String) roleCombo.getSelectedItem();

            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both email and password!");
                return;
            }

            Connection conn = null;
            PreparedStatement pst = null;
            ResultSet rs = null;

            try {
                conn = DBConnection.getConnection();
                if (conn == null) {
                    JOptionPane.showMessageDialog(this, "Database connection failed!");
                    return;
                }

                String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
                pst = conn.prepareStatement(sql);
                pst.setString(1, email);
                pst.setString(2, password);
                rs = pst.executeQuery();

                if (rs.next()) {
                    String dbRole = rs.getString("role");
                    String username = rs.getString("username");

                    if (dbRole.equalsIgnoreCase(selectedRole)) {
                        JOptionPane.showMessageDialog(this, "Welcome " + username + " (" + dbRole + ")");
                        dispose();

                        if (dbRole.equalsIgnoreCase("Admin")) {
                            new AdminDashboard(username);
                        } else if (dbRole.equalsIgnoreCase("Editor")) {
                            new ContentCreatorDashboard(username);
                        } else if (dbRole.equalsIgnoreCase("Viewer")) {
                            new ViewerDashboard(username);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Role mismatch! You are registered as: " + dbRole);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid email or password!");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            } finally {
                try { if (rs != null) rs.close(); } catch (Exception e) {}
                try { if (pst != null) pst.close(); } catch (Exception e) {}
                try { if (conn != null) conn.close(); } catch (Exception e) {}
            }
        }
    }

    public static void main(String[] args) {
        new LoginForm();
    }
}
