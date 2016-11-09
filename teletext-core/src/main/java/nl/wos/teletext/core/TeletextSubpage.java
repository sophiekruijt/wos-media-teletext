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
        String checkedText = TextOperations.removeSpecialCharactersAndHTML(text);
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

    public void addText(List<String> programAndScoresPageText) {
        if(textLines.length < programAndScoresPageText.size()) {
            textLines = new String[programAndScoresPageText.size()];
        }

        for(int i=0; i<programAndScoresPageText.size(); i++) {
            textLines[i] = programAndScoresPageText.get(i);
        }
    }
}