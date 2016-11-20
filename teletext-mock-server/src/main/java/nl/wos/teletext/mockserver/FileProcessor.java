package nl.wos.teletext.mockserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class FileProcessor {
    private static final Logger log = Logger.getLogger(FileProcessor.class.getName());

    private static FileProcessor fileProcessor = new FileProcessor();
    private Map<String, String> textFiles = new HashMap<>();
    private String controlDat = "";
    private List<TeletextPage> teletextPages = new ArrayList<>();
    private ConfigurationParser configurationParser = ConfigurationParser.getInstance();

    private final String CONTROL_DAT = "control.dat";
    private final String UPDATE_SEM = "update.sem";

    private FileProcessor(){ }

    public static FileProcessor getInstance( ) {
        return fileProcessor;
    }

    public List<TeletextPage> getTeletextPagesToBroadcast() {
        return teletextPages;
    }

    protected void addFile(String fileName, String fileText) {

        if(CONTROL_DAT.equals(fileName)) {
            controlDat = fileText;
            log.info("The file: " + fileName + " is received with instructions for the teletext server");
            configurationParser.parseConfiguration(fileText);
            return;
        }
        else if(UPDATE_SEM.equals(fileName)) {
            teletextPages.clear();
            log.info("update.sem received. Execute and send instructions to teletext inserter and remove all received files.");

            configurationParser.parseConfiguration(controlDat).stream().filter(command -> command.getCommand().equals("addPage")).forEach(command -> {
                TeletextPage page = new TeletextPage();
                page.setPageNumber(command.getPageNumber());
                page.setSubPageNumber(command.getSubPageNumber());

                String[] fasttext = command.getLinks();
                String[] prompts = command.getPrompts();
                page.setFastText1(Integer.parseInt(fasttext[0]));
                page.setFastText2(Integer.parseInt(fasttext[1]));
                page.setFastText3(Integer.parseInt(fasttext[2]));
                page.setFastText4(Integer.parseInt(fasttext[3]));

                page.setFastTextLabel1(prompts[0]);
                page.setFastTextLabel2(prompts[1]);
                page.setFastTextLabel3(prompts[2]);
                page.setFastTextLabel4(prompts[3]);

                String content = textFiles.get(command.getTextFileName());
                page.setTextLines(content.split("\n"));

                teletextPages.add(page);
            });

            controlDat = "";
            textFiles.clear();
            return;
        }

        textFiles.put(fileName, fileText);
        log.info("The file: " + fileName + " is added to the teletext mock server.");
    }
}