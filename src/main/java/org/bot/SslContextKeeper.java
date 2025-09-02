package org.bot;

import javax.net.ssl.SSLContext;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс для создания и управления несколькими SSLContext
 */
public class SslContextKeeper {
    private final Map<String, SSLContext> sslContexts = new HashMap<>();

    /**
     * Конфигурация для SSLContext
     */
    public static class SslConfig {
        private final String keyStorePath;
        private final String keyStorePassword;
        private final String trustStorePath;
        private final String trustStorePassword;
        private final String keyStoreType;
        private final String trustStoreType;

        public SslConfig(String keyStorePath, String keyStorePassword,
                         String trustStorePath, String trustStorePassword) {
            this(keyStorePath, keyStorePassword, trustStorePath, trustStorePassword,
                    "JKS", "JKS");
        }

        public SslConfig(String keyStorePath, String keyStorePassword,
                         String trustStorePath, String trustStorePassword,
                         String keyStoreType, String trustStoreType) {
            this.keyStorePath = keyStorePath;
            this.keyStorePassword = keyStorePassword;
            this.trustStorePath = trustStorePath;
            this.trustStorePassword = trustStorePassword;
            this.keyStoreType = keyStoreType;
            this.trustStoreType = trustStoreType;
        }

        // Getters
        public String getKeyStorePath() {
            return keyStorePath;
        }

        public String getKeyStorePassword() {
            return keyStorePassword;
        }

        public String getTrustStorePath() {
            return trustStorePath;
        }

        public String getTrustStorePassword() {
            return trustStorePassword;
        }

        public String getKeyStoreType() {
            return keyStoreType;
        }

        public String getTrustStoreType() {
            return trustStoreType;
        }
    }

    /**
     * Создает SSLContext на основе конфигурации
     */
    public SSLContext createSslContext(SslConfig config) throws Exception {
        // Загрузка keystore
        KeyStore keyStore = KeyStore.getInstance(config.getKeyStoreType());
        try (InputStream keyStoreStream = new FileInputStream(config.getKeyStorePath())) {
            keyStore.load(keyStoreStream, config.getKeyStorePassword().toCharArray());
        }

        // Загрузка truststore
        KeyStore trustStore = KeyStore.getInstance(config.getTrustStoreType());
        try (InputStream trustStoreStream = new FileInputStream(config.getTrustStorePath())) {
            trustStore.load(trustStoreStream, config.getTrustStorePassword().toCharArray());
        }

        // Настройка KeyManagerFactory
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
                KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, config.getKeyStorePassword().toCharArray());

        // Настройка TrustManagerFactory
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);

        // Создание SSLContext
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(
                keyManagerFactory.getKeyManagers(),
                trustManagerFactory.getTrustManagers(),
                new SecureRandom()
        );

        return sslContext;
    }

    /**
     * Добавляет SSLContext в хранилище по имени
     */
    public void addSslContext(String name, SslConfig config) throws Exception {
        SSLContext sslContext = createSslContext(config);
        sslContexts.put(name, sslContext);
    }

    /**
     * Добавляет несколько SSLContext из Map конфигураций
     */
    public void addAllSslContexts(Map<String, SslConfig> configs) throws Exception {
        for (Map.Entry<String, SslConfig> entry : configs.entrySet()) {
            addSslContext(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Возвращает SSLContext по имени
     */
    public SSLContext getSslContext(String name) {
        return sslContexts.get(name);
    }

    /**
     * Возвращает все SSLContext
     */
    public Map<String, SSLContext> getAllSslContexts() {
        return new HashMap<>(sslContexts);
    }

    /**
     * Удаляет SSLContext по имени
     */
    public void removeSslContext(String name) {
        sslContexts.remove(name);
    }

    /**
     * Очищает все SSLContext
     */
    public void clear() {
        sslContexts.clear();
    }

    /**
     * Проверяет наличие SSLContext по имени
     */
    public boolean contains(String name) {
        return sslContexts.containsKey(name);
    }

    /**
     * Возвращает количество SSLContext
     */
    public int size() {
        return sslContexts.size();
    }
}