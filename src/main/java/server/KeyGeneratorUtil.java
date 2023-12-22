package server;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.FileOutputStream;
import java.util.Base64;
import java.util.Properties;

public class KeyGeneratorUtil {

    public static void generateAndSaveKey(String filename) throws Exception {
        // Generate the AES key
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256); // for AES-256
        SecretKey secretKey = keyGen.generateKey();

        // Convert to Base64 for easy storage
        String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());

        // Save to properties file
        Properties prop = new Properties();
        prop.setProperty("encryptionKey", encodedKey);
        try (FileOutputStream output = new FileOutputStream(filename)) {
            prop.store(output, "Encryption Key");
        }
    }

    public static void main(String[] args) {
        try {
            generateAndSaveKey("src/main/ressources/config.properties");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
