package nl.wos.teletext.ejb;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
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
import nl.wos.teletext.util.ConfigurationLoader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

@Singleton
public class PhecapConnector {
    private static final Logger log = Logger.getLogger(PhecapConnector.class.getName());

    private final FTPClient ftpClient = new FTPClient();
    private final Properties properties = new ConfigurationLoader().getProperties();

    private boolean debugMode = Boolean.parseBoolean(properties.getProperty("debugMode"));
    private final String teletextServerHost = properties.getProperty("teletextServerHost");
    private final String teletextServerUser = properties.getProperty("teletextServerUser");
    private final String teletextServerPassword = properties.getProperty("teletextServerPassword");
    private final String teletextServerUploadPath = properties.getProperty("teletextServerUploadPath");
    private final int teletextServerPort = Integer.parseInt(properties.getProperty("teletextServerPort"));

    private String mockServerHost = properties.getProperty("mockServerHost");
    private int mockServerPort = Integer.parseInt(properties.getProperty("mockServerPort"));

    @Lock(LockType.WRITE)
    public void uploadFilesToTeletextServer(TeletextUpdatePackage updatePackage)
    {
        // Accidental upload to production is also prevented by the need to have an active VPN connection to production.
        if(debugMode) {
            log.info("Debug mode is enabled. Teletext update will be sent to mock server." + updatePackage.getFolder());
            sendFilesToMockServer(updatePackage);
        }
        else {
            ftpUploadToProduction(updatePackage);
        }
    }

    private void ftpUploadToProduction(TeletextUpdatePackage updatePackage) {
        Path folder = Paths.get(updatePackage.getFolder());
        log.info("Start new upload for teletext update package: " + updatePackage.toString());

        try {
            connectAndInitializeFtpClient();
            uploadTextFiles(folder);
            uploadUpdateSem();

            ftpClient.logout();
            Files.deleteIfExists(folder);
        } catch (Exception ex) {
            log.log(Level.SEVERE, "Exception occured", ex);
        }
    }

    private void sendFilesToMockServer(TeletextUpdatePackage updatePackage) {
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

    private void uploadUpdateSem() throws IOException {
        InputStream emptyFileInputStream = new ByteArrayInputStream("".getBytes());
        ftpClient.storeFile("update.sem", emptyFileInputStream);
        emptyFileInputStream.close();
    }

    private void connectAndInitializeFtpClient() throws IOException {
        ftpClient.connect(teletextServerHost, teletextServerPort);
        ftpClient.login(teletextServerUser, teletextServerPassword);
        ftpClient.enterLocalActiveMode();
        ftpClient.setConnectTimeout(5);
        ftpClient.setFileType(FTP.ASCII_FILE_TYPE);
        ftpClient.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);
        ftpClient.changeWorkingDirectory(teletextServerUploadPath);
    }

    private void uploadTextFiles(Path folder) throws IOException, InterruptedException {
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
}
