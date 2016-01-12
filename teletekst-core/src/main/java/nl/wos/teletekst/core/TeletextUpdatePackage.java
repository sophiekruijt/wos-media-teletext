package nl.wos.teletekst.core;

import nl.wos.teletekst.util.Configuration;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class TeletextUpdatePackage {

    private static int teletextUpdatePackageId = 1;

    private static final Logger log = Logger.getLogger(TeletextUpdatePackage.class.getName());
    private StringBuilder teletextCommands = new StringBuilder();
    private final String folderName;
    private int packageId;

    private List<TeletextPage> teletextPages = new ArrayList<>();

    public TeletextUpdatePackage() {
        packageId = teletextUpdatePackageId++;
        folderName = "/apps/wos/" + this.packageId + "/";

        try {
            File f = new File(folderName);
            if (f.exists()) {
                FileUtils.deleteDirectory(f);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        new File(folderName).mkdirs();
    }

    public void addTeletextPage(TeletextPage teletextPage) {
        teletextPages.add(teletextPage);
    }

    /***
     * Add a remove command for a specific range of teletext page.
     * @param start
     * @param end
     */
    public void addRemovePagesTask (int start, int end) {
        StringBuilder builder = new StringBuilder();
        for(int pageNumber=start; pageNumber<end; pageNumber++) {
            builder.append("["+pageNumber+".*]\n");
        }
        teletextCommands.append(builder.toString());
    }

    /***
     * Generate all textfiles in data directory for this set of commands and teletextpages.
     * This will also result in locking the teletext page since the configuration string with teletext inserter commands will be generated.
     */
    public void generateTextFiles() {
        for(TeletextPage teletextPage : teletextPages) {
            for (TeletextSubpage page : teletextPage.getTeletextSubPages()) {
                writePageToFile(page, teletextPage);
            }
            teletextCommands.append(teletextPage.getConfigurationString());
        }
        generateConfigurationFile(teletextCommands);
    }

    public String getFolder() {
        return this.folderName;
    }

    private void writePageToFile(TeletextSubpage page, TeletextPage p)
    {
        int pageNumber = p.getTeletextPagenumber();
        int subPageNumber = p.getSubpageNumber(page);

        String textFileName = "text-" + pageNumber + "-" + subPageNumber + ".txt";

        try {
            File file = new File(folderName + textFileName);
            file.createNewFile();
            FileUtils.writeStringToFile(file, page.getPageText());

        } catch (IOException e) {
            log.severe(e.toString());
            e.printStackTrace();
        }
    }

    private void generateConfigurationFile(StringBuilder teletextConfiguration)
    {
        try {
            File file = new File(folderName + "control.dat");
            file.createNewFile();
            FileUtils.writeStringToFile(file, teletextConfiguration.toString());
        } catch (Exception e) {
            log.severe(e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (TeletextPage page : teletextPages) {
            result.append("Pagenumber: " + page.getTeletextPagenumber() + " (" + page.getTeletextSubPages().size() + " subpages)]\n");
        }
        return result.toString();
    }

    public int getId() {
        return this.packageId;
    }
}