package nl.wos.teletext.components;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import nl.wos.teletext.core.TeletextUpdatePackage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.stereotype.Component;

@Component
public class PhecapConnector {
    private static final Logger log = Logger.getLogger(PhecapConnector.class.getName());

    private final Properties properties = PropertyManager.getProperties();

    private FTPClient ftpClient;

    private boolean debugMode = Boolean.parseBoolean(properties.getProperty("debugMode"));
    private final String teletextServerHost = properties.getProperty("teletextServerHost");
    private final String teletextServerUser = properties.getProperty("teletextServerUser");
    private final String teletextServerPassword = properties.getProperty("teletextServerPassword");
    private final String teletextServerUploadPath = properties.getProperty("teletextServerUploadPath");
    private final int teletextServerPort = Integer.parseInt(properties.getProperty("teletextServerPort"));
    private final int connectTimeOut = Integer.parseInt(properties.getProperty("connectTimeOut"));

    private String mockServerHost = properties.getProperty("mockServerHost");
    private int mockServerPort = Integer.parseInt(properties.getProperty("mockServerPort"));

    public synchronized void uploadFilesToTeletextServer(TeletextUpdatePackage updatePackage)
    {
        // Accidental upload to production is also prevented by the need to have an active VPN connection to production.
        if(debugMode) {
            log.info("Debug mode is enabled. Teletext update will be sent to mock server." + updatePackage.getFolder());
            sendFilesToMockServer(updatePackage);
        }
        else {
            ftpUploadToProduction(updatePackage);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void ftpUploadToProduction(TeletextUpdatePackage updatePackage) {
        Path folder = Paths.get(updatePackage.getFolder());
        log.info("Start new upload for teletext update package: " + updatePackage.toString());

        try {
            uploadFilesInFolder(folder);

            Files.deleteIfExists(folder);
        } catch (Exception ex) {
            log.log(Level.SEVERE, "Exception occured", ex);
        }
    }

    private synchronized void sendFilesToMockServer(TeletextUpdatePackage updatePackage) {
        if(!mockServerOnline()) {
            log.info("Mock server not available, ignore teletext update.");
            return;
        }

        Path folder = Paths.get(updatePackage.getFolder());
        try {
            sendTextFilesToMock(folder);
        }
        catch (Exception ex) {
            log.log(Level.SEVERE, "Exception occured", ex);
        }
    }

    private boolean mockServerOnline() {
        try {
            Socket socket = new Socket(mockServerHost, mockServerPort);
            return socket.isConnected();
        } catch (IOException e) {
        }
        return false;
    }

    private void uploadFilesInFolder(Path folder) throws IOException {
        ftpClient = new FTPClient();
        ftpClient.setConnectTimeout(connectTimeOut);
        ftpClient.connect(teletextServerHost, teletextServerPort);

        try {
            int reply;
            ftpClient.connect(teletextServerHost, teletextServerPort);
            System.out.println("Connected to " + teletextServerHost + ".");
            System.out.print(ftpClient.getReplyString());

            reply = ftpClient.getReplyCode();
            if(!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                System.err.println("FTP server refused connection.");
            }

            ftpClient.login(teletextServerUser, teletextServerPassword);
            ftpClient.setFileType(FTP.ASCII_FILE_TYPE);
            ftpClient.enterLocalActiveMode();
            ftpClient.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);
            ftpClient.changeWorkingDirectory(teletextServerUploadPath);

            List<File> filesInFolder = Files.walk(folder).filter(Files::isRegularFile).map(Path::toFile).collect(Collectors.toList());

            for (File file : filesInFolder) {
                if (!file.getName().equals("update.sem")) {
                    FileInputStream fis = new FileInputStream(file.getCanonicalPath());
                    ftpClient.storeFile(file.getName(), fis);
                    Thread.sleep(25);
                    fis.close();
                    Files.delete(Paths.get(file.getCanonicalPath()));
                }
            }

            InputStream emptyFileInputStream = new ByteArrayInputStream("".getBytes());
            ftpClient.storeFile("update.sem", emptyFileInputStream);
            emptyFileInputStream.close();

            ftpClient.logout();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch(IOException ioe) {
                    log.warning(ioe.toString());
                }
            }
        }
    }

    private void sendTextFilesToMock(Path folder) throws IOException, InterruptedException {
        List<File> filesInFolder = Files.walk(folder).filter(Files::isRegularFile).map(Path::toFile).collect(Collectors.toList());

        try (Socket sock = new Socket(mockServerHost, mockServerPort)) {
            DataOutputStream dataOutputStream = new DataOutputStream(sock.getOutputStream());
            dataOutputStream.writeInt(filesInFolder.size() + 1);

            filesInFolder.stream().filter(file -> !file.getName().equals("update.sem")).forEach(file -> {
                try {
                    // Write filename
                    dataOutputStream.writeUTF(file.getName());
                    // Followed by contents of file
                    dataOutputStream.writeUTF(FileUtils.readFileToString(file));
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

            });
            dataOutputStream.writeUTF("update.sem");
            dataOutputStream.writeUTF("");
            dataOutputStream.close();
        }
        catch (Exception ex) {
            log.log(Level.SEVERE, "Exception occured", ex);
        }
    }

    public boolean checkConnection() {
        ftpClient = new FTPClient();
        ftpClient.setConnectTimeout(connectTimeOut);

        try {
            int reply;
            ftpClient.connect(teletextServerHost, teletextServerPort);
            System.out.println("Connected to " + teletextServerHost + ".");
            System.out.print(ftpClient.getReplyString());

            reply = ftpClient.getReplyCode();
            if(!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                return false;
            }

            ftpClient.logout();
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if(ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch(IOException ioe) {
                    log.warning(ioe.toString());
                }
            }
        }

    }
}
