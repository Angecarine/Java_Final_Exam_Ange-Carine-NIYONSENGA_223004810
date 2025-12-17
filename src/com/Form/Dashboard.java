package com.Form;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Dashboard extends JFrame implements ActionListener {

    private JButton categoryButton, articleButton, commentButton, userButton, tagButton, logoutButton, exitButton;
    private JComboBox<String> themeSelector;

    public Dashboard() {
        setTitle("Dashboard");
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Top Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(176, 196, 222));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Dashboard", JLabel.LEFT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));

        String[] themes = {"Default", "Light Blue", "Dark"};
        themeSelector = new JComboBox<String>(themes);
        themeSelector.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        themeSelector.addActionListener(this);

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(themeSelector, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Center Panel with Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        buttonPanel.setBackground(new Color(245, 245, 245));

        categoryButton = createColoredButton("Manage Categories", new Color(255, 102, 102));
        articleButton = createColoredButton("Manage Articles", new Color(102, 255, 102));
        commentButton = createColoredButton("Manage Comments", new Color(102, 102, 255));
        userButton = createColoredButton("Manage Users", new Color(255, 204, 102));
        tagButton = createColoredButton("Manage Tags", new Color(204, 102, 255));

        buttonPanel.add(categoryButton);
        buttonPanel.add(articleButton);
        buttonPanel.add(commentButton);
        buttonPanel.add(userButton);
        buttonPanel.add(tagButton);

        add(buttonPanel, BorderLayout.CENTER);

        // Bottom Panel with Logout & Exit
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomPanel.setBackground(new Color(176, 196, 222));

        logoutButton = createColoredButton("Logout", new Color(102, 204, 255));
        exitButton = createColoredButton("Exit", new Color(255, 153, 153));

        bottomPanel.add(logoutButton);
        bottomPanel.add(exitButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Add ActionListener to buttons
        categoryButton.addActionListener(this);
        articleButton.addActionListener(this);
        commentButton.addActionListener(this);
        userButton.addActionListener(this);
        tagButton.addActionListener(this);
        logoutButton.addActionListener(this);
        exitButton.addActionListener(this);

        setVisible(true);
    }

    public Dashboard(String string) {
		// TODO Auto-generated constructor stub
	}

	private JButton createColoredButton(String text, final Color color) {
        final JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(color);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Mouse hover effect
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
            Class panelClass = Class.forName("com.pannel." + panelName);
            JFrame panel = (JFrame) panelClass.getDeclaredConstructor().newInstance();
            panel.setVisible(true);
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, panelName + " class not found!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (InstantiationException ex) {
            JOptionPane.showMessageDialog(this, "Cannot instantiate " + panelName, "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalAccessException ex) {
            JOptionPane.showMessageDialog(this, "Illegal access: " + panelName, "Error", JOptionPane.ERROR_MESSAGE);
        } catch (java.lang.reflect.InvocationTargetException ex) {
            JOptionPane.showMessageDialog(this, "Constructor error: " + panelName, "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NoSuchMethodException ex) {
            JOptionPane.showMessageDialog(this, "No default constructor: " + panelName, "Error", JOptionPane.ERROR_MESSAGE);
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

    @Override
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
        } else if (source == logoutButton) {
            dispose();
            new LoginForm().setVisible(true);
        } else if (source == exitButton) {
            System.exit(0);
        } else if (source == themeSelector) {
            changeTheme((String) themeSelector.getSelectedItem());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Dashboard();
            }
        });
    }
}
