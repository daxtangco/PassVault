package main.java;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

public class UserDatabase {

    public static boolean resetPassword(String username, String resetKey, String newPassword) {
        User user = getUserByUsername(username);
        if (user == null) {
            return false;
        }

        try {
            String decryptedResetKey = EncryptionService.decrypt(user.getEncryptedResetKey(), "your-very-secure-secret");
            if (!resetKey.equals(decryptedResetKey)) {
                return false;
            }

            String salt = EncryptionService.generateSalt();
            String hashedPassword = EncryptionService.hashPassword(newPassword, salt);
            String sql = "UPDATE users SET password = ? WHERE username = ?";
            try (Connection conn = Database.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, hashedPassword);
                pstmt.setString(2, username);

                int affectedRows = pstmt.executeUpdate();
                return affectedRows > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private Connection connect() {
        return Database.connect();
    }

    public void saveUser(User user) {
        String sql = "INSERT INTO users(username, password, reset_key) VALUES(?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEncryptedPassword());
            pstmt.setString(3, user.getEncryptedResetKey());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String encryptedPassword = rs.getString("password");
                String encryptedResetKey = rs.getString("reset_key");
                return new User(username, encryptedPassword, encryptedResetKey);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean usernameExists(String username) {
        String sql = "SELECT username FROM users WHERE username = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void updatePassword(String username, String encryptedPassword) {
        String sql = "UPDATE users SET password = ? WHERE username = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, encryptedPassword);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String generateResetKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[24];
        random.nextBytes(bytes);
        return Base64.getEncoder().withoutPadding().encodeToString(bytes);
    }
}
