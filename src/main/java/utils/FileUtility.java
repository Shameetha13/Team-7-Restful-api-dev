package utils;

import java.io.FileInputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class FileUtility {

    private static final String CONFIG_PATH = System.getProperty("user.dir");
         
    private static Properties loadProperties() {
        Properties props = new Properties();
        try {
            FileInputStream fis = new FileInputStream(CONFIG_PATH);
            props.load(fis);
            fis.close();
        } catch (Exception e) {
            System.out.println("[FileUtility] loadProperties Error: " + e.getMessage());
        }
        return props;
    }

  
    public static String getProperty(String key) {
        try {
            String value = loadProperties().getProperty(key, "").trim();
            System.out.println("[FileUtility] Key: " + key + " | Value: " + value);
            return value;
        } catch (Exception e) {
            System.out.println("[FileUtility] getProperty Error [key: " + key + "]: " + e.getMessage());
            return "";
        }
    }

    public static Map<String, String> getAllProperties() {
        Map<String, String> configMap = new LinkedHashMap<>();
        try {
            Properties props = loadProperties();
            for (String key : props.stringPropertyNames()) {
                configMap.put(key, props.getProperty(key).trim());
            }
            System.out.println("[FileUtility] Total properties loaded: " + configMap.size());
        } catch (Exception e) {
            System.out.println("[FileUtility] getAllProperties Error: " + e.getMessage());
        }
        return configMap;
    }

    public static boolean hasProperty(String key) {
        boolean exists = !getProperty(key).isEmpty();
        System.out.println("[FileUtility] Key '" + key + "' exists: " + exists);
        return exists;
    }
}