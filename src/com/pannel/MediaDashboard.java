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

/**
 * MediaDashboard - single-window dashboard that shows all management panels on one scrollable page.
 * Java 1.7 compatible (no lambdas).
 */
public class MediaDashboard extends JFrame {

    public MediaDashboard() {
        setTitle("Media Monitoring System - Dashboard");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main container with vertical stacking
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(250, 250, 250));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add each management panel to mainPanel
        mainPanel.add(new SectionWrapper("Articles Management", new ArticlePanel()));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(new SectionWrapper("Categories Management", new CategoryPanel()));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(new SectionWrapper("Comments Management", new CommentPanel()));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(new SectionWrapper("Advertisements Management", new AdvertisementPanel()));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(new SectionWrapper("Tags Management", new TagPanel()));
        mainPanel.add(Box.createVerticalGlue());

        // Put mainPanel into a scroll pane
        JScrollPane scrollPane = new JScrollPane(mainPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        getContentPane().add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    // Simple wrapper that provides a titled border and consistent padding for each section
    private static class SectionWrapper extends JPanel {
        public SectionWrapper(String title, JPanel inner) {
            setLayout(new BorderLayout());
            setBackground(new Color(245, 245, 250));
            setBorder(BorderFactory.createTitledBorder(title));
            inner.setOpaque(false);
            add(inner, BorderLayout.CENTER);
        }
    }

    // --------------------- Article Panel ---------------------
    public static class ArticlePanel extends JPanel {
        private JTable table;
        private DefaultTableModel model;
        private JTextField txtName, txtDescription, txtCategoryId, txtAuthorId, txtPrice, txtStatus;
        private JButton addButton, updateButton, deleteButton, refreshButton;
        private JTextField searchField; // ADDED: Search field
        private TableRowSorter<DefaultTableModel> rowSorter; // ADDED: Row sorter

        public ArticlePanel() {
            setLayout(new BorderLayout(8, 8));
            setPreferredSize(new Dimension(1050, 350)); // Increased height for search

            // ADDED: Search Panel
            JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
            searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            searchPanel.setOpaque(false);
            
            JLabel searchLabel = new JLabel("Search:");
            searchField = new JTextField();
            JButton searchButton = styledButton("Search");
            
            searchPanel.add(searchLabel, BorderLayout.WEST);
            searchPanel.add(searchField, BorderLayout.CENTER);
            searchPanel.add(searchButton, BorderLayout.EAST);

            model = new DefaultTableModel(new String[]{
                    "Article ID", "Name", "Description", "Category ID", "Author ID", "Price/Value", "Status", "Created At"
            }, 0);
            table = new JTable(model);
            table.setFillsViewportHeight(true);
            
            // ADDED: Row sorter for search
            rowSorter = new TableRowSorter<>(model);
            table.setRowSorter(rowSorter);

            JScrollPane scrollPane = new JScrollPane(table);
            add(scrollPane, BorderLayout.CENTER);

            JPanel formPanel = new JPanel(new GridLayout(2, 6, 8, 8));
            formPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

            txtName = new JTextField();
            txtDescription = new JTextField();
            txtCategoryId = new JTextField();
            txtAuthorId = new JTextField();
            txtPrice = new JTextField();
            txtStatus = new JTextField();

            formPanel.add(labeled("Name:", txtName));
            formPanel.add(labeled("Description:", txtDescription));
            formPanel.add(labeled("Category ID:", txtCategoryId));
            formPanel.add(labeled("Author ID:", txtAuthorId));
            formPanel.add(labeled("Price/Value:", txtPrice));
            formPanel.add(labeled("Status:", txtStatus));

            // ADDED: Container for search and form
            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.add(searchPanel, BorderLayout.NORTH);
            topPanel.add(formPanel, BorderLayout.CENTER);
            add(topPanel, BorderLayout.NORTH);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
            addButton = styledButton("Add");
            updateButton = styledButton("Update");
            deleteButton = styledButton("Delete");
            refreshButton = styledButton("Refresh");

            buttonPanel.add(addButton);
            buttonPanel.add(updateButton);
            buttonPanel.add(deleteButton);
            buttonPanel.add(refreshButton);

            add(buttonPanel, BorderLayout.SOUTH);

            loadArticles();

            // ADDED: Search functionality
            searchButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    performArticleSearch();
                }
            });

            searchField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    performArticleSearch();
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

