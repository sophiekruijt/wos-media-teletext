package nl.wos.teletekst.mockserver;

public class TeletextCommand {
    private String command;
    private int pageNumber;
    private String templateFileName;
    private String description;
    private String[] promps = new String[4];
    private Integer[] links = new Integer[4];

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

    public Integer[] getLinks() {
        return links;
    }

    public void setLinks(Integer[] links) {
        this.links = links;
    }
}
