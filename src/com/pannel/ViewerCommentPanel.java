package com.pannel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import com.util.DBConnection;

public class ViewerCommentPanel extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JButton refreshButton, closeButton;

    public ViewerCommentPanel() {
        setTitle("View Comments");
        setSize(800, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("All Comments", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(100, 149, 237));
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"Comment ID", "Article ID", "User ID", "Content", "Created At"}, 0);
        table = new JTable(model);
        table.setRowHeight(23);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(100, 149, 237));
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        refreshButton = new JButton("Refresh");
        closeButton = new JButton("Close");
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        loadComments();

        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                loadComments();
            }
        });

        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });

        setVisible(true);
    }

    private void loadComments() {
        model.setRowCount(0);
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT * FROM comments ORDER BY created_at DESC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("comment_id"),
                        rs.getInt("article_id"),
                        rs.getInt("user_id"),
                        rs.getString("content"),
                        rs.getTimestamp("created_at")
                });
            }

            rs.close();
            stmt.close();
            conn.close();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading comments: " + ex.getMessage());
        }
    }
}

