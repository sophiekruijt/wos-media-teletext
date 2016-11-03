package nl.wos.teletext.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TextClient {
    private static final Logger log = Logger.getLogger(String.valueOf(TextClient.class));
    private Properties properties = new ConfigurationLoader().getProperties();

    private String mockServerHost = properties.getProperty("mockServerHost");
    private int mockServerPort = Integer.parseInt(properties.getProperty("mockServerPort"));

    public static void main(String[] args) {
        new TextClient();
    }

    public String getTeletextLine(int page, int subpage, int line) {
        try (Socket sock = new Socket(mockServerHost, mockServerPort+1)) {
            OutputStream out = sock.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(out);
            DataInputStream dataInputStream = new DataInputStream(sock.getInputStream());

            dataOutputStream.writeInt(page);
            dataOutputStream.writeInt(subpage);
            dataOutputStream.writeInt(line);

            String textLine = dataInputStream.readUTF();

            dataInputStream.close();
            dataOutputStream.close();

            return textLine;
        }
        catch (Exception ex) {
            log.log(Level.SEVERE, "Exception occured", ex);
        }

        return "Error";
    }

    public void resetServer() {
        try (Socket sock = new Socket(mockServerHost, mockServerPort+1)) {
            OutputStream out = sock.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(out);
            DataInputStream dataInputStream = new DataInputStream(sock.getInputStream());

            dataOutputStream.writeInt(999);
            dataOutputStream.writeInt(999);
            dataOutputStream.writeInt(999);

            dataInputStream.close();
            dataOutputStream.close();

        }
        catch (Exception ex) {
            log.log(Level.SEVERE, "Exception occured", ex);
        }
    }
}
