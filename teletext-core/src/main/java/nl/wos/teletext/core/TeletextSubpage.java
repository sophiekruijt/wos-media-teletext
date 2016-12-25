package nl.wos.teletext.core;

import nl.wos.teletext.util.TextOperations;

import java.util.List;

public class TeletextSubpage {
    private String[] textLines = new String[25];
    private String layoutTemplateFileName;

    public String getPageText() {
        StringBuilder result = new StringBuilder();
        for (String line : textLines) {
            result.append((line != null) ? line + "\n" : "\n");
        }
        return result.toString();
    }

    public void setTextOnLine(int line, String text) {
        String checkedText = TextOperations.removeIllegalCharacters(text);
        if (line < textLines.length) {
            textLines[line] = checkedText;
        }
    }

    public String getLayoutTemplateFileName() {
        return layoutTemplateFileName;
    }

    public void setLayoutTemplateFileName(String layoutTemplateFileName) {
        this.layoutTemplateFileName = layoutTemplateFileName;
    }

    public void addText(List<String> text) {
        if(textLines.length < text.size()) {
            textLines = new String[text.size()];
        }

        for(int i=0; i<text.size(); i++) {
            textLines[i] = text.get(i);
        }
    }
}