package com.Form;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import com.pannel.ViewerArticlePanel;
import com.pannel.ViewerCommentPanel;

public class ViewerDashboard extends JFrame implements ActionListener {

    private JButton viewArticlesButton, viewCommentsButton, logoutButton;
    private String username;

    public ViewerDashboard(String username) {
        this.username = username;
        setTitle("Viewer Dashboard - " + username);
        setSize(900, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(100, 149, 237));
        topPanel.add(new JLabel("<html><font color='white' size=5><b>Viewer Dashboard</b></font></html>"));
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        centerPanel.setBackground(new Color(240, 248, 255));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        viewArticlesButton = createButton("View Articles", new Color(173, 216, 230));
        viewCommentsButton = createButton("View Comments", new Color(255, 228, 181));

        centerPanel.add(viewArticlesButton);
        centerPanel.add(viewCommentsButton);

        add(centerPanel, BorderLayout.CENTER);

        logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(255, 69, 0));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(this);
        add(logoutButton, BorderLayout.SOUTH);

        // === Button Actions ===
        viewArticlesButton.addActionListener(this);
        viewCommentsButton.addActionListener(this);

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
            new LoginForm();
        } else if (e.getSource() == viewArticlesButton) {
            new ViewerArticlePanel();
        } else if (e.getSource() == viewCommentsButton) {
            new ViewerCommentPanel();
        }
    }

    public static void main(String[] args) {
        new ViewerDashboard("TestUser");
    }
}
