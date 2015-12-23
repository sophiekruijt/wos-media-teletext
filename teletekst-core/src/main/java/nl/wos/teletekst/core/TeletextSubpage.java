package nl.wos.teletekst.core;

public class TeletextSubpage {
    private String[] textLines = new String[25];
    private String layoutTemplateFileName;

    public TeletextSubpage() {
    }

    public String getPageText() {
        String result = "";

        for (String line : textLines) {
            result += line + "\n";
        }

        return result;
    }

    public void setTextOnLine(int line, String text) {
        if (line < textLines.length) {
            textLines[line] = text;
        }
    }

    public String getPageLineTextAt(int line) {
        return (textLines[line] != null) ? textLines[line] : "";
    }

    public String getLayoutTemplateFileName() {
        return layoutTemplateFileName;
    }

    public void setLayoutTemplateFileName(String layoutTemplateFileName) {
        this.layoutTemplateFileName = layoutTemplateFileName;
    }
}