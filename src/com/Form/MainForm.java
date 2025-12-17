package com.Form;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.pannel.ArticlePanel;
import com.pannel.CategoryPanel;
import com.pannel.CommentPanel;
import com.pannel.TagPanel;
import com.pannel.UserPanel;

public class MainForm extends JFrame implements ActionListener {

    private JButton articleButton;
    private JButton categoryButton;
    private JButton commentButton;
    private JButton userButton;
    private JButton tagButton;
    private JButton logoutButton;
    private JButton exitButton;

    public MainForm() {
        setTitle("Media Monitoring Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(7, 1, 10, 10));

        // Initialize buttons
        categoryButton = new JButton("Manage Categories");
        articleButton = new JButton("Manage Articles");
        commentButton = new JButton("Manage Comments");
        userButton = new JButton("Manage Users");
        tagButton = new JButton("Manage Tags");
        logoutButton = new JButton("Logout");
        exitButton = new JButton("Exit");

        // Add buttons to dashboard
        add(categoryButton);
        add(articleButton);
        add(commentButton);
        add(userButton);
        add(tagButton);
        add(logoutButton);
        add(exitButton);

        // Add action listeners
        categoryButton.addActionListener(this);
        articleButton.addActionListener(this);
        commentButton.addActionListener(this);
        userButton.addActionListener(this);
        tagButton.addActionListener(this);
        logoutButton.addActionListener(this);
        exitButton.addActionListener(this);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == categoryButton) {
            new CategoryPanel().setVisible(true);
        } else if (e.getSource() == articleButton) {
            new ArticlePanel().setVisible(true);
        } else if (e.getSource() == commentButton) {
            new CommentPanel().setVisible(true);
        } else if (e.getSource() == userButton) {
            new UserPanel().setVisible(true);
        } else if (e.getSource() == tagButton) {
            new TagPanel().setVisible(true);
        } else if (e.getSource() == logoutButton) {
            dispose(); // close dashboard
            JOptionPane.showMessageDialog(this, "Logged out successfully!");
            // new LoginForm().setVisible(true); // Uncomment if LoginForm exists
        } else if (e.getSource() == exitButton) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainForm();
            }
        });
    }
}
