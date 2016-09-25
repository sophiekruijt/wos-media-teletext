package nl.wos.teletext.mockserver;

import nl.wos.teletext.util.ConfigurationLoader;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PhetxtMockServer {
    private static final Logger log = Logger.getLogger(String.valueOf(PhetxtMockServer.class));
    final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);

    private Properties properties = new ConfigurationLoader().getProperties();

    private int mockServerPort = Integer.parseInt(properties.getProperty("mockServerPort"));

    public static void main(String[] args) {
        new PhetxtMockServer();
    }

    public PhetxtMockServer() {
        log.log(Level.INFO, "Start Phetxt mock server on port: " + mockServerPort);
        Runnable serverTask = () -> {
            try {
                ServerSocket serverSocket = new ServerSocket(mockServerPort);

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    clientProcessingPool.submit(new ClientTask(clientSocket));
                }
            } catch (IOException ex) {
                log.log(Level.SEVERE, "Exception occured", ex);
                return;
            }
        };
        Thread serverThread = new Thread(serverTask);
        serverThread.start();
    }

    private class ClientTask implements Runnable {
        private final Logger log = Logger.getLogger(String.valueOf(ClientTask.class));
        private final Socket clientSocket;

        private ClientTask(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                int numberOfTextfilesToReceive = in.readInt();
                log.log(Level.FINE, "Got a new client who's going to send " + numberOfTextfilesToReceive + " files.");

                for(int i=0; i<numberOfTextfilesToReceive; i++) {
                    String fileName = in.readUTF();
                    String fileText = in.readUTF();

                    FileProcessor.getInstance().addFile(fileName, fileText);

                    log.log(Level.FINE, "Received file: " + fileName);
                }

                clientSocket.close();
            } catch (Exception ex) {
                log.log(Level.SEVERE, "Exception occured", ex);
            }
        }
    }
}