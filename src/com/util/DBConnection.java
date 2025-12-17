package com.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class DBConnection {

    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Load MySQL driver (MySQL Connector/J 8.x)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Database connection details
            String url = "jdbc:mysql://localhost:3306/media_monitoring";
            String user = "root"; // your MySQL username
            String password = ""; // your MySQL password

            // Try to connect
            conn = DriverManager.getConnection(url, user, password);

            // Show connection success message
            JOptionPane.showMessageDialog(null,
                    "✅ Database connected successfully!",
                    "Connection Status",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                    "❌ MySQL Driver not found!\n" + e.getMessage(),
                    "Driver Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "❌ Database connection failed!\n" + e.getMessage(),
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "⚠️ Unexpected error:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        return conn;
    }

    // Simple test (optional)
    public static void main(String[] args) {
        Connection testConn = getConnection();
        if (testConn != null) {
            System.out.println("Connected successfully!");
            try {
                testConn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Connection failed!");
        }
    }
}

