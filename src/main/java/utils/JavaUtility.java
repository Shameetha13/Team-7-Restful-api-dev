package utils;

import java.util.Random;

public class JavaUtility {
private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz";

    public static String getRandomString(int length) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    public static String getRandomEmail() {
        return getRandomString(8) + "@test.com";
    }

    public static String getRandomName() {
        String random = getRandomString(5);
        return random.substring(0, 1).toUpperCase() + random.substring(1);
    }
}