            table.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        // ADDED: Convert view row to model row for sorted table
                        int modelRow = table.convertRowIndexToModel(row);
                        txtName.setText(String.valueOf(model.getValueAt(modelRow, 1)));
                        txtDescription.setText(String.valueOf(model.getValueAt(modelRow, 2)));
                        txtCategoryId.setText(String.valueOf(model.getValueAt(modelRow, 3)));
                        Object au = model.getValueAt(modelRow, 4);
                        txtAuthorId.setText(au == null ? "" : au.toString());
                        txtPrice.setText(String.valueOf(model.getValueAt(modelRow, 5)));
                        txtStatus.setText(String.valueOf(model.getValueAt(modelRow, 6)));
                    }
                }
            });
        }

        // ADDED: Search method for articles
        private void performArticleSearch() {
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

        private JPanel labeled(String label, JComponent comp) {
            JPanel p = new JPanel(new BorderLayout(4,4));
            p.setOpaque(false);
            JLabel l = new JLabel(label);
            l.setPreferredSize(new Dimension(90, 20));
            p.add(l, BorderLayout.WEST);
            p.add(comp, BorderLayout.CENTER);
            return p;
        }

        private JButton styledButton(String text) {
            JButton b = new JButton(text);
            b.setBackground(new Color(52, 152, 219));
            b.setForeground(Color.WHITE);
            b.setFocusPainted(false);
            return b;
        }

        private void loadArticles() {
            model.setRowCount(0);
            String sql = "SELECT * FROM articles ORDER BY created_at DESC";
            Connection conn = null;
            Statement stmt = null;
            ResultSet rs = null;
            try {
                conn = DBConnection.getConnection();
                stmt = conn.createStatement();
                rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("article_id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getObject("category_id"),
                            rs.getObject("author_id"),
                            rs.getString("price_or_value"),
                            rs.getString("status"),
                            rs.getTimestamp("created_at")
                    });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error loading articles: " + ex.getMessage());
            } finally {
                closeQuiet(rs);
                closeQuiet(stmt);
                closeQuiet(conn);
            }
        }

        private void addArticle() {
            String name = txtName.getText().trim();
            if (name.isEmpty()) { JOptionPane.showMessageDialog(this, "Name required"); return; }
            String desc = txtDescription.getText().trim();
            String catText = txtCategoryId.getText().trim();
            String authorText = txtAuthorId.getText().trim();
            String price = txtPrice.getText().trim();
            String status = txtStatus.getText().trim();

            Connection conn = null;
            PreparedStatement pst = null;
            try {
                conn = DBConnection.getConnection();
                String sql = "INSERT INTO articles (name, description, category_id, author_id, price_or_value, status, created_at) VALUES (?, ?, ?, ?, ?, ?, NOW())";
                pst = conn.prepareStatement(sql);
                pst.setString(1, name);
                pst.setString(2, desc);
                if (catText.isEmpty()) pst.setNull(3, Types.INTEGER); else pst.setInt(3, Integer.parseInt(catText));
                if (authorText.isEmpty()) pst.setNull(4, Types.INTEGER); else pst.setInt(4, Integer.parseInt(authorText));
                pst.setString(5, price);
                pst.setString(6, status);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Article added!");
                loadArticles();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error adding article: " + ex.getMessage());
            } finally {
                closeQuiet(pst);
                closeQuiet(conn);
            }
        }

        private void updateArticle() {
            int row = table.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Select an article to update"); return; }
            // ADDED: Convert view row to model row for sorted table
            int modelRow = table.convertRowIndexToModel(row);
            Integer id = (Integer) model.getValueAt(modelRow, 0);
            String name = txtName.getText().trim();
            String desc = txtDescription.getText().trim();
            String catText = txtCategoryId.getText().trim();
            String authorText = txtAuthorId.getText().trim();
            String price = txtPrice.getText().trim();
            String status = txtStatus.getText().trim();

            Connection conn = null;
            PreparedStatement pst = null;
            try {
                conn = DBConnection.getConnection();
                String sql = "UPDATE articles SET name=?, description=?, category_id=?, author_id=?, price_or_value=?, status=? WHERE article_id=?";
                pst = conn.prepareStatement(sql);
                pst.setString(1, name);
                pst.setString(2, desc);
                if (catText.isEmpty()) pst.setNull(3, Types.INTEGER); else pst.setInt(3, Integer.parseInt(catText));
                if (authorText.isEmpty()) pst.setNull(4, Types.INTEGER); else pst.setInt(4, Integer.parseInt(authorText));
                pst.setString(5, price);
                pst.setString(6, status);
                pst.setInt(7, id);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Article updated!");
                loadArticles();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error updating article: " + ex.getMessage());
            } finally {
                closeQuiet(pst);
                closeQuiet(conn);
            }
        }

        private void deleteArticle() {
            int row = table.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Select an article to delete"); return; }
            // ADDED: Convert view row to model row for sorted table
            int modelRow = table.convertRowIndexToModel(row);
            Integer id = (Integer) model.getValueAt(modelRow, 0);
            int conf = JOptionPane.showConfirmDialog(this, "Delete article " + id + " ?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (conf != JOptionPane.YES_OPTION) return;
            Connection conn = null;
            PreparedStatement pst = null;
            try {
                conn = DBConnection.getConnection();
                String sql = "DELETE FROM articles WHERE article_id=?";
                pst = conn.prepareStatement(sql);
                pst.setInt(1, id);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Article deleted!");
                loadArticles();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting article: " + ex.getMessage());
            } finally {
                closeQuiet(pst);
                closeQuiet(conn);
            }
        }
    }

    // --------------------- Category Panel ---------------------
    public static class CategoryPanel extends JPanel {
        private JTable table;
        private DefaultTableModel model;
        private JTextField txtAttr1, txtAttr2, txtAttr3;
        private JButton addButton, updateButton, deleteButton, refreshButton;
        private JTextField searchField; // ADDED: Search field
        private TableRowSorter<DefaultTableModel> rowSorter; // ADDED: Row sorter

        public CategoryPanel() {
            setLayout(new BorderLayout(8,8));
            setPreferredSize(new Dimension(1050, 270)); // Increased height for search

            // ADDED: Search Panel
            JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
            searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            searchPanel.setOpaque(false);
            
            JLabel searchLabel = new JLabel("Search:");
            searchField = new JTextField();
            JButton searchButton = styledButton("Search");
            
            searchPanel.add(searchLabel, BorderLayout.WEST);
            searchPanel.add(searchField, BorderLayout.CENTER);
            searchPanel.add(searchButton, BorderLayout.EAST);

            model = new DefaultTableModel(new String[]{"ID","Attribute1","Attribute2","Attribute3","Created At"},0);
            table = new JTable(model);
            
            // ADDED: Row sorter for search
            rowSorter = new TableRowSorter<>(model);
            table.setRowSorter(rowSorter);
            
            add(new JScrollPane(table), BorderLayout.CENTER);

            JPanel form = new JPanel(new GridLayout(1,6,8,8));
            txtAttr1=new JTextField(); txtAttr2=new JTextField(); txtAttr3=new JTextField();
            form.add(labeled("Attribute1:", txtAttr1));
            form.add(labeled("Attribute2:", txtAttr2));
            form.add(labeled("Attribute3:", txtAttr3));

            // ADDED: Container for search and form
            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.add(searchPanel, BorderLayout.NORTH);
            topPanel.add(form, BorderLayout.CENTER);
            add(topPanel, BorderLayout.NORTH);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,10,6));
            addButton = styledButton("Add");
            updateButton = styledButton("Update");
            deleteButton = styledButton("Delete");
            refreshButton = styledButton("Refresh");
            buttonPanel.add(addButton); buttonPanel.add(updateButton);
            buttonPanel.add(deleteButton); buttonPanel.add(refreshButton);
            add(buttonPanel, BorderLayout.SOUTH);

            loadCategories();

            // ADDED: Search functionality
            searchButton.addActionListener(new ActionListener(){ 
                public void actionPerformed(ActionEvent e){ 
                    performCategorySearch(); 
                }
            });

            searchField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    performCategorySearch();
                }
            });

            addButton.addActionListener(new ActionListener(){ public void actionPerformed(ActionEvent e){ addCategory(); }});
            updateButton.addActionListener(new ActionListener(){ public void actionPerformed(ActionEvent e){ updateCategory(); }});
            deleteButton.addActionListener(new ActionListener(){ public void actionPerformed(ActionEvent e){ deleteCategory(); }});
            refreshButton.addActionListener(new ActionListener(){ 
                public void actionPerformed(ActionEvent e){ 
                    loadCategories(); 
                    searchField.setText(""); // ADDED: Clear search on refresh
                    rowSorter.setRowFilter(null); // ADDED: Reset filter
                }
            });

            table.addMouseListener(new MouseAdapter(){
                public void mouseClicked(MouseEvent e){
                    int r = table.getSelectedRow();
                    if(r>=0){
                        // ADDED: Convert view row to model row for sorted table
                        int modelRow = table.convertRowIndexToModel(r);
                        txtAttr1.setText(String.valueOf(model.getValueAt(modelRow,1)));
                        txtAttr2.setText(String.valueOf(model.getValueAt(modelRow,2)));
                        txtAttr3.setText(String.valueOf(model.getValueAt(modelRow,3)));
                    }
                }
            });
        }

        // ADDED: Search method for categories
        private void performCategorySearch() {
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

        private JPanel labeled(String label, JComponent comp) {
            JPanel p = new JPanel(new BorderLayout(4,4));
            p.setOpaque(false);
            JLabel l = new JLabel(label);
            l.setPreferredSize(new Dimension(90, 20));
            p.add(l, BorderLayout.WEST);
            p.add(comp, BorderLayout.CENTER);
            return p;
        }

        private JButton styledButton(String text) {
            JButton b = new JButton(text);
            b.setBackground(new Color(46, 204, 113));
            b.setForeground(Color.WHITE);
            b.setFocusPainted(false);
            return b;
        }

        private void loadCategories() {
            model.setRowCount(0);
            String sql = "SELECT * FROM categories ORDER BY created_at DESC";
            Connection conn = null;
            Statement st = null;
            ResultSet rs = null;
            try {
                conn = DBConnection.getConnection();
                st = conn.createStatement();
                rs = st.executeQuery(sql);
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("category_id"),
                            rs.getString("attribute1"),
                            rs.getString("attribute2"),
                            rs.getString("attribute3"),
                            rs.getTimestamp("created_at")
                    });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error loading categories: " + ex.getMessage());
            } finally {
                closeQuiet(rs); closeQuiet(st); closeQuiet(conn);
            }
        }

        private void addCategory() {
            String a1 = txtAttr1.getText().trim();
            if (a1.isEmpty()) { JOptionPane.showMessageDialog(this, "Attribute1 required"); return; }
            String a2 = txtAttr2.getText().trim();
            String a3 = txtAttr3.getText().trim();
            Connection conn = null;
            PreparedStatement pst = null;
            try {
                conn = DBConnection.getConnection();
                String sql = "INSERT INTO categories (attribute1, attribute2, attribute3, created_at) VALUES (?, ?, ?, NOW())";
                pst = conn.prepareStatement(sql);
                pst.setString(1, a1);
                pst.setString(2, a2);
                pst.setString(3, a3);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Category added!");
                loadCategories();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error adding category: " + ex.getMessage());
            } finally {
                closeQuiet(pst); closeQuiet(conn);
            }
        }

        private void updateCategory() {
            int r = table.getSelectedRow();
            if (r == -1) { JOptionPane.showMessageDialog(this, "Select a category to update"); return; }
            // ADDED: Convert view row to model row for sorted table
            int modelRow = table.convertRowIndexToModel(r);
            Integer id = (Integer) model.getValueAt(modelRow, 0);
            String a1 = txtAttr1.getText().trim();
            String a2 = txtAttr2.getText().trim();
            String a3 = txtAttr3.getText().trim();
            Connection conn = null;
            PreparedStatement pst = null;
            try {
                conn = DBConnection.getConnection();
                String sql = "UPDATE categories SET attribute1=?, attribute2=?, attribute3=? WHERE category_id=?";
                pst = conn.prepareStatement(sql);
                pst.setString(1, a1);
                pst.setString(2, a2);
                pst.setString(3, a3);
                pst.setInt(4, id);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Category updated!");
                loadCategories();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error updating category: " + ex.getMessage());
            } finally {
                closeQuiet(pst); closeQuiet(conn);
            }
        }

        private void deleteCategory() {
            int r = table.getSelectedRow();
            if (r == -1) { JOptionPane.showMessageDialog(this, "Select a category to delete"); return; }
            // ADDED: Convert view row to model row for sorted table
            int modelRow = table.convertRowIndexToModel(r);
            Integer id = (Integer) model.getValueAt(modelRow,0);
            int c = JOptionPane.showConfirmDialog(this, "Delete category " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (c != JOptionPane.YES_OPTION) return;
            Connection conn = null;
            PreparedStatement pst = null;
            try {
                conn = DBConnection.getConnection();
                String sql = "DELETE FROM categories WHERE category_id=?";
                pst = conn.prepareStatement(sql);
                pst.setInt(1, id);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Category deleted!");
                loadCategories();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting category: " + ex.getMessage());
            } finally {
                closeQuiet(pst); closeQuiet(conn);
            }
        }
    }

    // --------------------- Comment Panel ---------------------
    public static class CommentPanel extends JPanel {
        private JTable table; private DefaultTableModel model;
        private JTextField txtCommentId, txtArticleId, txtUserId, txtCreatedAt;
        private JTextArea txtContent;
        private JButton addButton, updateButton, deleteButton, refreshButton;
        private JTextField searchField; // ADDED: Search field
        private TableRowSorter<DefaultTableModel> rowSorter; // ADDED: Row sorter

        public CommentPanel() {
            setLayout(new BorderLayout(8,8));
            setPreferredSize(new Dimension(1050, 350)); // Increased height for search

            // ADDED: Search Panel
            JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
            searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            searchPanel.setOpaque(false);
            
            JLabel searchLabel = new JLabel("Search:");
            searchField = new JTextField();
            JButton searchButton = new JButton("Search");
            styleButton(searchButton, new Color(52, 152, 219));
            
            searchPanel.add(searchLabel, BorderLayout.WEST);
            searchPanel.add(searchField, BorderLayout.CENTER);
            searchPanel.add(searchButton, BorderLayout.EAST);

            model=new DefaultTableModel(new String[]{"Comment ID","Article ID","User ID","Content","Created At"},0);
            table=new JTable(model);
            
            // ADDED: Row sorter for search
            rowSorter = new TableRowSorter<>(model);
            table.setRowSorter(rowSorter);
            
            add(new JScrollPane(table),BorderLayout.CENTER);

            JPanel form=new JPanel(new GridLayout(2,5,8,8));
            txtCommentId=new JTextField(); txtArticleId=new JTextField(); txtUserId=new JTextField(); txtCreatedAt=new JTextField();
            txtContent=new JTextArea(3,20);

            form.add(labeled("Comment ID:", txtCommentId));
            form.add(labeled("Article ID:", txtArticleId));
            form.add(labeled("User ID:", txtUserId));
            form.add(labeled("Created At:", txtCreatedAt));
            form.add(labeled("Content:", new JScrollPane(txtContent)));

            // ADDED: Container for search and form
            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.add(searchPanel, BorderLayout.NORTH);
            topPanel.add(form, BorderLayout.CENTER);
            add(topPanel, BorderLayout.NORTH);

            JPanel buttonPanel=new JPanel(new FlowLayout(FlowLayout.LEFT,10,6));
            addButton=new JButton("Add"); 
            updateButton=new JButton("Update");
            deleteButton=new JButton("Delete"); 
            refreshButton=new JButton("Refresh");
            styleButton(addButton, new Color(46,204,113));
            styleButton(updateButton, new Color(241,196,15));
            styleButton(deleteButton, new Color(231,76,60));
            styleButton(refreshButton, new Color(52,152,219));
            buttonPanel.add(addButton); 
            buttonPanel.add(updateButton);
            buttonPanel.add(deleteButton); 
            buttonPanel.add(refreshButton);
            add(buttonPanel,BorderLayout.SOUTH);

            loadComments();

            // ADDED: Search functionality
            searchButton.addActionListener(new ActionListener(){ 
                public void actionPerformed(ActionEvent e){ 
                    performCommentSearch(); 
                }
            });

            searchField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    performCommentSearch();
                }
            });

            addButton.addActionListener(new ActionListener(){ public void actionPerformed(ActionEvent e){ addComment(); }});
            updateButton.addActionListener(new ActionListener(){ public void actionPerformed(ActionEvent e){ updateComment(); }});
            deleteButton.addActionListener(new ActionListener(){ public void actionPerformed(ActionEvent e){ deleteComment(); }});
            refreshButton.addActionListener(new ActionListener(){ 
                public void actionPerformed(ActionEvent e){ 
                    loadComments(); 
                    searchField.setText(""); // ADDED: Clear search on refresh
                    rowSorter.setRowFilter(null); // ADDED: Reset filter
                }
            });

            table.addMouseListener(new MouseAdapter(){
                public void mouseClicked(MouseEvent e){
                    int r = table.getSelectedRow();
                    if(r>=0){
                        // ADDED: Convert view row to model row for sorted table
                        int modelRow = table.convertRowIndexToModel(r);
                        txtCommentId.setText(String.valueOf(model.getValueAt(modelRow,0)));
                        txtArticleId.setText(String.valueOf(model.getValueAt(modelRow,1)));
                        txtUserId.setText(String.valueOf(model.getValueAt(modelRow,2)));
                        txtContent.setText(String.valueOf(model.getValueAt(modelRow,3)));
                        txtCreatedAt.setText(String.valueOf(model.getValueAt(modelRow,4)));
                    }
                }
            });
        }

        // ADDED: Search method for comments
        private void performCommentSearch() {
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

        private JPanel labeled(String label, JComponent comp) {
            JPanel p = new JPanel(new BorderLayout(4,4));
            p.setOpaque(false);
            JLabel l = new JLabel(label);
            l.setPreferredSize(new Dimension(90, 20));
            p.add(l, BorderLayout.WEST);
            p.add(comp, BorderLayout.CENTER);
            return p;
        }

        private void styleButton(JButton b, Color bg) {
            b.setBackground(bg); b.setForeground(Color.WHITE); b.setFocusPainted(false);
        }

        private void loadComments() {
            model.setRowCount(0);
            String sql = "SELECT * FROM comments ORDER BY created_at DESC";
            Connection conn = null; Statement st = null; ResultSet rs = null;
            try {
                conn = DBConnection.getConnection();
                st = conn.createStatement();
                rs = st.executeQuery(sql);
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("comment_id"),
                            rs.getObject("article_id"),
                            rs.getObject("user_id"),
                            rs.getString("content"),
                            rs.getTimestamp("created_at")
                    });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error loading comments: " + ex.getMessage());
            } finally {
                closeQuiet(rs); closeQuiet(st); closeQuiet(conn);
            }
        }

        private void addComment() {
            String cidText = txtCommentId.getText().trim();
            String aidText = txtArticleId.getText().trim();
            String uidText = txtUserId.getText().trim();
            String content = txtContent.getText().trim();
            if (aidText.isEmpty() || content.isEmpty()) { 
                JOptionPane.showMessageDialog(this, "Article ID and content required"); 
                return; 
            }
            Connection conn = null; PreparedStatement pst = null;
            try {
                conn = DBConnection.getConnection();
                String sql = "INSERT INTO comments (article_id, user_id, content, created_at) VALUES (?, ?, ?, NOW())";
                pst = conn.prepareStatement(sql);
                pst.setInt(1, Integer.parseInt(aidText));
                if (uidText.isEmpty()) pst.setNull(2, Types.INTEGER); else pst.setInt(2, Integer.parseInt(uidText));
                pst.setString(3, content);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Comment added!");
                clearCommentFields();
                loadComments();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error adding comment: " + ex.getMessage());
            } finally {
                closeQuiet(pst); closeQuiet(conn);
            }
        }

        private void updateComment() {
            int r = table.getSelectedRow();
            if (r == -1) { 
                JOptionPane.showMessageDialog(this, "Select a comment to update"); 
                return; 
            }
            // ADDED: Convert view row to model row for sorted table
            int modelRow = table.convertRowIndexToModel(r);
            Integer id = (Integer) model.getValueAt(modelRow, 0);
            String aidText = txtArticleId.getText().trim();
            String uidText = txtUserId.getText().trim();
            String content = txtContent.getText().trim();
            String createdAt = txtCreatedAt.getText().trim();
            
            if (aidText.isEmpty() || content.isEmpty()) { 
                JOptionPane.showMessageDialog(this, "Article ID and content required"); 
                return; 
            }
            
            Connection conn = null; PreparedStatement pst = null;
            try {
                conn = DBConnection.getConnection();
                String sql = "UPDATE comments SET article_id=?, user_id=?, content=?, created_at=? WHERE comment_id=?";
                pst = conn.prepareStatement(sql);
                pst.setInt(1, Integer.parseInt(aidText));
                if (uidText.isEmpty()) pst.setNull(2, Types.INTEGER); else pst.setInt(2, Integer.parseInt(uidText));
                pst.setString(3, content);
                pst.setString(4, createdAt.isEmpty() ? null : createdAt);
                pst.setInt(5, id);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Comment updated!");
                loadComments();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error updating comment: " + ex.getMessage());
            } finally {
                closeQuiet(pst); closeQuiet(conn);
            }
        }

        private void deleteComment() {
            int r = table.getSelectedRow();
            if (r == -1) { JOptionPane.showMessageDialog(this, "Select a comment to delete"); return; }
            // ADDED: Convert view row to model row for sorted table
            int modelRow = table.convertRowIndexToModel(r);
            Integer id = (Integer) model.getValueAt(modelRow, 0);
            int c = JOptionPane.showConfirmDialog(this, "Delete comment " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (c != JOptionPane.YES_OPTION) return;
            Connection conn = null; PreparedStatement pst = null;
            try {
                conn = DBConnection.getConnection();
                String sql = "DELETE FROM comments WHERE comment_id=?";
                pst = conn.prepareStatement(sql);
                pst.setInt(1, id);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Comment deleted!");
                loadComments();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting comment: " + ex.getMessage());
            } finally {
                closeQuiet(pst); closeQuiet(conn);
            }
        }

        private void clearCommentFields() {
            txtCommentId.setText("");
            txtArticleId.setText("");
            txtUserId.setText("");
            txtContent.setText("");
            txtCreatedAt.setText("");
        }
    }

    // --------------------- Advertisement Panel ---------------------
    public static class AdvertisementPanel extends JPanel {
        private JTable table; private DefaultTableModel model;
        private JTextField txtUserId, txtAttr1, txtAttr2, txtAttr3, txtCreatedBy;
        private JButton addButton, updateButton, deleteButton, refreshButton;
        private JTextField searchField; // ADDED: Search field
        private TableRowSorter<DefaultTableModel> rowSorter; // ADDED: Row sorter

        public AdvertisementPanel() {
            setLayout(new BorderLayout(8,8));
            setPreferredSize(new Dimension(1050, 310)); // Increased height for search

            // ADDED: Search Panel
            JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
            searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            searchPanel.setOpaque(false);
            
            JLabel searchLabel = new JLabel("Search:");
            searchField = new JTextField();
            JButton searchButton = styledButton("Search");
            
            searchPanel.add(searchLabel, BorderLayout.WEST);
            searchPanel.add(searchField, BorderLayout.CENTER);
            searchPanel.add(searchButton, BorderLayout.EAST);

            model = new DefaultTableModel(new String[]{
                    "Ad ID","User ID","Attribute1","Attribute2","Attribute3","Comment ID","Created At","Linked Comment ID","Created By User ID"
            },0);
            table = new JTable(model);
            
            // ADDED: Row sorter for search
            rowSorter = new TableRowSorter<>(model);
            table.setRowSorter(rowSorter);
            
            add(new JScrollPane(table), BorderLayout.CENTER);

            JPanel form = new JPanel(new GridLayout(1,6,8,8));
            txtUserId = new JTextField(); txtAttr1 = new JTextField(); txtAttr2 = new JTextField(); txtAttr3 = new JTextField(); txtCreatedBy = new JTextField();
            form.add(labeled("User ID:", txtUserId));
            form.add(labeled("Attr1:", txtAttr1));
            form.add(labeled("Attr2:", txtAttr2));
            form.add(labeled("Attr3:", txtAttr3));
            form.add(labeled("Created By User ID:", txtCreatedBy));

            // ADDED: Container for search and form
            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.add(searchPanel, BorderLayout.NORTH);
            topPanel.add(form, BorderLayout.CENTER);
            add(topPanel, BorderLayout.NORTH);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,10,6));
            addButton = styledButton("Add"); updateButton = styledButton("Update"); deleteButton = styledButton("Delete"); refreshButton = styledButton("Refresh");
            buttonPanel.add(addButton); buttonPanel.add(updateButton); buttonPanel.add(deleteButton); buttonPanel.add(refreshButton);
            add(buttonPanel, BorderLayout.SOUTH);

            loadAds();

            // ADDED: Search functionality
            searchButton.addActionListener(new ActionListener(){ 
                public void actionPerformed(ActionEvent e){ 
                    performAdSearch(); 
                }
            });

            searchField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    performAdSearch();
                }
            });

            addButton.addActionListener(new ActionListener(){ public void actionPerformed(ActionEvent e){ addAd(); }});
            updateButton.addActionListener(new ActionListener(){ public void actionPerformed(ActionEvent e){ updateAd(); }});
            deleteButton.addActionListener(new ActionListener(){ public void actionPerformed(ActionEvent e){ deleteAd(); }});
            refreshButton.addActionListener(new ActionListener(){ 
                public void actionPerformed(ActionEvent e){ 
                    loadAds(); 
                    searchField.setText(""); // ADDED: Clear search on refresh
                    rowSorter.setRowFilter(null); // ADDED: Reset filter
                }
            });

            table.addMouseListener(new MouseAdapter(){ public void mouseClicked(MouseEvent e){
                int r = table.getSelectedRow();
                if(r>=0){
                    // ADDED: Convert view row to model row for sorted table
                    int modelRow = table.convertRowIndexToModel(r);
                    txtUserId.setText(String.valueOf(model.getValueAt(modelRow,1)));
                    txtAttr1.setText(String.valueOf(model.getValueAt(modelRow,2)));
                    txtAttr2.setText(String.valueOf(model.getValueAt(modelRow,3)));
                    txtAttr3.setText(String.valueOf(model.getValueAt(modelRow,4)));
                    txtCreatedBy.setText(String.valueOf(model.getValueAt(modelRow,8)));
                }
            }});
        }

        // ADDED: Search method for advertisements
        private void performAdSearch() {
            String searchText = searchField.getText().trim();
            
            if (searchText.length() == 0) {
                rowSorter.setRowFilter(null);
            } else {
                try {
                    // Search in Attribute1 (column 2), Attribute2 (column 3), and Attribute3 (column 4)
                    RowFilter<DefaultTableModel, Object> rf = RowFilter.regexFilter("(?i)" + searchText, 2, 3, 4);
                    rowSorter.setRowFilter(rf);
                } catch (java.util.regex.PatternSyntaxException e) {
                    return; // If invalid regex, do nothing
                }
            }
        }

        private JPanel labeled(String label, JComponent comp) {
            JPanel p = new JPanel(new BorderLayout(4,4));
            p.setOpaque(false);
            JLabel l = new JLabel(label);
            l.setPreferredSize(new Dimension(110, 20));
            p.add(l, BorderLayout.WEST);
            p.add(comp, BorderLayout.CENTER);
            return p;
        }

        private JButton styledButton(String text) {
            JButton b = new JButton(text);
            b.setBackground(new Color(155, 89, 182));
            b.setForeground(Color.WHITE);
            b.setFocusPainted(false);
            return b;
        }

        private void loadAds() {
            model.setRowCount(0);
            String sql = "SELECT * FROM advertisements ORDER BY created_at DESC";
            Connection conn = null; Statement st = null; ResultSet rs = null;
            try {
                conn = DBConnection.getConnection();
                st = conn.createStatement();
                rs = st.executeQuery(sql);
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("ad_id"),
                            rs.getObject("user_id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getObject("price"),
                            rs.getObject("comment_id"),
                            rs.getTimestamp("created_at"),
                            rs.getObject("linked_comment_id"),
                            rs.getObject("created_by_user_id")
                    });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error loading advertisements: " + ex.getMessage());
            } finally {
                closeQuiet(rs); closeQuiet(st); closeQuiet(conn);
            }
        }

        private void addAd() {
            String uText = txtUserId.getText().trim();
            String a1 = txtAttr1.getText().trim();
            String a2 = txtAttr2.getText().trim();
            String a3 = txtAttr3.getText().trim();
            String createdByText = txtCreatedBy.getText().trim();
            Connection conn = null; PreparedStatement pst = null;
            try {
                conn = DBConnection.getConnection();
                String sql = "INSERT INTO advertisements (user_id, title, description, price, created_at, created_by_user_id) VALUES (?, ?, ?, ?, NOW(), ?)";
                pst = conn.prepareStatement(sql);
                if (uText.isEmpty()) pst.setNull(1, Types.INTEGER); else pst.setInt(1, Integer.parseInt(uText));
                pst.setString(2, a1); // title
                pst.setString(3, a2); // description
                if (a3.isEmpty()) pst.setNull(4, Types.DECIMAL); else pst.setDouble(4, Double.parseDouble(a3)); // price
                if (createdByText.isEmpty()) pst.setNull(5, Types.INTEGER); else pst.setInt(5, Integer.parseInt(createdByText));
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Advertisement added!");
                loadAds();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error adding advertisement: " + ex.getMessage());
            } finally {
                closeQuiet(pst); closeQuiet(conn);
            }
        }

        private void updateAd() {
            int r = table.getSelectedRow();
            if (r == -1) { JOptionPane.showMessageDialog(this, "Select an advertisement to update"); return; }
            // ADDED: Convert view row to model row for sorted table
            int modelRow = table.convertRowIndexToModel(r);
            Integer id = (Integer) model.getValueAt(modelRow,0);
            String uText = txtUserId.getText().trim();
            String a1 = txtAttr1.getText().trim();
            String a2 = txtAttr2.getText().trim();
            String a3 = txtAttr3.getText().trim();
            String createdByText = txtCreatedBy.getText().trim();
            Connection conn = null; PreparedStatement pst = null;
            try {
                conn = DBConnection.getConnection();
                String sql = "UPDATE advertisements SET user_id=?, title=?, description=?, price=?, created_by_user_id=? WHERE ad_id=?";
                pst = conn.prepareStatement(sql);
                if (uText.isEmpty()) pst.setNull(1, Types.INTEGER); else pst.setInt(1, Integer.parseInt(uText));
                pst.setString(2, a1); pst.setString(3, a2); 
                if (a3.isEmpty()) pst.setNull(4, Types.DECIMAL); else pst.setDouble(4, Double.parseDouble(a3));
                if (createdByText.isEmpty()) pst.setNull(5, Types.INTEGER); else pst.setInt(5, Integer.parseInt(createdByText));
                pst.setInt(6, id);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Advertisement updated!");
                loadAds();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error updating advertisement: " + ex.getMessage());
            } finally {
                closeQuiet(pst); closeQuiet(conn);
            }
        }

        private void deleteAd() {
            int r = table.getSelectedRow();
            if (r == -1) { JOptionPane.showMessageDialog(this, "Select an advertisement to delete"); return; }
            // ADDED: Convert view row to model row for sorted table
            int modelRow = table.convertRowIndexToModel(r);
            Integer id = (Integer) model.getValueAt(modelRow,0);
            int c = JOptionPane.showConfirmDialog(this, "Delete advertisement " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (c != JOptionPane.YES_OPTION) return;
            Connection conn = null; PreparedStatement pst = null;
            try {
                conn = DBConnection.getConnection();
                String sql = "DELETE FROM advertisements WHERE ad_id=?";
                pst = conn.prepareStatement(sql);
                pst.setInt(1, id);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Advertisement deleted!");
                loadAds();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting advertisement: " + ex.getMessage());
            } finally {
                closeQuiet(pst); closeQuiet(conn);
            }
        }
    }

    // --------------------- Tag Panel ---------------------
    public static class TagPanel extends JPanel {
        private JTable table; private DefaultTableModel model;
        private JButton addButton, refreshButton;
        private JTextField searchField; // ADDED: Search field
        private TableRowSorter<DefaultTableModel> rowSorter; // ADDED: Row sorter

        public TagPanel() {
            setLayout(new BorderLayout(8,8));
            setPreferredSize(new Dimension(1050, 270)); // Increased height for search

            // ADDED: Search Panel
            JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
            searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            searchPanel.setOpaque(false);
            
            JLabel searchLabel = new JLabel("Search:");
            searchField = new JTextField();
            JButton searchButton = new JButton("Search");
            styleButton(searchButton, new Color(52, 152, 219));
            
            searchPanel.add(searchLabel, BorderLayout.WEST);
            searchPanel.add(searchField, BorderLayout.CENTER);
            searchPanel.add(searchButton, BorderLayout.EAST);

            model=new DefaultTableModel(new String[]{"Tag ID","Name","Description","Category","Created At"},0);
            table=new JTable(model);
            
            // ADDED: Row sorter for search
            rowSorter = new TableRowSorter<>(model);
            table.setRowSorter(rowSorter);
            
            add(new JScrollPane(table),BorderLayout.CENTER);

            JPanel buttonPanel=new JPanel(new FlowLayout(FlowLayout.LEFT,10,6));
            addButton=new JButton("Add Tag"); refreshButton=new JButton("Refresh");
            styleButton(addButton, new Color(46,204,113));
            styleButton(refreshButton, new Color(52,152,219));
            buttonPanel.add(addButton); buttonPanel.add(refreshButton);
            
            // ADDED: Container for search and buttons
            JPanel bottomPanel = new JPanel(new BorderLayout());
            bottomPanel.add(searchPanel, BorderLayout.NORTH);
            bottomPanel.add(buttonPanel, BorderLayout.CENTER);
            add(bottomPanel,BorderLayout.SOUTH);

            loadTags();

            // ADDED: Search functionality
            searchButton.addActionListener(new ActionListener(){ 
                public void actionPerformed(ActionEvent e){ 
                    performTagSearch(); 
                }
            });

            searchField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    performTagSearch();
                }
            });

            addButton.addActionListener(new ActionListener(){ public void actionPerformed(ActionEvent e){ addTag(); }});
            refreshButton.addActionListener(new ActionListener(){ 
                public void actionPerformed(ActionEvent e){ 
                    loadTags(); 
                    searchField.setText(""); // ADDED: Clear search on refresh
                    rowSorter.setRowFilter(null); // ADDED: Reset filter
                }
            });
        }

        // ADDED: Search method for tags
        private void performTagSearch() {
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

        private void styleButton(JButton b, Color bg) { 
            b.setBackground(bg); 
            b.setForeground(Color.WHITE); 
            b.setFocusPainted(false); 
        }

        private void loadTags() {
            model.setRowCount(0);
            String sql="SELECT * FROM tags ORDER BY created_at DESC";
            Connection conn = null; Statement st = null; ResultSet rs = null;
            try {
                conn = DBConnection.getConnection();
                st = conn.createStatement();
                rs = st.executeQuery(sql);
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("tag_id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getString("category"),
                            rs.getTimestamp("created_at")
                    });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error loading tags: " + ex.getMessage());
            } finally {
                closeQuiet(rs); closeQuiet(st); closeQuiet(conn);
            }
        }

        private void addTag() {
            String name = JOptionPane.showInputDialog(this, "Enter Tag Name:");
            if (name == null || name.trim().isEmpty()) { 
                JOptionPane.showMessageDialog(this, "Name required"); 
                return; 
            }
            String description = JOptionPane.showInputDialog(this, "Enter Description:");
            String category = JOptionPane.showInputDialog(this, "Enter Category:");
            Connection conn = null; PreparedStatement pst = null;
            try {
                conn = DBConnection.getConnection();
                String sql = "INSERT INTO tags (name, description, category, created_at) VALUES (?, ?, ?, NOW())";
                pst = conn.prepareStatement(sql);
                pst.setString(1, name);
                pst.setString(2, description != null ? description : "");
                pst.setString(3, category != null ? category : "");
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Tag added!");
                loadTags();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error adding tag: " + ex.getMessage());
            } finally {
                closeQuiet(pst); closeQuiet(conn);
            }
        }
    }

    // --------------------- Utility ---------------------
    private static void closeQuiet(AutoCloseable ac) {
        if (ac == null) return;
        try { ac.close(); } catch (Exception e) { /* ignore */ }
    }

    // --------------------- MAIN ---------------------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MediaDashboard();
            }
        });
    }
}