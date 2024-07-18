package main.java;

public class User {
    private String username;
    private String encryptedPassword;
    private String encryptedResetKey;

    public User(String username, String encryptedPassword, String encryptedResetKey) {
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.encryptedResetKey = encryptedResetKey;
    }

    public String getUsername() {
        return username;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public String getEncryptedResetKey() {
        return encryptedResetKey;
    }
}
