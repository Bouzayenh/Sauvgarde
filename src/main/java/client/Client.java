package client;

import common.FileBackup;
import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.security.KeyStore;
import java.util.*;
import java.util.stream.Stream;
import java.util.Base64;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8080;
    private static final String PARAMETER_FILE_NAME = "C:\\Users\\bh13h\\IdeaProjects\\SauvgardeJ\\src\\main\\java\\client\\parameters.txt";

    public static void main(String[] args) throws Exception {
        Set<String> allowedExtensions = loadAllowedExtensions();
        Scanner scanner = new Scanner(System.in);

        // Establish SSL connection
        SSLSocket sslSocket = createSSLSocket();
        ObjectOutputStream out = new ObjectOutputStream(sslSocket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(sslSocket.getInputStream());


        try {
            while (true) {
                System.out.print("Enter the folder path to backup or type 'exit' to quit: ");
                String folderPathStr = scanner.nextLine();

                if ("exit".equalsIgnoreCase(folderPathStr)) {
                    break;
                }

                Path folderPath = Paths.get(folderPathStr);
                if (!Files.isDirectory(folderPath)) {
                    System.out.println("The specified path is not a directory.");
                    continue;
                }

                backupFiles(folderPath, allowedExtensions, out, in);

            }
        } finally {
            System.out.println("Closing connection...");
            out.close();
            sslSocket.close();
            scanner.close();
        }
    }

    private static void backupFiles(Path folderPath, Set<String> allowedExtensions, ObjectOutputStream out, ObjectInputStream in) {
        try (Stream<Path> paths = Files.walk(folderPath)) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> hasAllowedExtension(path, allowedExtensions))
                    .forEach(path -> {
                        try {
                            byte[] fileContent = Files.readAllBytes(path);
                            String encodedContent = Base64.getEncoder().encodeToString(fileContent);
                            out.writeObject(new FileBackup(path.getFileName().toString(), encodedContent));
                            String confirmation = (String) in.readObject();  // Read confirmation for each file
                            System.out.println(confirmation);
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static Set<String> loadAllowedExtensions() {
        Set<String> extensions = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(PARAMETER_FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                extensions.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return extensions;
    }

    private static boolean hasAllowedExtension(Path path, Set<String> allowedExtensions) {
        String fileName = path.getFileName().toString();
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            String extension = fileName.substring(i + 1);
            return allowedExtensions.contains(extension);
        }
        return false;
    }

    private static SSLSocket createSSLSocket() throws Exception {
        // Load the truststore
        URL truststoreResource = Client.class.getClassLoader().getResource("SSL/client.truststore.jks");
        if (truststoreResource == null) {
            throw new FileNotFoundException("Le fichier 'truststore.jks' est introuvable.");
        }
        String truststorePassword = "furryfurry";

        KeyStore ts = KeyStore.getInstance("JKS");
        ts.load(truststoreResource.openStream(), truststorePassword.toCharArray());

        // Initialize the TrustManagerFactory
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ts);

        // Initialize SSLContext
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);

        // Create and return SSLSocket
        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        return (SSLSocket) sslSocketFactory.createSocket(SERVER_ADDRESS, SERVER_PORT);
    }
}
