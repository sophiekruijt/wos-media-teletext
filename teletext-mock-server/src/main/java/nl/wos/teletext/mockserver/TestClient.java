package nl.wos.teletext.mockserver;

import nl.wos.teletext.util.ConfigurationLoader;

import java.io.*;
import java.net.Socket;
import java.util.Properties;

public class TestClient {
    private Properties properties = new ConfigurationLoader().getProperties();

    private String mockServerHost = properties.getProperty("mockServerHost");
    private int mockServerPort = Integer.parseInt(properties.getProperty("mockServerPort"));

    public static void main(String[] args) {
        new TestClient();
    }

    public TestClient() {
        System.out.println("TestClient started");

        try (Socket sock = new Socket(mockServerHost, mockServerPort)) {
            OutputStream out = sock.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(out);
            dataOutputStream.writeInt(2);
            dataOutputStream.writeUTF("control.dat");
            dataOutputStream.writeUTF("[719.*]\n" +
                    "\n" +
                    "[719.0000]\n" +
                    "TemplateFilename=template-treinen.tpg\n" +
                    "TextFilename=text-719-0.txt\n" +
                    "Descr= Automatische pagina\n" +
                    "prompts=Nieuws Sport TV Weer\n" +
                    "links=101 600 200 700");
            dataOutputStream.writeUTF("update.sem");
            dataOutputStream.writeUTF("");
            dataOutputStream.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
