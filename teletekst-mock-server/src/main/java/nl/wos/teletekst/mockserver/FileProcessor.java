package nl.wos.teletekst.mockserver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileProcessor {

    private static FileProcessor fileProcessor = new FileProcessor( );
    private FileProcessor(){ }

    private Map<String, String> textFiles = new HashMap<>();
    private String controlDat = "";

    private ConfigurationParser configurationParser = ConfigurationParser.getInstance();

    public static FileProcessor getInstance( ) {
        return fileProcessor;
    }

    protected void addFile(String fileName, String fileText) {
        if(fileName.startsWith("text")) {
            // Text file with contents of a page is received
            textFiles.put(fileName, fileText);
            System.out.println("The file: " + fileName + " is added to the teletext mock server.");
        }
        else if(fileName.equals("control.dat")) {
            controlDat = fileText;
            System.out.println("The file: " + fileName + " is received with instructions for the teletext server");
            configurationParser.parseConfiguration(fileText);
        }
        else if(fileName.equals("update.sem")) {
            System.out.println("update.sem received. Execute and send instructions to teletext inserter and remove all received files.");
            configurationParser.parseConfiguration(fileText);
            controlDat = "";
            textFiles.clear();
        }
    }
}