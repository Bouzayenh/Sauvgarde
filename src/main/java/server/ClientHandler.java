package server;

import common.FileBackup;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.stream.Stream;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;


class ClientHandler extends Thread {
    private SSLSocket sslSocket;

    public ClientHandler(SSLSocket socket) {
        this.sslSocket = socket;
    }

    @Override
    public void run() {
        try (ObjectInputStream in = new ObjectInputStream(sslSocket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(sslSocket.getOutputStream())) {

            Object obj;
            while ((obj = in.readObject()) != null) {
                if (obj instanceof FileBackup) {
                    FileBackup backup = (FileBackup) obj;
                    saveBackup(backup, out);
                } else if (obj instanceof String && ((String) obj).startsWith("RESTORE:")) {
                    String baseFolderName = ((String) obj).substring(8); // Extract base folder name
                    restoreFiles(baseFolderName, out);
                }
            }
        } catch (EOFException e) {
            System.out.println("Client disconnected.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                sslSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveBackup(FileBackup backup, ObjectOutputStream out) throws IOException {
        try {
            String relativePath = backup.getFileName();
            Path backupPath = Paths.get(Server.BACKUP_DIR, relativePath).normalize();

            // Ensure the path is still within the backup directory
            if (!backupPath.startsWith(Paths.get(Server.BACKUP_DIR))) {
                out.writeObject("Invalid file path: " + relativePath);
                return;
            }

            // Create directories if they don't exist
            if (Files.notExists(backupPath.getParent())) {
                Files.createDirectories(backupPath.getParent());
            }

            byte[] fileContent = Base64.getDecoder().decode(backup.getFileContent());
            SecretKey secretKey = ConfigLoader.getSecretKey(); // Load the secret key
            byte[] encryptedContent = encrypt(fileContent, secretKey); // Encrypt content

            Files.write(backupPath, encryptedContent);
            out.writeObject("Backup updated for: " + backup.getFileName());
        } catch (Exception e) {
            e.printStackTrace();
            out.writeObject("Error processing file: " + backup.getFileName());
        }
        out.flush();
    }

    private void restoreFiles(String baseFolderName, ObjectOutputStream out) throws IOException {
        Path backupFolderPath = Paths.get(Server.BACKUP_DIR, baseFolderName);
        if (Files.notExists(backupFolderPath)) {
            out.writeObject("No backup found for the specified folder.");
            return;
        }

        try (Stream<Path> paths = Files.walk(backupFolderPath)) {
            paths.filter(Files::isRegularFile).forEach(file -> {
                try {
                    byte[] fileContent = Files.readAllBytes(file);
                    SecretKey secretKey = ConfigLoader.getSecretKey(); // Load the secret key
                    byte[] decryptedContent = decrypt(fileContent, secretKey); // Decrypt content
                    String encodedContent = Base64.getEncoder().encodeToString(decryptedContent);
                    String relativePath = backupFolderPath.relativize(file).toString();
                    out.writeObject(new FileBackup(relativePath, encodedContent));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
        out.writeObject(null); // Indicate end of file transmission
    }

    // Encryption method
    static byte[] encrypt(byte[] data, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    // Decryption method
    static byte[] decrypt(byte[] data, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(data);
    }
}
