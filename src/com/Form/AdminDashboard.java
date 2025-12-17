package com.Form;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AdminDashboard extends JFrame implements ActionListener {

    private JButton categoryButton, articleButton, commentButton, userButton, tagButton, advertisementButton, logoutButton, exitButton, searchButton;
    private JComboBox<String> themeSelector;
    private JTextField searchField;
    private String username;

    public AdminDashboard(String username) {
        this.username = username;
        setTitle("Administrator Dashboard - " + username);
        setSize(900, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        //  TOP PANEL 
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(70, 130, 180));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel titleLabel = new JLabel("Administrator Dashboard", JLabel.LEFT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        // Create RIGHT panel for search + theme
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        rightPanel.setBackground(new Color(70, 130, 180));

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setForeground(Color.WHITE);
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        searchField = new JTextField(12);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        searchButton = new JButton("Go");
        searchButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        searchButton.setFocusPainted(false);
        searchButton.addActionListener(this);

        String[] themes = {"Default", "Light Blue", "Dark"};
        themeSelector = new JComboBox<String>(themes);
        themeSelector.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        themeSelector.addActionListener(this);

        // Add to right side
        rightPanel.add(searchLabel);
        rightPanel.add(searchField);
        rightPanel.add(searchButton);
        rightPanel.add(themeSelector);

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        //  CENTER BUTTON PANEL 
        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        buttonPanel.setBackground(new Color(245, 245, 245));

        categoryButton = createColoredButton("Manage Categories", new Color(255, 204, 102));
        articleButton = createColoredButton("Manage Articles", new Color(102, 204, 255));
        commentButton = createColoredButton("Manage Comments", new Color(255, 153, 153));
        userButton = createColoredButton("Manage Users", new Color(153, 255, 153));
        tagButton = createColoredButton("Manage Tags", new Color(204, 153, 255));
        advertisementButton = createColoredButton("Manage Advertisements", new Color(255, 204, 204));

        buttonPanel.add(articleButton);
        buttonPanel.add(categoryButton);
        buttonPanel.add(commentButton);
        buttonPanel.add(userButton);
        buttonPanel.add(tagButton);
        buttonPanel.add(advertisementButton);

        add(buttonPanel, BorderLayout.CENTER);

        //  BOTTOM PANEL 
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomPanel.setBackground(new Color(70, 130, 180));

        logoutButton = createColoredButton("Logout", new Color(255, 99, 71));
        exitButton = createColoredButton("Exit", new Color(255, 182, 193));

        bottomPanel.add(logoutButton);
        bottomPanel.add(exitButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Add listeners
        categoryButton.addActionListener(this);
        articleButton.addActionListener(this);
        commentButton.addActionListener(this);
        userButton.addActionListener(this);
        tagButton.addActionListener(this);
        advertisementButton.addActionListener(this);
        logoutButton.addActionListener(this);
        exitButton.addActionListener(this);
        searchButton.addActionListener(this);

        setVisible(true);
    }

    private JButton createColoredButton(String text, final Color color) {
        final JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(color);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(color.darker());
                button.setForeground(Color.WHITE);
            }

            public void mouseExited(MouseEvent evt) {
                button.setBackground(color);
                button.setForeground(Color.BLACK);
            }
        });

        return button;
    }

    private void openPanel(String panelName) {
        try {
            Class<?> panelClass = Class.forName("com.pannel." + panelName);
            JFrame panel = (JFrame) panelClass.getDeclaredConstructor().newInstance();
            panel.setVisible(true);
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, panelName + " class not found!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading " + panelName + ": " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void changeTheme(String theme) {
        Color bgColor;
        if (theme.equals("Dark")) {
            bgColor = new Color(45, 45, 45);
        } else if (theme.equals("Light Blue")) {
            bgColor = new Color(173, 216, 230);
        } else {
            bgColor = new Color(245, 245, 245);
        }
        getContentPane().setBackground(bgColor);
    }

    // SEARCH LOGIC 
    private void performSearch() {
        String query = searchField.getText().trim().toLowerCase();

        if (query.equals("")) {
            JOptionPane.showMessageDialog(this, "Please enter something to search!");
            return;
        }

        if (query.contains("article")) {
            openPanel("ArticlePanel");
        } else if (query.contains("category")) {
            openPanel("CategoryPanel");
        } else if (query.contains("comment")) {
            openPanel("CommentPanel");
        } else if (query.contains("user")) {
            openPanel("UserPanel");
        } else if (query.contains("tag")) {
            openPanel("TagPanel");
        } else if (query.contains("advertisement") || query.contains("ad")) {
            openPanel("AdvertisementPanel");
        } else {
            JOptionPane.showMessageDialog(this, "No matching module found for: " + query);
        }
    }

    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == categoryButton) {
            openPanel("CategoryPanel");
        } else if (source == articleButton) {
            openPanel("ArticlePanel");
        } else if (source == commentButton) {
            openPanel("CommentPanel");
        } else if (source == userButton) {
            openPanel("UserPanel");
        } else if (source == tagButton) {
            openPanel("TagPanel");
        } else if (source == advertisementButton) {
            openPanel("AdvertisementPanel");
        } else if (source == logoutButton) {
            dispose();
            new LoginForm().setVisible(true);
        } else if (source == exitButton) {
            System.exit(0);
        } else if (source == themeSelector) {
            changeTheme((String) themeSelector.getSelectedItem());
        } else if (source == searchButton) {
            performSearch();
        }
    }

    public static void main(String[] args) {
        new AdminDashboard("AdminUser");
    }
}
