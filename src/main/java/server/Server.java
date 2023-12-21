package server;

import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.security.KeyStore;

public class Server {
    private static final int PORT = 8080;
    static final String BACKUP_DIR = "backups";

    public static void main(String[] args) throws Exception {
        new File(BACKUP_DIR).mkdir();
        SSLContext sslContext = setupSSLContext();
        SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
        SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(PORT);

        System.out.println("SSL Server started on port " + PORT);

        while (true) {
            SSLSocket sslSocket = (SSLSocket) sslServerSocket.accept();
            System.out.println("Client connected: " + sslSocket.getRemoteSocketAddress());
            new ClientHandler(sslSocket).start();
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
