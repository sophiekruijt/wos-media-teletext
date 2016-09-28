package nl.wos.teletext.steps;

import cucumber.api.PendingException;
import cucumber.api.java8.En;

import java.io.*;
import java.net.Socket;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.fail;

public class MockServerSteps implements En {
    String mockServerHost = "127.0.0.1";

    public MockServerSteps() {
        Given("^a started mockserver on port (\\d+)$", (Integer port) -> {
            try {
                Socket socket = new Socket(mockServerHost, port);
                assertThat(socket.isConnected(), is(true));
            } catch (IOException e) {
                fail(e.toString());
            }
        });

        And("^I send testData to the mockserver on port (\\d+)$", (Integer port) -> {
            try {
                Socket socket = new Socket(mockServerHost, port);
                OutputStream out = socket.getOutputStream();
                InputStream in = socket.getInputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(out);

                dataOutputStream.writeInt(3);
                dataOutputStream.writeUTF("text-719-0.txt");
                dataOutputStream.writeUTF("Test content for page 719 \nsecond line on page 719");
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
            } catch (IOException e) {
                fail(e.toString());
            }
        });

        Then("^I can retrieve testData from the mockserver on port (\\d+)$", (Integer port) -> {
            try {
                Socket socket = new Socket(mockServerHost, port);
                OutputStream out = socket.getOutputStream();
                InputStream in = socket.getInputStream();
                DataInputStream dataInputStream = new DataInputStream(in);
                DataOutputStream dataOutputStream = new DataOutputStream(out);

                dataOutputStream.writeInt(719);
                dataOutputStream.writeInt(0);
                dataOutputStream.writeInt(1);

                String textLine = dataInputStream.readUTF();
                assertThat(textLine, is("second line on page 719"));

            } catch (Exception e) {
                fail(e.toString());
            }
        });
        And("^I wait (\\d+) seconds$", (Integer seconds) -> {
            try {
                Thread.sleep(seconds * 1000);
            } catch (InterruptedException e) {
                 fail(e.toString());
            };
        });
    }
}
