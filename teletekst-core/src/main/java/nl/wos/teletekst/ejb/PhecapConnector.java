package nl.wos.teletekst.ejb;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.inject.Inject;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import nl.wos.teletekst.core.TeletextUpdatePackage;
import nl.wos.teletekst.entity.PropertyManager;
import nl.wos.teletekst.util.Configuration;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

@Singleton
public class PhecapConnector {
    private static final Logger log = Logger.getLogger(PhecapConnector.class.getName());

    @Inject private PropertyManager propertyManager;
    private final FTPClient ftpClient = new FTPClient();

    @Lock(LockType.WRITE)
    public void uploadFilesToTeletextServer(TeletextUpdatePackage updatePackage)
    {
        if (Configuration.DEBUG_MODE) {
            log.info("Debug mode is enabled. The following teletext update package will not be uploaded to the " +
                    "teletext inserter:\n" + updatePackage.toString());
            return;
        }

        Path folder = Paths.get(updatePackage.getFolder());
        log.info("Start new upload for teletext update package: " + updatePackage.toString());

        try {
            connectAndInitializeFtpClient();
            uploadTextFiles(folder);
            uploadUpdateSem();

            ftpClient.logout();
            Files.deleteIfExists(folder);
        } catch (Exception e) {
            e.printStackTrace();
            log.severe(e.toString());
        }
    }

    private void uploadUpdateSem() throws IOException {
        InputStream emptyFileInputStream = new ByteArrayInputStream("".getBytes());
        ftpClient.storeFile("update.sem", emptyFileInputStream);
        emptyFileInputStream.close();
    }

    private void connectAndInitializeFtpClient() throws IOException {
        ftpClient.connect(Configuration.IP_TELETEXT_SERVER, Configuration.PORT_TELETEXT_SERVER);
        ftpClient.login(Configuration.FTP_USER_TELETEXT_SERVER, Configuration.FTP_PASSWORD_TELETEXT_SERVER);
        ftpClient.enterLocalActiveMode();
        ftpClient.setConnectTimeout(5);
        ftpClient.setFileType(FTP.ASCII_FILE_TYPE);
        ftpClient.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);
        ftpClient.changeWorkingDirectory(Configuration.FTP_UPLOAD_PATH_TELETEXT_SERVER);
    }

    private void uploadTextFiles(Path folder) throws IOException, InterruptedException {
        List<File> filesInFolder = Files.walk(folder)
            .filter(Files::isRegularFile)
            .map(Path::toFile)
            .collect(Collectors.toList());

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
}
