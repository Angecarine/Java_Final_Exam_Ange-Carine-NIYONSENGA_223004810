package com.pannel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AdvertisementPanel extends JFrame {

    private JTable adTable;
    private DefaultTableModel adModel;
    private JTextField txtTitle, txtDescription, txtPrice, txtCreatedBy;
    private JButton addButton, updateButton, deleteButton, refreshButton, exitButton;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> rowSorter;

    private static final String URL = "jdbc:mysql://localhost:3306/media_monitoring";
    private static final String USER = "root";
    private static final String PASS = "";

    public AdvertisementPanel() {
        setTitle("Advertisement Management");
        setSize(1000, 600); // Increased height for search panel
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // === SEARCH PANEL - ADDED ===
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel searchLabel = new JLabel("Search:");
        searchField = new JTextField();
        
        JButton searchButton = new JButton("Search");
        
        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        // === Table ===
        adModel = new DefaultTableModel(new String[]{
                "Ad ID", "User ID", "Title", "Description", "Price",
                "Comment ID", "Created At", "Linked Comment ID", "Created By"
        }, 0);

        adTable = new JTable(adModel);
        adTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        adTable.setRowHeight(22);
        
        // ADDED: Row sorter for search
        rowSorter = new TableRowSorter<>(adModel);
        adTable.setRowSorter(rowSorter);
        
        JScrollPane scrollPane = new JScrollPane(adTable);
        add(scrollPane, BorderLayout.CENTER);

        // === Form Panel ===
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Advertisement Details"));

        txtTitle = new JTextField();
        txtDescription = new JTextField();
        txtPrice = new JTextField();
        txtCreatedBy = new JTextField();

        formPanel.add(new JLabel("Title:"));
        formPanel.add(txtTitle);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(txtDescription);
        formPanel.add(new JLabel("Price:"));
        formPanel.add(txtPrice);
        formPanel.add(new JLabel("Created By (User ID):"));
        formPanel.add(txtCreatedBy);

        add(formPanel, BorderLayout.WEST);

        // === Buttons Panel ===
        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        refreshButton = new JButton("Refresh");
        exitButton = new JButton("Exit");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(exitButton);

        // ADDED: Main container for top panels
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(searchPanel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // === Load Data ===
        loadAdvertisements();

        // === SEARCH FUNCTIONALITY - ADDED ===
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

        // === Table Row Click ===
        adTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = adTable.getSelectedRow();
                if (row >= 0) {
                    // Convert view row to model row for sorted table
                    int modelRow = adTable.convertRowIndexToModel(row);
                    txtTitle.setText(adModel.getValueAt(modelRow, 2).toString());
                    txtDescription.setText(adModel.getValueAt(modelRow, 3).toString());
                    txtPrice.setText(adModel.getValueAt(modelRow, 4).toString());
                    txtCreatedBy.setText(adModel.getValueAt(modelRow, 8).toString());
                }
            }
        });

        // === Button Actions ===
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { addAdvertisement(); }
        });
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { updateAdvertisement(); }
        });
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { deleteAdvertisement(); }
        });
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { 
                loadAdvertisements(); 
                searchField.setText(""); // Clear search on refresh
                rowSorter.setRowFilter(null); // Reset filter
            }
        });
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { dispose(); }
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
                // Search in Title (column 2), Description (column 3), and Price (column 4)
                RowFilter<DefaultTableModel, Object> rf = RowFilter.regexFilter("(?i)" + searchText, 2, 3, 4);
                rowSorter.setRowFilter(rf);
            } catch (java.util.regex.PatternSyntaxException e) {
                return; // If invalid regex, do nothing
            }
        }
    }

    private void loadAdvertisements() {
        adModel.setRowCount(0);
        String sql = "SELECT * FROM advertisements";
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASS);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                adModel.addRow(new Object[]{
                        rs.getInt("ad_id"),
                        rs.getInt("user_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getObject("comment_id"),
                        rs.getTimestamp("created_at"),
                        rs.getObject("linked_comment_id"),
                        rs.getInt("created_by_user_id")
                });
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading advertisements: " + e.getMessage());
        }
    }

    private void addAdvertisement() {
        if (txtTitle.getText().isEmpty() || txtPrice.getText().isEmpty() || txtCreatedBy.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill Title, Price, and Created By fields!");
            return;
        }
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASS);
            String sql = "INSERT INTO advertisements(user_id, title, description, price, created_by_user_id) VALUES(?,?,?,?,?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, Integer.parseInt(txtCreatedBy.getText()));
            pst.setString(2, txtTitle.getText());
            pst.setString(3, txtDescription.getText());
            pst.setDouble(4, Double.parseDouble(txtPrice.getText()));
            pst.setInt(5, Integer.parseInt(txtCreatedBy.getText()));
            pst.executeUpdate();
            pst.close();
            conn.close();
            JOptionPane.showMessageDialog(this, "Advertisement added!");
            loadAdvertisements();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void updateAdvertisement() {
        int row = adTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this,"Select a row to update!"); return; }
        int adId = (Integer) adModel.getValueAt(adTable.convertRowIndexToModel(row),0);
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASS);
            String sql = "UPDATE advertisements SET title=?, description=?, price=? WHERE ad_id=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, txtTitle.getText());
            pst.setString(2, txtDescription.getText());
            pst.setDouble(3, Double.parseDouble(txtPrice.getText()));
            pst.setInt(4, adId);
            pst.executeUpdate();
            pst.close();
            conn.close();
            JOptionPane.showMessageDialog(this, "Advertisement updated!");
            loadAdvertisements();
        } catch (Exception e) { JOptionPane.showMessageDialog(this,"Error: "+e.getMessage()); }
    }

    private void deleteAdvertisement() {
        int row = adTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this,"Select a row to delete!"); return; }
        int adId = (Integer) adModel.getValueAt(adTable.convertRowIndexToModel(row),0);
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASS);
            String sql = "DELETE FROM advertisements WHERE ad_id=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, adId);
            pst.executeUpdate();
            pst.close();
            conn.close();
            JOptionPane.showMessageDialog(this,"Advertisement deleted!");
            loadAdvertisements();
        } catch(Exception e){ JOptionPane.showMessageDialog(this,"Error: "+e.getMessage()); }
    }

    public static void main(String[] args) {
        new AdvertisementPanel();
    }
}