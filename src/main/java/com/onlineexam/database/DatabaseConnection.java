package com.onlineexam.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConnection {
    private String url;
    private String username;
    private String password;

    public DatabaseConnection() {
        loadProperties();
    }

    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("application.properties")) {
            
            if (input == null) {
                System.err.println("Unable to find application.properties");
                // Fallback to default values
                this.url = "jdbc:oracle:thin:@localhost:1521:xe";
                this.username = "examplatform";
                this.password = "yourpassword";
                return;
            }

            Properties prop = new Properties();
            prop.load(input);

            this.url = prop.getProperty("spring.datasource.url", "jdbc:oracle:thin:@localhost:1521:xe");
            this.username = prop.getProperty("spring.datasource.username", "examplatform");
            this.password = prop.getProperty("spring.datasource.password", "yourpassword");

        } catch (Exception e) {
            System.err.println("Error loading properties: " + e.getMessage());
            // Fallback to default values
            this.url = "jdbc:oracle:thin:@localhost:1521:xe";
            this.username = "examplatform";
            this.password = "yourpassword";
        }
    }

    public Connection connect() throws SQLException {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            return DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Oracle JDBC Driver not found", e);
        }
    }

    // Test connection method
    public boolean testConnection() {
        try (Connection conn = connect()) {
            System.out.println("✅ Database connection successful!");
            System.out.println("Connected to: " + conn.getMetaData().getDatabaseProductName());
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed: " + e.getMessage());
            return false;
        }
    }
}