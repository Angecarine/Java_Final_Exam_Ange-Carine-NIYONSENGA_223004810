package com.pannel;

import com.util.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class TagPanel extends JFrame implements ActionListener {

    private JTable table;
    private DefaultTableModel model;
    private JButton addButton, refreshButton;
    private JTextField searchField; // ADDED: Search field
    private TableRowSorter<DefaultTableModel> rowSorter; // ADDED: Row sorter for search
    
    public TagPanel() {
        setTitle("Tags Management");
        setSize(800, 550); // Increased height to accommodate search
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(245, 245, 250));
        setLayout(new BorderLayout(10, 10));
        
        // ===== SEARCH PANEL - ADDED =====
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBackground(new Color(245, 245, 250));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        searchField = new JTextField();
        
        JButton searchButton = createStyledButton("Search", new Color(52, 152, 219), new Color(41, 128, 185));
        
        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        // ===== Table =====
        model = new DefaultTableModel();
        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(22);
        table.setFillsViewportHeight(true);

        model.addColumn("Tag ID");
        model.addColumn("Name");
        model.addColumn("Description");
        model.addColumn("Category");
        model.addColumn("Created At");
        
        // ADDED: Row sorter for search functionality
        rowSorter = new TableRowSorter<>(model);
        table.setRowSorter(rowSorter);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(750, 250));
        add(scrollPane, BorderLayout.CENTER);

        // ===== Buttons =====
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(245, 245, 250));

        addButton = createStyledButton("Add Tag", new Color(46, 204, 113), new Color(39, 174, 96));
        refreshButton = createStyledButton("Refresh", new Color(241, 196, 15), new Color(243, 156, 18));

        addButton.addActionListener(this);
        refreshButton.addActionListener(this);
        
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

        buttonPanel.add(addButton);
        buttonPanel.add(refreshButton);
        
        // ADDED: Create a container for search and main content
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(searchPanel, BorderLayout.NORTH);
        topContainer.add(buttonPanel, BorderLayout.CENTER);
        
        add(topContainer, BorderLayout.SOUTH);

        // Load data initially
        loadTags();
        setVisible(true);
    }

    // ADDED: Search functionality method
    private void performSearch() {
        String searchText = searchField.getText().trim();
        
        if (searchText.length() == 0) {
            rowSorter.setRowFilter(null);
        } else {
            try {
                // Search in Name (column 1), Description (column 2), and Category (column 3)
                RowFilter<DefaultTableModel, Object> rf = RowFilter.regexFilter("(?i)" + searchText, 1, 2, 3);
                rowSorter.setRowFilter(rf);
            } catch (java.util.regex.PatternSyntaxException e) {
                return; // If invalid regex, do nothing
            }
        }
    }

    // ===== Load Tags =====
    private void loadTags() {
        model.setRowCount(0);
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM tags ORDER BY created_at DESC";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Vector<Object> row = new Vector<Object>();
                row.add(rs.getInt("tag_id"));
                row.add(rs.getString("name"));
                row.add(rs.getString("description"));
                row.add(rs.getString("category"));
                row.add(rs.getDate("created_at"));
                model.addRow(row);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }

    // ===== Styled Button =====
    private JButton createStyledButton(final String text, final Color bgColor, final Color hoverColor) {
        final JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { button.setBackground(hoverColor); }
            public void mouseExited(MouseEvent e) { button.setBackground(bgColor); }
        });
        return button;
    }

    // ===== Button Actions =====
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == refreshButton) {
            loadTags();
            searchField.setText(""); // ADDED: Clear search on refresh
            rowSorter.setRowFilter(null); // ADDED: Reset filter
        } else if (e.getSource() == addButton) {
            String name = JOptionPane.showInputDialog(this, "Enter Tag Name:");
            if (name == null || name.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name is required!");
                return;
            }
            String description = JOptionPane.showInputDialog(this, "Enter Description:");
            String category = JOptionPane.showInputDialog(this, "Enter Category:");

            Connection conn = null;
            PreparedStatement stmt = null;
            try {
                conn = DBConnection.getConnection();
                String sql = "INSERT INTO tags (name, description, category, created_at) VALUES (?, ?, ?, NOW())";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, name);
                stmt.setString(2, description != null ? description : null);
                stmt.setString(3, category != null ? category : null);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "✅ Tag added successfully!");
                loadTags(); // Refresh table immediately

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
            } finally {
                try { if (stmt != null) stmt.close(); } catch (Exception ex) {}
                try { if (conn != null) conn.close(); } catch (Exception ex) {}
            }
        }
    }

    // ===== MAIN =====
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TagPanel();
            }
        });
    }
}