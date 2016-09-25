package nl.wos.teletext.mockserver;

import java.util.Arrays;

public class TeletextCommand {
    private String command;
    private int pageNumber;
    private String templateFileName;
    private String textFileName;
    private String description;
    private String[] promps = new String[4];
    private String[] links = new String[4];

    public TeletextCommand(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getTemplateFileName() {
        return templateFileName;
    }

    public void setTemplateFileName(String templateFileName) {
        this.templateFileName = templateFileName;
    }

    public String getTextFileName() {
        return textFileName;
    }

    public void setTextFileName(String textFileName) {
        this.textFileName = textFileName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getPromps() {
        return promps;
    }

    public void setPromps(String[] promps) {
        this.promps = promps;
    }

    public String[] getLinks() {
        return links;
    }

    public void setLinks(String[] links) {
        this.links = links;
    }

    @Override
    public String toString() {
        return "TeletextCommand{" +
                "command='" + command + '\'' +
                ", pageNumber=" + pageNumber +
                ", templateFileName='" + templateFileName + '\'' +
                ", textFileName='" + textFileName + '\'' +
                ", description='" + description + '\'' +
                ", promps=" + Arrays.toString(promps) +
                ", links=" + Arrays.toString(links) +
                '}';
    }
}
