package main.java;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String DATABASE_NAME = "PassVault.db";
    private static String DATABASE_PATH;
    private static String DATABASE_URL;

    static {
        try {
            DATABASE_PATH = getApplicationDirectory() + File.separator + DATABASE_NAME;
            DATABASE_URL = "jdbc:sqlite:" + DATABASE_PATH;
            setupDatabase();
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        initializeDatabase();
    }

    private static String getApplicationDirectory() throws URISyntaxException {
        File jarFile = new File(Database.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        return jarFile.getParentFile().getAbsolutePath();
    }

    private static void setupDatabase() throws IOException {
        File dbFile = new File(DATABASE_PATH);
        if (!dbFile.exists()) {
            System.out.println("Database file does not exist, copying from resources to " + DATABASE_PATH);
            try (InputStream is = Database.class.getResourceAsStream("/" + DATABASE_NAME)) {
                if (is == null) {
                    throw new IOException("Database file not found in resources.");
                }
                Files.copy(is, dbFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Database copied to " + DATABASE_PATH);
            }
        } else {
            System.out.println("Using existing database at " + DATABASE_PATH);
        }
    }

    public static Connection connect() {
        try {
            Connection conn = DriverManager.getConnection(DATABASE_URL);
            System.out.println("Connection to SQLite has been established at " + DATABASE_PATH);
            return conn;
        } catch (SQLException e) {
            System.out.println("Failed to connect to the database: " + e.getMessage());
            return null;
        }
    }

    public static void initializeDatabase() {
        String userTable = "CREATE TABLE IF NOT EXISTS users (" +
                "username TEXT PRIMARY KEY, " +
                "password TEXT NOT NULL, " +
                "reset_key TEXT NOT NULL" +
                ");";

        String vaultTable = "CREATE TABLE IF NOT EXISTS vault (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "alias TEXT NOT NULL, " +
                "platform TEXT NOT NULL, " +
                "username TEXT NOT NULL, " +
                "encrypted_password TEXT NOT NULL, " +
                "user_username TEXT NOT NULL, " +
                "FOREIGN KEY (user_username) REFERENCES users(username)" +
                ");";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(userTable);
            stmt.execute(vaultTable);
            System.out.println("Database tables created.");
        } catch (SQLException e) {
            System.out.println("Failed to initialize the database: " + e.getMessage());
        }
    }
}
