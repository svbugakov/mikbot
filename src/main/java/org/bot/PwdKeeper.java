package org.bot;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class PwdKeeper {
    private final Map<String, String> passwords = new HashMap<>();
    private String filePath;


    public PwdKeeper() throws Exception {
        this.filePath = "pwd.txt";
        loadPasswords();
    }

    /**
     * Загружает пароли из файла
     *

     * @throws IOException если файл не найден или ошибка чтения
     */
    public void loadPasswords() throws Exception {
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        String[] lines = content.split("\n");

        for (String line : lines) {
            if (line.trim().isEmpty()) continue;

            String[] parts = line.split("=", 2);
            if (parts.length == 2) {
                final String typePwd = parts[0].trim();
                if(typePwd.equals("giga")) {
                    passwords.put(parts[0].trim(), decrypt(parts[1].trim()));
                } else {
                    passwords.put(parts[0].trim(), parts[1].trim());
                }
             }
        }
    }

    /**
     * Возвращает пароль по типу
     *
     * @param passwordType тип пароля (например, "type_pwd1")
     * @return пароль или null, если не найден
     */
    public String getPassword(String passwordType) {
        return passwords.get(passwordType);
    }

    /**
     * Возвращает все пары тип-пароль
     */
    public Map<String, String> getAllPasswords() {
        return new HashMap<>(passwords); // Возвращаем копию для безопасности
    }

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String SECRET_KEY_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int KEY_SIZE = 256;
    private static final int ITERATION_COUNT = 65536;
    private static final String MASTER_PASSWORD = "xxx";

    public String encrypt(String password) throws Exception {
        SecretKey secretKey = generateSecretKey(MASTER_PASSWORD);
        byte[] iv = new byte[16];
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));

        byte[] encryptedData = cipher.doFinal(password.getBytes());
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    // Дешифрование
    public String decrypt(String encryptedPassword) throws Exception {
        SecretKey secretKey = generateSecretKey(MASTER_PASSWORD);
        byte[] iv = new byte[16];
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));

        byte[] decodedData = Base64.getDecoder().decode(encryptedPassword);
        byte[] decryptedData = cipher.doFinal(decodedData);
        return new String(decryptedData);
    }

    // Генерация ключа из мастер-пароля
    private SecretKey generateSecretKey(String masterPassword) throws Exception {
        byte[] salt = "xxx".getBytes(); // В реальности используйте уникальную соль

        SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_KEY_ALGORITHM);
        KeySpec spec = new PBEKeySpec(masterPassword.toCharArray(), salt, ITERATION_COUNT, KEY_SIZE);

        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }
}
