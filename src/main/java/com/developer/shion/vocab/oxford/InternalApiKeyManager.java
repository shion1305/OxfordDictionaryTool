package com.developer.shion.vocab.oxford;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class InternalApiKeyManager {
    private static final Properties properties = new Properties();

    static {
        try (FileInputStream s = new FileInputStream("OxfordApiConfig.properties")) {
            properties.load(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getApiId() {
        return properties.getProperty("OxfordApiId");
    }

    public static String getApiKey() {
        return properties.getProperty("OxfordApiKey");
    }
}
