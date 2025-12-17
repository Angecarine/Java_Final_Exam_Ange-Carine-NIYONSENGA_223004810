package com.pannel;

import com.util.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class CommentPanel extends JFrame {

    private JTextField txtCommentId, txtArticleId, txtUserId, txtCreatedAt;
    private JTextArea txtContent;
    private JButton addButton, updateButton, deleteButton, refreshButton, exitButton;
    private JTable commentTable;
    private DefaultTableModel tableModel;
    private JTextField searchField; // ADDED: Search field
    private TableRowSorter<DefaultTableModel> rowSorter; // ADDED: Row sorter for search

    public CommentPanel() {
        setTitle("Manage Comments");
        setSize(800, 650); // Increased height to accommodate search
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // ===== SEARCH PANEL - ADDED =====
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel searchLabel = new JLabel("Search:");
        searchField = new JTextField();
        
        JButton searchButton = new JButton("Search");
        
        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        // ===== Table =====
        tableModel = new DefaultTableModel();
        commentTable = new JTable(tableModel);
        
        // ADDED: Row sorter for search functionality
        rowSorter = new TableRowSorter<>(tableModel);
        commentTable.setRowSorter(rowSorter);
        
        JScrollPane scroll = new JScrollPane(commentTable);
        add(scroll, BorderLayout.CENTER);

        tableModel.setColumnIdentifiers(new Object[]{"Comment ID", "Article ID", "User ID", "Content", "Created At"});

        // ===== Form Panel =====
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        txtCommentId = new JTextField();
        txtArticleId = new JTextField();
        txtUserId = new JTextField();
        txtCreatedAt = new JTextField();
        txtContent = new JTextArea();
        JScrollPane contentScroll = new JScrollPane(txtContent);

        formPanel.add(new JLabel("Comment ID:"));
        formPanel.add(txtCommentId);
        formPanel.add(new JLabel("Article ID:"));
        formPanel.add(txtArticleId);
        formPanel.add(new JLabel("User ID:"));
        formPanel.add(txtUserId);
        formPanel.add(new JLabel("Created At (yyyy-MM-dd HH:mm:ss):"));
        formPanel.add(txtCreatedAt);
        formPanel.add(new JLabel("Content:"));
        formPanel.add(contentScroll);

        // ADDED: Create a container for search and form panels
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(searchPanel, BorderLayout.NORTH);
        topContainer.add(formPanel, BorderLayout.CENTER);
        
        add(topContainer, BorderLayout.NORTH);

        // ===== Buttons =====
        JPanel btnPanel = new JPanel();
        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        refreshButton = new JButton("Refresh");
        exitButton = new JButton("Exit");

        btnPanel.add(addButton);
        btnPanel.add(updateButton);
        btnPanel.add(deleteButton);
        btnPanel.add(refreshButton);
        btnPanel.add(exitButton);
        add(btnPanel, BorderLayout.SOUTH);

        // ===== Button Actions =====
        
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
            public void actionPerformed(ActionEvent e) { addComment(); }
        });
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { updateComment(); }
        });
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { deleteComment(); }
        });
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { 
                loadComments(); 
                searchField.setText(""); // ADDED: Clear search on refresh
                rowSorter.setRowFilter(null); // ADDED: Reset filter
            }
        });
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { dispose(); }
        });

        commentTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = commentTable.getSelectedRow();
                if (row >= 0) {
                    // ADDED: Convert view row to model row for sorted table
                    int modelRow = commentTable.convertRowIndexToModel(row);
                    txtCommentId.setText(tableModel.getValueAt(modelRow, 0).toString());
                    txtArticleId.setText(tableModel.getValueAt(modelRow, 1).toString());
                    txtUserId.setText(tableModel.getValueAt(modelRow, 2).toString());
                    txtContent.setText(tableModel.getValueAt(modelRow, 3).toString());
                    txtCreatedAt.setText(tableModel.getValueAt(modelRow, 4).toString());
                }
            }
        });

        loadComments();
        setVisible(true);
    }

    // ADDED: Search functionality method
    private void performSearch() {
        String searchText = searchField.getText().trim();
        
        if (searchText.length() == 0) {
            rowSorter.setRowFilter(null);
        } else {
            try {
                // Search in Content (column 3) and Created At (column 4)
                RowFilter<DefaultTableModel, Object> rf = RowFilter.regexFilter("(?i)" + searchText, 3, 4);
                rowSorter.setRowFilter(rf);
            } catch (java.util.regex.PatternSyntaxException e) {
                return; // If invalid regex, do nothing
            }
        }
    }

    private void loadComments() {
        tableModel.setRowCount(0);
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM comments ORDER BY created_at DESC");
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("comment_id"),
                        rs.getInt("article_id"),
                        rs.getInt("user_id"),
                        rs.getString("content"),
                        rs.getString("created_at")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading comments: " + ex.getMessage());
        } finally {
            try { if(rs != null) rs.close(); } catch(Exception e){}
            try { if(stmt != null) stmt.close(); } catch(Exception e){}
            try { if(conn != null) conn.close(); } catch(Exception e){}
        }
    }

    private void addComment() {
        if (txtCommentId.getText().isEmpty() || txtArticleId.getText().isEmpty() || txtUserId.getText().isEmpty() || txtContent.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!");
            return;
        }
        Connection conn = null;
        PreparedStatement pst = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO comments(comment_id, article_id, user_id, content, created_at) VALUES(?,?,?,?,?)";
            pst = conn.prepareStatement(sql);
            pst.setInt(1, Integer.parseInt(txtCommentId.getText()));
            pst.setInt(2, Integer.parseInt(txtArticleId.getText()));
            pst.setInt(3, Integer.parseInt(txtUserId.getText()));
            pst.setString(4, txtContent.getText());
            pst.setString(5, txtCreatedAt.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Comment added!");
            loadComments();
        } catch(Exception ex){ JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage()); }
        finally { try{ if(pst!=null)pst.close(); if(conn!=null)conn.close(); } catch(Exception e){} }
    }

    private void updateComment() {
        if (txtCommentId.getText().isEmpty()) { JOptionPane.showMessageDialog(this,"Select a comment to update!"); return; }
        Connection conn = null;
        PreparedStatement pst = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE comments SET article_id=?, user_id=?, content=?, created_at=? WHERE comment_id=?";
            pst = conn.prepareStatement(sql);
            pst.setInt(1, Integer.parseInt(txtArticleId.getText()));
            pst.setInt(2, Integer.parseInt(txtUserId.getText()));
            pst.setString(3, txtContent.getText());
            pst.setString(4, txtCreatedAt.getText());
            pst.setInt(5, Integer.parseInt(txtCommentId.getText()));
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Comment updated!");
            loadComments();
        } catch(Exception ex){ JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage()); }
        finally { try{ if(pst!=null)pst.close(); if(conn!=null)conn.close(); } catch(Exception e){} }
    }

    private void deleteComment() {
        if (txtCommentId.getText().isEmpty()) { JOptionPane.showMessageDialog(this,"Select a comment to delete!"); return; }
        Connection conn = null;
        PreparedStatement pst = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "DELETE FROM comments WHERE comment_id=?";
            pst = conn.prepareStatement(sql);
            pst.setInt(1, Integer.parseInt(txtCommentId.getText()));
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Comment deleted!");
            loadComments();
        } catch(Exception ex){ JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage()); }
        finally { try{ if(pst!=null)pst.close(); if(conn!=null)conn.close(); } catch(Exception e){} }
    }

    public static void main(String[] args){
        new CommentPanel();
    }
}