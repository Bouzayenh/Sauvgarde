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

    private void saveBackup(FileBackup backup, ObjectOutputStream out) {
        try {
            byte[] incomingFileContent = Base64.getDecoder().decode(backup.getFileContent());
            Path backupPath = Paths.get(Server.BACKUP_DIR, backup.getFileName());

            // Check if backup already exists
            if (Files.exists(backupPath)) {
                byte[] existingFileContent = Files.readAllBytes(backupPath);

                // Compare existing backup with incoming file
                if (Arrays.equals(incomingFileContent, existingFileContent)) {
                    out.writeObject("Backup already exists for: " + backup.getFileName());
                } else {
                    Files.write(backupPath, incomingFileContent); // Update backup
                    out.writeObject("Backup updated for: " + backup.getFileName());
                }
            } else {
                Files.write(backupPath, incomingFileContent); // Create new backup
                out.writeObject("New backup created for: " + backup.getFileName());
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}