package nl.wos.teletekst.core;

import java.util.ArrayList;

public class TeletextPage {
    private int pageNumber;
    private FastText fastText;
    private StringBuilder teletextCommands;
    private ArrayList<TeletextSubpage> teletextSubPages = new ArrayList<TeletextSubpage>();
    private boolean locked;

    /***
     * @param pageNumber
     * @return first subpage of teletext page
     */
    public TeletextSubpage TeletextPage(int pageNumber) {
        this.pageNumber = pageNumber;
        TeletextSubpage subpage = new TeletextSubpage();
        teletextSubPages.add(subpage);
        initializeFastText();
        return subpage;
    }

    private void initializeFastText() {
        //TODO get fasttext entity from database and use those values for setting up fasttext
        this.fastText = new FastText();
    }

    private void finalizeTeletextPage() {
        if (teletextPageReadyForBroadcast()) {
            locked = true;

        }
    }

    /***
     * A teletextPage can be broadcast only! when the layoutTemplate and fasttext buttons are setup.
     *
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
}
