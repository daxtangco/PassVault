package main.java;

public class PasswordEntry {
    private int id;
    private String alias;
    private String platform;
    private String username;
    private String encryptedPassword;
    private String user;

    public PasswordEntry(String alias, String platform, String username, String encryptedPassword, String user) {
        this.alias = alias;
        this.platform = platform;
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.user = user;
    }

    public String getAlias() {
        return alias;
    }

    public String getPlatform() {
        return platform;
    }

    public String getUsername() {
        return username;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public String getUser() {
        return user;
    }

}
