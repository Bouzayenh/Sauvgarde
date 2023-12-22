package server;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Properties;

public class ConfigLoader {
    private static final String CONFIG_FILE = "config.properties";
    private static SecretKey secretKey;

    public static SecretKey getSecretKey() {
        try {
            Properties prop = new Properties();
            try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
                prop.load(input);
            }
            String keyString = prop.getProperty("encryptionKey");
            byte[] decodedKey = Base64.getDecoder().decode(keyString);
            return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        } catch (IOException e) {
            throw new RuntimeException("Error loading secret key", e);
        }
    }

    private static void loadConfig() {
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find " + CONFIG_FILE);
                return;
            }

            prop.load(input);
            String keyString = prop.getProperty("encryptionKey");
            // Convert your string key to SecretKey. Example:
            secretKey = new SecretKeySpec(keyString.getBytes(), "AES");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}