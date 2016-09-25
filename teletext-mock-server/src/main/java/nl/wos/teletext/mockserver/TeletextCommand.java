package nl.wos.teletext.mockserver;

import java.util.Arrays;

public class TeletextCommand {
    private String command;
    private int pageNumber;
    private int subPageNumber;
    private String templateFileName;
    private String textFileName;
    private String description;
    private String[] prompts = new String[4];
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

    public int getSubPageNumber() {
        return subPageNumber;
    }

    public void setSubPageNumber(int subPageNumber) {
        this.subPageNumber = subPageNumber;
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

    public String[] getPrompts() {
        return prompts;
    }

    public void setPrompts(String[] prompts) {
        this.prompts = prompts;
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
                ", prompts=" + Arrays.toString(prompts) +
                ", links=" + Arrays.toString(links) +
                '}';
    }
}
