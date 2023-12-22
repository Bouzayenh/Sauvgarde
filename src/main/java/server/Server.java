package server;

import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.util.Scanner;
import java.util.stream.Stream;
import javax.crypto.SecretKey;

public class Server {
    private static final int PORT = 8080;
    static final String BACKUP_DIR = "backups";

    public static void main(String[] args) throws Exception {
        new File(BACKUP_DIR).mkdir();
        SSLContext sslContext = setupSSLContext();
        SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
        SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(PORT);

        System.out.println("SSL Server started on port " + PORT);
        new Thread(() -> acceptClients(sslServerSocket)).start();
        handleConsoleCommands();
    }

    private static void acceptClients(SSLServerSocket sslServerSocket) {
        try {
            while (true) {
                SSLSocket sslSocket = (SSLSocket) sslServerSocket.accept();
                System.out.println("Client connected: " + sslSocket.getRemoteSocketAddress());
                new ClientHandler(sslSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleConsoleCommands() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String command = scanner.nextLine();
            if ("decrypt".equalsIgnoreCase(command)) {
                processFiles(Server.BACKUP_DIR, false); // Decrypt files
                System.out.println("Decrypted all files.");
            } else if ("encrypt".equalsIgnoreCase(command)) {
                processFiles(Server.BACKUP_DIR, true); // Encrypt files
                System.out.println("Encrypted all files.");
            }
        }
    }

    private static void processFiles(String directory, boolean encrypt) {
        try (Stream<Path> paths = Files.walk(Paths.get(directory))) {
            paths.filter(Files::isRegularFile).forEach(file -> {
                try {
                    byte[] fileContent = Files.readAllBytes(file);
                    SecretKey secretKey = ConfigLoader.getSecretKey();
                    byte[] processedContent = encrypt ? ClientHandler.encrypt(fileContent, secretKey) : ClientHandler.decrypt(fileContent, secretKey);
                    Files.write(file, processedContent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static SSLContext setupSSLContext() throws Exception {
        URL keystoreResource = Server.class.getClassLoader().getResource("SSL/server.keystore.jks");
        if (keystoreResource == null) {
            throw new FileNotFoundException("Le fichier 'server.keystore.jks' est introuvable.");
        }
        String keystorePassword = "furryfurry";

        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(keystoreResource.openStream(), keystorePassword.toCharArray());

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, keystorePassword.toCharArray());

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), null, null);
        return sslContext;
    }
}
