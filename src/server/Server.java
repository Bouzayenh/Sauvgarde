package server;// File: Server.java
import common.FileBackup;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.Base64;

public class Server {
    private static final int PORT = 8080;
    static final String BACKUP_DIR = "backups";

    public static void main(String[] args) {
        new File(BACKUP_DIR).mkdir();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler implements Runnable {
    private Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {
            FileBackup backup;
            while ((backup = (FileBackup) in.readObject()) != null) {
                saveBackup(backup);
            }
        } catch (EOFException ignored) {
            // End of stream reached
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void saveBackup(FileBackup backup) {
        try {
            byte[] fileContent = Base64.getDecoder().decode(backup.getFileContent());
            Path backupPath = Paths.get(Server.BACKUP_DIR, backup.getFileName());
            Files.write(backupPath, fileContent);
            System.out.println("Backup created for: " + backup.getFileName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
