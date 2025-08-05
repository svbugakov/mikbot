package org.bot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class PwdKeeper {
    private final Map<String, String> passwords = new HashMap<>();
    private String filePath;


    public PwdKeeper() throws IOException {
        this.filePath = "pwd.txt";
        loadPasswords();
    }

    /**
     * Загружает пароли из файла
     *

     * @throws IOException если файл не найден или ошибка чтения
     */
    public void loadPasswords() throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        String[] lines = content.split("\n");

        for (String line : lines) {
            if (line.trim().isEmpty()) continue;

            String[] parts = line.split("=", 2);
            if (parts.length == 2) {
                passwords.put(parts[0].trim(), parts[1].trim());
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
}
