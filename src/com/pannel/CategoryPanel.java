package com.pannel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class CategoryPanel extends JFrame implements ActionListener {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtAttribute1, txtAttribute2, txtAttribute3;
    private JButton addButton, updateButton, deleteButton, refreshButton, exitButton;
    private JTextField searchField; // ADDED: Search field
    private TableRowSorter<DefaultTableModel> rowSorter; // ADDED: Row sorter for search

    private static final String URL = "jdbc:mysql://localhost:3306/media_monitoring";
    private static final String USER = "root";
    private static final String PASS = "";

    public CategoryPanel() {
        setTitle("Manage Categories");
        setSize(800, 550); // Increased height to accommodate search
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // ======= STYLING THE BACKGROUND =======
        getContentPane().setBackground(new Color(240, 248, 255)); // Soft blue-white background

        // ======= SEARCH PANEL - ADDED =======
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBackground(new Color(240, 248, 255));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        searchField = new JTextField();
        
        JButton searchButton = new JButton("Search");
        searchButton.setBackground(new Color(0, 102, 204));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        searchButton.setFocusPainted(false);
        searchButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        // ======= TABLE =======
        model = new DefaultTableModel(new String[]{"ID", "Attribute1", "Attribute2", "Attribute3", "Created At"}, 0);
        table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(51, 153, 255));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(173, 216, 230));
        
        // ADDED: Row sorter for search functionality
        rowSorter = new TableRowSorter<>(model);
        table.setRowSorter(rowSorter);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 2), 
                "Category Records", 0, 0, new Font("SansSerif", Font.BOLD, 14), new Color(0, 102, 204)));
        add(scrollPane, BorderLayout.CENTER);

        // ======= FORM PANEL =======
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBackground(new Color(230, 240, 255));
        formPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 2),
                "Category Details", 0, 0, new Font("SansSerif", Font.BOLD, 14), new Color(0, 102, 204)));

        JLabel lbl1 = new JLabel("Attribute 1:");
        JLabel lbl2 = new JLabel("Attribute 2:");
        JLabel lbl3 = new JLabel("Attribute 3:");

        lbl1.setFont(new Font("SansSerif", Font.BOLD, 14));
        lbl2.setFont(new Font("SansSerif", Font.BOLD, 14));
        lbl3.setFont(new Font("SansSerif", Font.BOLD, 14));

        txtAttribute1 = new JTextField();
        txtAttribute2 = new JTextField();
        txtAttribute3 = new JTextField();

        formPanel.add(lbl1);
        formPanel.add(txtAttribute1);
        formPanel.add(lbl2);
        formPanel.add(txtAttribute2);
        formPanel.add(lbl3);
        formPanel.add(txtAttribute3);

        // ADDED: Create a container for search and form panels
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(searchPanel, BorderLayout.NORTH);
        topContainer.add(formPanel, BorderLayout.CENTER);
        
        add(topContainer, BorderLayout.NORTH);

        // ======= BUTTON PANEL =======
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 248, 255));

        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        refreshButton = new JButton("Refresh");
        exitButton = new JButton("Exit");

        JButton[] buttons = {addButton, updateButton, deleteButton, refreshButton, exitButton};
        Color[] colors = {
                new Color(0, 153, 76),   // Add - green
                new Color(255, 140, 0),  // Update - orange
                new Color(220, 20, 60),  // Delete - red
                new Color(0, 102, 204),  // Refresh - blue
                new Color(128, 0, 128)   // Exit - purple
        };

        for (int i = 0; i < buttons.length; i++) {
            final JButton b = buttons[i];
            b.setBackground(colors[i]);
            b.setForeground(Color.WHITE);
            b.setFont(new Font("SansSerif", Font.BOLD, 13));
            b.setFocusPainted(false);
            b.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

            // Button hover effect
            final Color normalColor = colors[i];
            b.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    b.setBackground(normalColor.darker());
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    b.setBackground(normalColor);
                }
            });
            buttonPanel.add(b);
        }

        add(buttonPanel, BorderLayout.SOUTH);

        // ======= BUTTON ACTIONS =======
        addButton.addActionListener(this);
        updateButton.addActionListener(this);
        deleteButton.addActionListener(this);
        refreshButton.addActionListener(this);
        exitButton.addActionListener(this);

        // ADDED: Search button action
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });

        // ADDED: Real-time search as you type
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                performSearch();
            }
        });

        // ======= LOAD DATA INITIALLY =======
        loadCategories();

        // ======= TABLE ROW CLICK =======
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    // ADDED: Convert view row to model row for sorted table
                    int modelRow = table.convertRowIndexToModel(row);
                    txtAttribute1.setText(model.getValueAt(modelRow, 1).toString());
                    txtAttribute2.setText(model.getValueAt(modelRow, 2).toString());
                    txtAttribute3.setText(model.getValueAt(modelRow, 3).toString());
                }
            }
        });

        setVisible(true);
    }

    // ADDED: Search functionality method
    private void performSearch() {
        String searchText = searchField.getText().trim();
        
        if (searchText.length() == 0) {
            rowSorter.setRowFilter(null);
        } else {
            try {
                // Search in Attribute1 (column 1), Attribute2 (column 2), and Attribute3 (column 3)
                RowFilter<DefaultTableModel, Object> rf = RowFilter.regexFilter("(?i)" + searchText, 1, 2, 3);
                rowSorter.setRowFilter(rf);
            } catch (java.util.regex.PatternSyntaxException e) {
                return; // If invalid regex, do nothing
            }
        }
    }

    // ================= LOGIC (UNCHANGED) ================= //
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) addCategory();
        else if (e.getSource() == updateButton) updateCategory();
        else if (e.getSource() == deleteButton) deleteCategory();
        else if (e.getSource() == refreshButton) {
            loadCategories();
            searchField.setText(""); // ADDED: Clear search on refresh
            rowSorter.setRowFilter(null); // ADDED: Reset filter
        }
        else if (e.getSource() == exitButton) dispose();
    }

    private void loadCategories() {
        model.setRowCount(0);
        String sql = "SELECT * FROM categories";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("category_id"),
                        rs.getString("attribute1"),
                        rs.getString("attribute2"),
                        rs.getString("attribute3"),
                        rs.getTimestamp("created_at")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading categories: " + ex.getMessage());
        }
    }

    private void addCategory() {
        String a1 = txtAttribute1.getText().trim();
        String a2 = txtAttribute2.getText().trim();
        String a3 = txtAttribute3.getText().trim();

        if (a1.isEmpty() || a2.isEmpty() || a3.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fill all fields!");
            return;
        }

        String sql = "INSERT INTO categories(attribute1, attribute2, attribute3, created_at) VALUES (?, ?, ?, NOW())";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a1);
            ps.setString(2, a2);
            ps.setString(3, a3);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Category added!");
            clearFields();
            loadCategories();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding category: " + ex.getMessage());
        }
    }

    private void updateCategory() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a category to update!");
            return;
        }

        // ADDED: Convert view row to model row for sorted table
        int modelRow = table.convertRowIndexToModel(selectedRow);
        int id = (int) model.getValueAt(modelRow, 0);
        String a1 = txtAttribute1.getText().trim();
        String a2 = txtAttribute2.getText().trim();
        String a3 = txtAttribute3.getText().trim();

        String sql = "UPDATE categories SET attribute1=?, attribute2=?, attribute3=? WHERE category_id=?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a1);
            ps.setString(2, a2);
            ps.setString(3, a3);
            ps.setInt(4, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Category updated!");
            clearFields();
            loadCategories();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating category: " + ex.getMessage());
        }
    }

    private void deleteCategory() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a category to delete!");
            return;
        }

        // ADDED: Convert view row to model row for sorted table
        int modelRow = table.convertRowIndexToModel(selectedRow);
        int id = (int) model.getValueAt(modelRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this category?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        String sql = "DELETE FROM categories WHERE category_id=?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Category deleted!");
            clearFields();
            loadCategories();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting category: " + ex.getMessage());
        }
    }

    private void clearFields() {
        txtAttribute1.setText("");
        txtAttribute2.setText("");
        txtAttribute3.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CategoryPanel();
            }
        });
    }
}