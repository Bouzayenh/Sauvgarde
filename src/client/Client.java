package client;// File: Client.java
import common.FileBackup;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.Base64;
import java.util.Scanner;
import java.util.stream.Stream;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the folder path to backup: ");
        String folderPathStr = scanner.nextLine();

        Path folderPath = Paths.get(folderPathStr);
        if (!Files.isDirectory(folderPath)) {
            System.out.println("The specified path is not a directory.");
            return;
        }

        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

            try (Stream<Path> paths = Files.walk(folderPath)) {
                paths.filter(Files::isRegularFile).forEach(path -> {
                    try {
                        byte[] fileContent = Files.readAllBytes(path);
                        String encodedContent = Base64.getEncoder().encodeToString(fileContent);
                        out.writeObject(new FileBackup(path.getFileName().toString(), encodedContent));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

