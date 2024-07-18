package main.java;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PasswordVault {

    public List<PasswordEntry> getEntriesForUser(String username) {
        List<PasswordEntry> entries = new ArrayList<>();
        String sql = "SELECT id, alias, platform, username, encrypted_password FROM vault WHERE user_username = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String alias = rs.getString("alias");
                String platform = rs.getString("platform");
                String user = rs.getString("username");
                String encryptedPassword = rs.getString("encrypted_password");
                entries.add(new PasswordEntry(alias, platform, user, encryptedPassword, username));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return entries;
    }

    public void addEntry(PasswordEntry entry) {
        String sql = "INSERT INTO vault(alias, platform, username, encrypted_password, user_username) VALUES(?, ?, ?, ?, ?)";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, entry.getAlias());
            pstmt.setString(2, entry.getPlatform());
            pstmt.setString(3, entry.getUsername());
            pstmt.setString(4, entry.getEncryptedPassword());
            pstmt.setString(5, entry.getUser());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeEntry(PasswordEntry entry) {
        String sql = "DELETE FROM vault WHERE alias = ? AND user_username = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, entry.getAlias());
            pstmt.setString(2, entry.getUser());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
