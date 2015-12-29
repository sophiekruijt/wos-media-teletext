package nl.wos.teletekst.ejb;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import nl.wos.teletekst.core.TeletextUpdatePackage;
import nl.wos.teletekst.util.Configuration;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

@Singleton
public class PhecapConnector {
    private static final Logger log = Logger.getLogger(PhecapConnector.class.getName());

    private String server = "10.35.0.80";
    private int port = 21;
    private String user = "FTP_2016";
    private String pass = "qk34&#sdfhk123()%";
    private String path = "/PheTxtServer/Transfer/FTP_2016/";

    private FTPClient ftpClient = new FTPClient();

    @Lock(LockType.WRITE)
    public void uploadFilesToTeletextServer(TeletextUpdatePackage updatePackage)
    {
        if(Configuration.DEBUG) {
            return;
        }
        Path folder = Paths.get(updatePackage.getFolder());
        log.info("Start new upload for teletext update package: " + updatePackage.toString());

        try {
            ftpClient.connect(server, port);
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setConnectTimeout(5);
            ftpClient.setFileType(FTP.ASCII_FILE_TYPE);
            ftpClient.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);
            ftpClient.changeWorkingDirectory(this.path);

            List<File> filesInFolder = Files.walk(folder)
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .collect(Collectors.toList());

            for (File file : filesInFolder) {
                if (!file.getName().equals("update.sem")) {
                    FileInputStream fis = new FileInputStream(file.getCanonicalPath());
                    ftpClient.storeFile(file.getName(), fis);
                    fis.close();
                    Files.delete(Paths.get(file.getCanonicalPath()));
                }
            }

            InputStream emptyFileInputStream = new ByteArrayInputStream("".getBytes());
            ftpClient.storeFile("update.sem", emptyFileInputStream);
            emptyFileInputStream.close();

            ftpClient.logout();
            Files.deleteIfExists(folder);
        } catch (Exception e) {
            e.printStackTrace();
            log.severe(e.toString());
        }
        log.info("All files are uploaded. Update package " + updatePackage.getId() + " id finished.");
    }
}
