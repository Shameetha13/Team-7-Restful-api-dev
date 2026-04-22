package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class FileUtility {

    private static Properties properties = new Properties();

    // 🔥 Static block → loads file once
    static {
        try {
            String path = System.getProperty("user.dir") + "/src/test/resources/config/config.properties";
            FileInputStream fis = new FileInputStream(path);
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties file", e);
        }
    }

    // ✅ Get value using key
    public static String getProperty(String key) {
        String value = properties.getProperty(key);

        if (value == null) {
            throw new RuntimeException("Key not found in config.properties: " + key);
        }

        return value.trim();
    }
}