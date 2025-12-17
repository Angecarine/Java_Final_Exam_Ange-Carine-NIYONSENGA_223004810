package com.Form;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ContentCreatorDashboard extends JFrame implements ActionListener {

    private JButton articleButton, tagButton, advertisementButton, commentButton, logoutButton;
    private String username;

    public ContentCreatorDashboard(String username) {
        this.username = username;
        setTitle("Content Creator Dashboard - " + username);
        setSize(900, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(60, 179, 113));
        topPanel.add(new JLabel("<html><font color='white' size=5><b>Content Creator Dashboard</b></font></html>"));
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        centerPanel.setBackground(new Color(245, 255, 250));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        articleButton = createButton("My Articles", new Color(144, 238, 144));
        tagButton = createButton("Manage Tags", new Color(173, 216, 230));
        advertisementButton = createButton("My Advertisements", new Color(255, 218, 185));
        commentButton = createButton("Manage Comments", new Color(255, 182, 193));

        articleButton.addActionListener(this);
        tagButton.addActionListener(this);
        advertisementButton.addActionListener(this);
        commentButton.addActionListener(this);

        centerPanel.add(articleButton);
        centerPanel.add(tagButton);
        centerPanel.add(advertisementButton);
        centerPanel.add(commentButton);

        add(centerPanel, BorderLayout.CENTER);

        logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(255, 99, 71));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(this);
        add(logoutButton, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JButton createButton(String text, final Color color) {
        final JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(color.darker());
                btn.setForeground(Color.WHITE);
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(color);
                btn.setForeground(Color.BLACK);
            }
        });
        return btn;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == logoutButton) {
            dispose();
            // Open login form if you have it
        } else if (e.getSource() == articleButton) {
            new com.pannel.ArticlePanel();
        } else if (e.getSource() == tagButton) {
            new com.pannel.TagPanel();
        } else if (e.getSource() == advertisementButton) {
            new com.pannel.AdvertisementPanel();
        } else if (e.getSource() == commentButton) {
            new com.pannel.CommentPanel();
        }
    }

    public static void main(String[] args) {
        new ContentCreatorDashboard("editorUser");
    }
}
