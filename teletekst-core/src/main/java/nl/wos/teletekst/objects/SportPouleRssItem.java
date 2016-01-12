package nl.wos.teletekst.objects;

import java.util.List;

public class SportPouleRssItem {
    private String title;
    private int textPageNumber;
    private List<List<String>> standenData;
    private List<List<String>> uitslagenData;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTextPageNumber() {
        return textPageNumber;
    }

    public void setTextPageNumber(int textPageNumber) {
        this.textPageNumber = textPageNumber;
    }

    public List<List<String>> getStandenData() {
        return standenData;
    }

    public void setStandenData(List<List<String>> standenData) {
        this.standenData = standenData;
    }

    public List<List<String>> getUitslagenData() {
        return uitslagenData;
    }

    public void setUitslagenData(List<List<String>> uitslagenData) {
        this.uitslagenData = uitslagenData;
    }
}