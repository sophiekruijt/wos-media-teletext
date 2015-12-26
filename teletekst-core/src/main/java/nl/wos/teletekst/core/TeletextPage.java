package nl.wos.teletekst.core;

import java.util.ArrayList;

public class TeletextPage {
    private int pageNumber;
    private FastText fastText;
    private String teletextCommands;
    private ArrayList<TeletextSubpage> teletextSubPages = new ArrayList<>();
    private boolean locked;

    public ArrayList<TeletextSubpage> getTeletextSubPages() {
        return teletextSubPages;
    }

    /***
     * @param pageNumber
     * @return first subpage of teletext page
     */
    public TeletextPage (int pageNumber) {
        this.pageNumber = pageNumber;
        initializeFastText();
    }

    public TeletextSubpage addNewSubpage() throws Exception {
        if(locked) {
            throw new Exception("TeletextPage is final and can't be changed.");
        }
        TeletextSubpage subpage = new TeletextSubpage();
        teletextSubPages.add(subpage);
        return subpage;
    }

    private void initializeFastText() {
        this.fastText = new FastText();
    }

    private boolean finalizeTeletextPage() {
        if (teletextPageReadyForBroadcast()) {
            locked = true;
            return true;
        }
        return false;
    }

    /***
     * A teletextPage can be broadcast only! when the layoutTemplate and fasttext buttons are set up!
     * @return
     */
    private boolean teletextPageReadyForBroadcast() {
        if (fastText == null) {
            return false;
        }

        for (TeletextSubpage page : teletextSubPages) {
            if (page.getLayoutTemplateFileName().isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public String getConfigurationString() {
        this.locked = true;
        finalizeTeletextPage();
        this.teletextCommands = generateTeletextCommandString();
        return this.teletextCommands;
    }

    public String generateTeletextCommandString() {
        String result = "[" + pageNumber + ".*]\n\n";

        for (TeletextSubpage page : teletextSubPages) {
            String textFileName = "text-" + pageNumber + "-" + teletextSubPages.indexOf(page) + ".txt";
            int subpageNr = (teletextSubPages.size() == 1) ? teletextSubPages.indexOf(page) : teletextSubPages.indexOf(page) + 1;

            result += "[" + pageNumber + ".000" + subpageNr + "]\n";
            result += "TemplateFilename=" + page.getLayoutTemplateFileName() + "\n";
            result += "TextFilename=" + textFileName + "\n";
            result += "Descr= Automatische pagina\n";
            result += "prompts=" + getFastTextTexts() + "\n";
            result += "links=" + getFastTextLinks() + "\n";
            result += "\n";
        }
        return result;
    }

    private String getFastTextTexts() {
        return String.format("%s %s %s %s", fastText.getRedButtonText(), fastText.getGreenButtonText(), fastText.getYellowButtonText(), fastText.getBlueButtonText());
    }

    private String getFastTextLinks() {
        return String.format("%d %d %d %d", fastText.getRedButtonLink(), fastText.getGreenButtonLink(), fastText.getYellowButtonLink(), fastText.getBlueButtonLink());
    }

    public int getTeletextPagenumber() {
        return this.pageNumber;
    }

    public int getSubpageNumber(TeletextSubpage s) {
        return teletextSubPages.indexOf(s);
    }

    public boolean isLocked() {
        return this.locked;
    }
}
