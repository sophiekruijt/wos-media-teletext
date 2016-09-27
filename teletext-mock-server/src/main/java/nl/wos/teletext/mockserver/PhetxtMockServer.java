package nl.wos.teletext.mockserver;

import nl.wos.teletext.util.ConfigurationLoader;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PhetxtMockServer {
    private static final Logger log = Logger.getLogger(String.valueOf(PhetxtMockServer.class));
    private final ExecutorService clientProcessingPool1 = Executors.newFixedThreadPool(10);
    private final ExecutorService clientProcessingPool2 = Executors.newFixedThreadPool(10);

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
                    clientProcessingPool1.submit(new SendPageTask(clientSocket));
                }
            } catch (IOException ex) {
                log.log(Level.SEVERE, "Exception occured", ex);
                return;
            }
        };
        Thread serverThread = new Thread(serverTask);
        serverThread.start();


        Runnable dataRetrievalTask= () -> {
            try {
                ServerSocket serverSocket = new ServerSocket(mockServerPort+1);

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    clientProcessingPool2.submit(new GetTeletextDataTask(clientSocket));
                }
            } catch (IOException ex) {
                log.log(Level.SEVERE, "Exception occured", ex);
                return;
            }
        };
        Thread dataRetrievalThread = new Thread(dataRetrievalTask);
        dataRetrievalThread.start();
    }

    private class SendPageTask implements Runnable {
        private final Logger log = Logger.getLogger(String.valueOf(SendPageTask.class));
        private final Socket clientSocket;

        private SendPageTask(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                FileProcessor fileProcessor = FileProcessor.getInstance();
                Teletext teletext = Teletext.getInstance();

                DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                int numberOfTextfilesToReceive = in.readInt();
                log.log(Level.FINE, "Got a new client who's going to send " + numberOfTextfilesToReceive + " files.");

                for(int i=0; i<numberOfTextfilesToReceive; i++) {
                    String fileName = in.readUTF();
                    String fileText = in.readUTF();

                    fileProcessor.addFile(fileName, fileText);

                    log.log(Level.FINE, "Received file: " + fileName);
                }

                List<TeletextPage> teletextPagesToBroadcast = fileProcessor.getTeletextPagesToBroadcast();
                teletextPagesToBroadcast.forEach(teletext::addTeletextPage);

                clientSocket.close();
            } catch (Exception ex) {
                log.log(Level.SEVERE, "Exception occured", ex);
            }
        }
    }

    private class GetTeletextDataTask implements Runnable {
        private final Logger log = Logger.getLogger(String.valueOf(GetTeletextDataTask.class));
        private final Socket clientSocket;

        private GetTeletextDataTask(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                Teletext teletext = Teletext.getInstance();

                DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                int pageNumber = in.readInt();
                int subPageNumber = in.readInt();
                int lineNumber = in.readInt();
                log.log(Level.FINE, "Got a new client who wants to know the content of page " + pageNumber);

                String lineText = teletext.getTextLine(pageNumber, subPageNumber, lineNumber);

                DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
                out.writeUTF(lineText);
                out.close();
                clientSocket.close();
            } catch (Exception ex) {
                log.log(Level.SEVERE, "Exception occured", ex);
            }
        }
    }
}