package utils;

import java.io.FileInputStream;
import java.util.Properties;

public class FileUtility {

    private static Properties prop;

    static {
        try {
            FileInputStream fis = new FileInputStream("src/test/resources/config.properties");
            prop = new Properties();
            prop.load(fis);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config.properties: " + e.getMessage());
        }
    }

    public static String get(String key) {
        String value = prop.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Property key not found in config.properties: " + key);
        }
        return value.trim();
    }
}
