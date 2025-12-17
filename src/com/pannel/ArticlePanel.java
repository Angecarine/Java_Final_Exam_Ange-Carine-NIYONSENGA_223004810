package com.pannel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ArticlePanel extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtName, txtDescription, txtCategoryId, txtAuthorId, txtPrice, txtStatus;
    private JButton addButton, updateButton, deleteButton, refreshButton, exitButton;
    private JTextField searchField; // ADDED: Search field
    private TableRowSorter<DefaultTableModel> rowSorter; // ADDED: Row sorter for search

    private static final String URL = "jdbc:mysql://localhost:3306/media_monitoring";
    private static final String USER = "root";
    private static final String PASS = "";

    public ArticlePanel() {
        setTitle("Article Management Panel");
        setSize(1000, 600); // Increased height to accommodate search
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 248, 255)); // light blue background

        // ===== SEARCH PANEL - ADDED =====
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        searchPanel.setBackground(new Color(240, 248, 255));
        
        JLabel searchLabel = new JLabel("Search:");
        searchField = new JTextField();
        
        JButton searchButton = new JButton("Search");
        searchButton.setBackground(new Color(135, 206, 250)); // light sky blue
        searchButton.setFocusPainted(false);
        
        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        // ===== TABLE =====
        model = new DefaultTableModel(new String[]{
                "Article ID", "Name", "Description", "Category ID", "Author ID", "Price/Value", "Status", "Created At"
        }, 0);
        table = new JTable(model);
        table.setBackground(new Color(255, 255, 255));
        table.setGridColor(new Color(200, 200, 200));
        table.setSelectionBackground(new Color(173, 216, 230));
        
        // ADDED: Row sorter for search functionality
        rowSorter = new TableRowSorter<>(model);
        table.setRowSorter(rowSorter);
        
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ===== FORM PANEL =====
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Article Details"));
        formPanel.setBackground(new Color(224, 255, 255)); // mint background

        txtName = new JTextField();
        txtDescription = new JTextField();
        txtCategoryId = new JTextField();
        txtAuthorId = new JTextField();
        txtPrice = new JTextField();
        txtStatus = new JTextField();

        formPanel.add(new JLabel("Name:"));
        formPanel.add(txtName);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(txtDescription);
        formPanel.add(new JLabel("Category ID:"));
        formPanel.add(txtCategoryId);
        formPanel.add(new JLabel("Author ID:"));
        formPanel.add(txtAuthorId);
        formPanel.add(new JLabel("Price/Value:"));
        formPanel.add(txtPrice);
        formPanel.add(new JLabel("Status:"));
        formPanel.add(txtStatus);

        // ADDED: Create a container panel for search and form
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(searchPanel, BorderLayout.NORTH);
        topPanel.add(formPanel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);

        // ===== BUTTON PANEL =====
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 248, 255)); // same as background

        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        refreshButton = new JButton("Refresh");
        exitButton = new JButton("Exit");

        // 🎨 Set button colors
        addButton.setBackground(new Color(144, 238, 144));     // light green
        updateButton.setBackground(new Color(255, 255, 102));  // yellow
        deleteButton.setBackground(new Color(255, 99, 71));    // tomato red
        refreshButton.setBackground(new Color(173, 216, 230)); // sky blue
        exitButton.setBackground(new Color(211, 211, 211));    // light gray

        // Add soft borders & font
        JButton[] buttons = {addButton, updateButton, deleteButton, refreshButton, exitButton};
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setFocusPainted(false);
            buttons[i].setFont(new Font("Segoe UI", Font.BOLD, 14));
            buttons[i].setPreferredSize(new Dimension(110, 35));
            buttonPanel.add(buttons[i]);
        }

        add(buttonPanel, BorderLayout.SOUTH);

        // ===== EVENT HANDLERS =====
        
        // ADDED: Search functionality
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                performSearch();
            }
        });

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addArticle();
            }
        });
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateArticle();
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteArticle();
            }
        });
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadArticles();
                searchField.setText(""); // ADDED: Clear search on refresh
                rowSorter.setRowFilter(null); // ADDED: Reset filter
            }
        });
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    // ADDED: Convert view row to model row for sorted table
                    int modelRow = table.convertRowIndexToModel(row);
                    txtName.setText(model.getValueAt(modelRow, 1).toString());
                    txtDescription.setText(model.getValueAt(modelRow, 2).toString());
                    txtCategoryId.setText(model.getValueAt(modelRow, 3).toString());
                    Object authorObj = model.getValueAt(modelRow, 4);
                    txtAuthorId.setText(authorObj != null ? authorObj.toString() : "");
                    txtPrice.setText(model.getValueAt(modelRow, 5).toString());
                    txtStatus.setText(model.getValueAt(modelRow, 6).toString());
                }
            }
        });

        // Load data initially
        loadArticles();
        setVisible(true);
    }

    // ADDED: Search functionality method
    private void performSearch() {
        String searchText = searchField.getText().trim();
        
        if (searchText.length() == 0) {
            rowSorter.setRowFilter(null);
        } else {
            try {
                // Search in Name (column 1), Description (column 2), Price/Value (column 5), and Status (column 6)
                RowFilter<DefaultTableModel, Object> rf = RowFilter.regexFilter("(?i)" + searchText, 1, 2, 5, 6);
                rowSorter.setRowFilter(rf);
            } catch (java.util.regex.PatternSyntaxException e) {
                return; // If invalid regex, do nothing
            }
        }
    }

    private void loadArticles() {
        model.setRowCount(0);
        String sql = "SELECT * FROM articles";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PASS);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("article_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getInt("category_id"),
                        rs.getObject("author_id"),
                        rs.getString("price_or_value"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading articles: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }

    private void addArticle() {
        try {
            String name = txtName.getText();
            String desc = txtDescription.getText();
            int category = Integer.parseInt(txtCategoryId.getText());
            String authorText = txtAuthorId.getText();
            Integer author = authorText.isEmpty() ? null : Integer.parseInt(authorText);
            String price = txtPrice.getText();
            String status = txtStatus.getText();

            String sql = "INSERT INTO articles (name, description, category_id, author_id, price_or_value, status) VALUES (?, ?, ?, ?, ?, ?)";
            Connection conn = DriverManager.getConnection(URL, USER, PASS);
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, desc);
            ps.setInt(3, category);
            if (author == null) ps.setNull(4, Types.INTEGER); else ps.setInt(4, author);
            ps.setString(5, price);
            ps.setString(6, status);
            ps.executeUpdate();
            ps.close();
            conn.close();
            JOptionPane.showMessageDialog(this, "Article added successfully!");
            loadArticles();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Category ID and Author ID must be numbers.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding article: " + ex.getMessage());
        }
    }

    private void updateArticle() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an article to update.");
            return;
        }
        try {
            // ADDED: Convert view row to model row for sorted table
            int modelRow = table.convertRowIndexToModel(row);
            int id = Integer.parseInt(model.getValueAt(modelRow, 0).toString());
            String name = txtName.getText();
            String desc = txtDescription.getText();
            int category = Integer.parseInt(txtCategoryId.getText());
            String authorText = txtAuthorId.getText();
            Integer author = authorText.isEmpty() ? null : Integer.parseInt(authorText);
            String price = txtPrice.getText();
            String status = txtStatus.getText();

            String sql = "UPDATE articles SET name=?, description=?, category_id=?, author_id=?, price_or_value=?, status=? WHERE article_id=?";
            Connection conn = DriverManager.getConnection(URL, USER, PASS);
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, desc);
            ps.setInt(3, category);
            if (author == null) ps.setNull(4, Types.INTEGER); else ps.setInt(4, author);
            ps.setString(5, price);
            ps.setString(6, status);
            ps.setInt(7, id);
            ps.executeUpdate();
            ps.close();
            conn.close();
            JOptionPane.showMessageDialog(this, "Article updated successfully!");
            loadArticles();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Category ID and Author ID must be numbers.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating article: " + ex.getMessage());
        }
    }

    private void deleteArticle() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an article to delete.");
            return;
        }
        // ADDED: Convert view row to model row for sorted table
        int modelRow = table.convertRowIndexToModel(row);
        int id = Integer.parseInt(model.getValueAt(modelRow, 0).toString());
        try {
            String sql = "DELETE FROM articles WHERE article_id=?";
            Connection conn = DriverManager.getConnection(URL, USER, PASS);
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();
            conn.close();
            JOptionPane.showMessageDialog(this, "Article deleted successfully!");
            loadArticles();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting article: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ArticlePanel();
            }
        });
    }
}