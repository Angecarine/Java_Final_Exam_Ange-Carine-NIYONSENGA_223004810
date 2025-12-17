package com.pannel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import com.util.DBConnection;

public class ViewerArticlePanel extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JButton refreshButton, closeButton;

    public ViewerArticlePanel() {
        setTitle("View Articles");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("All Published Articles", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(70, 130, 180));
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"ID", "Name", "Description", "Category ID", "Author ID", "Value", "Status", "Created At"}, 0);
        table = new JTable(model);
        table.setRowHeight(24);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(70, 130, 180));
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        refreshButton = new JButton("Refresh");
        closeButton = new JButton("Close");
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        loadArticles();

        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                loadArticles();
            }
        });

        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });

        setVisible(true);
    }

    private void loadArticles() {
        model.setRowCount(0);
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT * FROM articles ORDER BY created_at DESC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("article_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getInt("category_id"),
                        rs.getInt("author_id"),
                        rs.getDouble("price_or_value"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at")
                });
            }

            rs.close();
            stmt.close();
            conn.close();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading articles: " + ex.getMessage());
        }
    }
}
