package nl.wos.teletext.mockserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Teletext {
    private static Teletext instance = new Teletext();
    private HashMap<Integer, ArrayList<TeletextPage>> teletextPages = new HashMap<>();

    private Teletext(){ }

    public static Teletext getInstance( ) {
        return instance;
    }

    public HashMap<Integer, ArrayList<TeletextPage>> getTeletextPageList() {
        return teletextPages;
    }

    public void addTeletextPage(TeletextPage page) {
        if(teletextPages.containsKey(page.getPageNumber())) {
            List<TeletextPage> pages = teletextPages.get(page.getPageNumber());
            for(int i=0; i<pages.size(); i++) {
                if(pages.get(i).getSubPageNumber() == page.getSubPageNumber()) {
                    pages.remove(i);
                    pages.add(page);
                    break;
                }
            }
            pages.add(page);
        }
        else {
            ArrayList<TeletextPage> newPageList = new ArrayList<>();
            newPageList.add(page);
            teletextPages.put(page.getPageNumber(), newPageList);
        }
    }

    public String getTextLine(int pageNumber, int subPageNumber, int lineNumber) {
        if (!teletextPages.containsKey(pageNumber)) {
            return "Error, mock server doens't contain page with pagenumber: " + pageNumber;
        }
        for(TeletextPage page : teletextPages.get(pageNumber)) {
            if(page.getSubPageNumber() == subPageNumber) {
                return page.getTextLines()[lineNumber];
            }
        }

        return "Error, no valid combination of pagenumber, subpageNumber and lineNumber";
    }

    public void resetServer() {
        teletextPages.clear();
    }
}
