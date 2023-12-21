package server;

import common.FileBackup;

import javax.net.ssl.SSLSocket;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;

class ClientHandler extends Thread {
    private SSLSocket sslSocket;

    public ClientHandler(SSLSocket socket) {
        this.sslSocket = socket;
    }

    @Override
    public void run() {
        try (ObjectInputStream in = new ObjectInputStream(sslSocket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(sslSocket.getOutputStream())) {

            FileBackup backup;
            while (true) {
                try {
                    backup = (FileBackup) in.readObject();
                    if (backup == null) break;
                    saveBackup(backup, out);
                } catch (EOFException e) {
                    System.out.println("Client disconnected.");
                    break; // Exit the loop if client disconnects
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
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
        String relativePath = backup.getFileName();
        Path backupPath = Paths.get(Server.BACKUP_DIR, relativePath).normalize();

        // Ensure the path is still within the backup directory
        if (!backupPath.startsWith(Paths.get(Server.BACKUP_DIR))) {
            out.writeObject("Invalid file path: " + relativePath);
            return;
        }

        // Create directories if they don't exist
        Files.createDirectories(backupPath.getParent());

        byte[] fileContent = Base64.getDecoder().decode(backup.getFileContent());
        Files.write(backupPath, fileContent);
        out.writeObject("Backup created for: " + relativePath);
        out.flush();
    }



}