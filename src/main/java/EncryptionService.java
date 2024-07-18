package main.java;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class EncryptionService {

    private static final String ALGORITHM = "AES";

    public static String encrypt(String data, String secret) throws Exception {
        SecretKey secretKey = generateKeyFromString(secret);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String encryptedData, String secret) throws Exception {
        SecretKey secretKey = generateKeyFromString(secret);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedBytes);
    }

    private static SecretKey generateKeyFromString(String key) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        byte[] keyBytes = key.getBytes("UTF-8");
        keyBytes = sha.digest(keyBytes);
        keyBytes = java.util.Arrays.copyOf(keyBytes, 16); // Use only the first 128 bits
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }

    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hashPassword(String password, String salt) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest((password + salt).getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(hash) + ":" + salt;
    }

    public static boolean verifyPassword(String password, String storedPasswordWithSalt) throws Exception {
        String[] parts = storedPasswordWithSalt.split(":");
        String hash = parts[0];
        String salt = parts[1];
        String hashedInputPassword = hashPassword(password, salt);
        return hashedInputPassword.equals(storedPasswordWithSalt);
    }

    public static String generateResetKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[24];
        random.nextBytes(bytes);
        return Base64.getEncoder().withoutPadding().encodeToString(bytes);
    }

    public static String encryptVaultPassword(String data, String secret) throws Exception {
        return encrypt(data, secret);
    }

    public static String decryptVaultPassword(String encryptedData, String secret) throws Exception {
        return decrypt(encryptedData, secret);
    }
}
