package com.pannel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;
import com.util.DBConnection;

public class UserPanel extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JTextField userIdField, nameField, emailField, searchField;
    private JButton addButton, updateButton, deleteButton, refreshButton, exitButton;
    private TableRowSorter<DefaultTableModel> rowSorter;

    public UserPanel() {
        setTitle("Manage Users");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // CHANGED: Set background color for main frame to light purple
        getContentPane().setBackground(new Color(245, 240, 250));

        // Search Panel - CHANGED COLORS
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        searchPanel.setBackground(new Color(230, 220, 240)); // CHANGED: Light purple background
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(147, 112, 219), 1), // CHANGED: Purple border
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        JLabel searchLabel = new JLabel(" Search:");
        searchLabel.setForeground(new Color(75, 0, 130)); // CHANGED: Dark purple text
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchField = new JTextField();
        searchField.setBackground(Color.WHITE);
        searchField.setBorder(BorderFactory.createLineBorder(new Color(147, 112, 219), 1));
        
        JButton searchButton = new JButton("Search");
        // CHANGED: Button colors to purple
        searchButton.setBackground(new Color(147, 112, 219));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        searchButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        searchButton.setFocusPainted(false);
        
        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        // Table setup - CHANGED COLORS
        model = new DefaultTableModel();
        table = new JTable(model);
        table.setBackground(Color.WHITE);
        table.setGridColor(new Color(200, 200, 200));
        table.setSelectionBackground(new Color(216, 191, 216)); // CHANGED: Plum selection color
        table.setSelectionForeground(Color.BLACK);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setBackground(new Color(75, 0, 130)); // CHANGED: Dark purple header
        table.getTableHeader().setForeground(new Color(255, 215, 0)); // CHANGED: Gold text
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        model.addColumn("User ID");
        model.addColumn("Name");
        model.addColumn("Email");
        model.addColumn("Created At");

        // ADDED ROW SORTER FOR SEARCH
        rowSorter = new TableRowSorter<>(model);
        table.setRowSorter(rowSorter);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(147, 112, 219), 1));
        
        // Form panel - CHANGED COLORS
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(75, 0, 130), 2), // CHANGED: Dark purple border
            "User Details",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Segoe UI", Font.BOLD, 12),
            new Color(75, 0, 130) // CHANGED: Dark purple text
        ));
        formPanel.setBackground(new Color(250, 245, 255)); // CHANGED: Very light purple

        // CHANGED: Style for labels
        JLabel userIdLabel = new JLabel("User ID:");
        userIdLabel.setForeground(new Color(75, 0, 130));
        userIdLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setForeground(new Color(75, 0, 130));
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(new Color(75, 0, 130));
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        userIdField = new JTextField();
        nameField = new JTextField();
        emailField = new JTextField();
        
        // CHANGED: Style for text fields
        StyleTextField(userIdField);
        StyleTextField(nameField);
        StyleTextField(emailField);

        formPanel.add(userIdLabel);
        formPanel.add(userIdField);
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(emailLabel);
        formPanel.add(emailField);

        // Buttons panel - CHANGED COLORS
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(245, 240, 250));
        
        // CHANGED: Different color scheme for buttons
        addButton = createStyledButton("Add", new Color(50, 205, 50)); // Lime green
        updateButton = createStyledButton("Update", new Color(255, 140, 0)); // Dark orange
        deleteButton = createStyledButton("Delete", new Color(178, 34, 34)); // Firebrick red
        refreshButton = createStyledButton("Refresh", new Color(147, 112, 219)); // Medium purple
        exitButton = createStyledButton("Exit", new Color(105, 105, 105)); // Dim gray

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(exitButton);

        // CHANGED: Arrange components in main container with new colors
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(245, 240, 250));
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        // ADDED: Search button action listener
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });

        // ADDED: Real-time search on key press
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                performSearch();
            }
        });

        // Button actions (anonymous inner classes) - EXISTING CODE
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    addUser();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(UserPanel.this, "Error: " + ex.getMessage());
                }
            }
        });

        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    updateUser();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(UserPanel.this, "Error: " + ex.getMessage());
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    deleteUser();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(UserPanel.this, "Error: " + ex.getMessage());
                }
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    loadUsers();
                    searchField.setText(""); // ADDED: Clear search on refresh
                    rowSorter.setRowFilter(null); // ADDED: Reset filter on refresh
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(UserPanel.this, "Error: " + ex.getMessage());
                }
            }
        });

        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // Table row click to load fields - EXISTING CODE
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    // Convert view row index to model row index for sorted table
                    int modelRow = table.convertRowIndexToModel(row);
                    userIdField.setText(table.getModel().getValueAt(modelRow, 0).toString());
                    nameField.setText(table.getModel().getValueAt(modelRow, 1).toString());
                    emailField.setText(table.getModel().getValueAt(modelRow, 2).toString());
                }
            }
        });

        try {
            loadUsers();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage());
        }

        setVisible(true);
    }

    // CHANGED: Method to style text fields with purple theme
    private void StyleTextField(JTextField textField) {
        textField.setBackground(Color.WHITE);
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(147, 112, 219), 1), // CHANGED: Purple border
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }

    // CHANGED: Method to create styled buttons with new colors
    private JButton createStyledButton(String text, final Color baseColor) {
        final JButton button = new JButton(text);
        button.setBackground(baseColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        
        // ADDED: Hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(baseColor.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(baseColor);
            }
        });
        
        return button;
    }

    // ADDED: Search functionality method
    private void performSearch() {
        String searchText = searchField.getText().trim();
        
        if (searchText.length() == 0) {
            rowSorter.setRowFilter(null);
        } else {
            try {
                // Search in Name (column 1), Email (column 2), and User ID (column 0)
                RowFilter<DefaultTableModel, Object> rf = RowFilter.regexFilter("(?i)" + searchText, 1, 2, 0);
                rowSorter.setRowFilter(rf);
            } catch (java.util.regex.PatternSyntaxException e) {
                return; // If invalid regex, do nothing
            }
        }
    }

    // EXISTING METHODS (unchanged)
    private void loadUsers() throws Exception {
        model.setRowCount(0);
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM users";
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                Vector<Object> row = new Vector<Object>();
                row.add(rs.getInt("user_id"));
                row.add(rs.getString("username"));
                row.add(rs.getString("email"));
                row.add(rs.getTimestamp("created_at"));
                model.addRow(row);
            }
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) {}
            if (pst != null) try { pst.close(); } catch (SQLException e) {}
            if (conn != null) try { conn.close(); } catch (SQLException e) {}
        }
    }

    private void addUser() throws Exception {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        if (name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and Email are required.");
            return;
        }

        Connection conn = null;
        PreparedStatement pst = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO users (username, email, created_at) VALUES (?, ?, NOW())";
            pst = conn.prepareStatement(sql);
            pst.setString(1, name);
            pst.setString(2, email);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "User added successfully!");
            clearFields();
            loadUsers();
        } finally {
            if (pst != null) try { pst.close(); } catch (SQLException e) {}
            if (conn != null) try { conn.close(); } catch (SQLException e) {}
        }
    }

    private void updateUser() throws Exception {
        String userId = userIdField.getText().trim();
        String username = nameField.getText().trim();
        String email = emailField.getText().trim();
        if (userId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a user to update.");
            return;
        }

        Connection conn = null;
        PreparedStatement pst = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE users SET username=?, email=? WHERE user_id=?";
            pst = conn.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, email);
            pst.setInt(3, Integer.parseInt(userId));
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "User updated successfully!");
            clearFields();
            loadUsers();
        } finally {
            if (pst != null) try { pst.close(); } catch (SQLException e) {}
            if (conn != null) try { conn.close(); } catch (SQLException e) {}
        }
    }

    private void deleteUser() throws Exception {
        String userId = userIdField.getText().trim();
        if (userId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a user to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this user?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        Connection conn = null;
        PreparedStatement pst = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "DELETE FROM users WHERE user_id=?";
            pst = conn.prepareStatement(sql);
            pst.setInt(1, Integer.parseInt(userId));
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "User deleted successfully!");
            clearFields();
            loadUsers();
        } finally {
            if (pst != null) try { pst.close(); } catch (SQLException e) {}
            if (conn != null) try { conn.close(); } catch (SQLException e) {}
        }
    }

    private void clearFields() {
        userIdField.setText("");
        nameField.setText("");
        emailField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new UserPanel();
            }
        });
    }
}