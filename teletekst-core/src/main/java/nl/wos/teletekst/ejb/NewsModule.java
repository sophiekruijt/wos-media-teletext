package nl.wos.teletekst.ejb;

import nl.wos.teletekst.core.RSSItem;
import nl.wos.teletekst.core.TeletextPage;
import nl.wos.teletekst.core.TeletextSubpage;
import nl.wos.teletekst.core.TeletextUpdatePackage;
import nl.wos.teletekst.util.Configuration;
import nl.wos.teletekst.util.TextOperations;
import nl.wos.teletekst.util.Web;
import org.apache.http.util.EntityUtils;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;
import java.util.List;
import java.util.logging.Logger;

@Singleton
public class NewsModule {
    private static final Logger log = Logger.getLogger(NewsModule.class.getName());

    @Inject private PhecapConnector phecapConnector;

    private int pageNumberNews = Configuration.PAGENUMBER_NIEUWS_BERICHTEN_START;
    private int pageNumberSport = Configuration.PAGENUMBER_SPORT_BERICHTEN_START;

    @Schedule(second="*/10", minute="*",hour="*", persistent=false)
    public void doTeletextUpdate() throws Exception {
        log.info(this.getClass().getName() + " is going to update teletext.");

        TeletextUpdatePackage updatePackage = new TeletextUpdatePackage();
        List<RSSItem> newsData = GetNewsData();

        updateNieuwsEnSportBerichten(updatePackage, newsData);
        updateNieuwsOverzicht(updatePackage, newsData);
        updateLaatsteNieuwsOverzicht(updatePackage, newsData);
        //updateSportOverzicht(updatePackage, newsData);

        updatePackage.generateTextFiles();
        phecapConnector.uploadFilesToTeletextServer(updatePackage);
    }

    private void updateLaatsteNieuwsOverzicht(TeletextUpdatePackage updatePackage, List<RSSItem> berichten)
    {
        try {
            TeletextPage teletextPage = new TeletextPage(Configuration.PAGENUMBER_LAATSTE_NIEUWS);
            TeletextSubpage subpage = teletextPage.addNewSubpage();
            subpage.setLayoutTemplateFileName("template-laatstenieuws.tpg");

            int line = 0;
            RSSItem[] newsItems = berichten.stream().filter(b -> b.getCategory().equals("nieuws")).toArray(RSSItem[]::new);
            for(RSSItem item: newsItems)
            {
                int lineNumber = line * 2 + 1;
                subpage.setTextOnLine(line * 2 + 1,
                        TextOperations.makeBerichtTitelVoorIndexPagina(item.getTitle()) + "\u0003" + (103 + line));
                line++;
            }
            updatePackage.addTeletextPage(teletextPage);
        }
        catch(Exception e) {
            e.printStackTrace();
        }

    }

    private void updateNieuwsOverzicht(TeletextUpdatePackage updatePackage, List<RSSItem> berichten)
    {
        TeletextPage teletextPage = new TeletextPage(Configuration.PAGENUMBER_NIEUWS_OVERZICHT);
        TeletextSubpage subpage = teletextPage.addNewSubpage();
        subpage.setLayoutTemplateFileName("template-laatstenieuws.tpg");

        int line = 0;
        RSSItem[] newsItems = berichten.stream().filter(b -> b.getCategory().equals("nieuws")).toArray(RSSItem[]::new);
        for(RSSItem bericht : newsItems)
        {
            subpage.setTextOnLine(line,
                    TextOperations.makeBerichtTitelVoorIndexPagina(bericht.getTitle()) + "\u0003" + (103 + line));
            line++;
        }
        updatePackage.addTeletextPage(teletextPage);
    }

    private void updateNieuwsEnSportBerichten(TeletextUpdatePackage updatePackage, List<RSSItem> berichten) {
        for(RSSItem bericht : berichten) {
            try {
                TeletextPage teletextPage = createTeletextPage(bericht);
                TeletextSubpage subpage = teletextPage.addNewSubpage();
                subpage.setLayoutTemplateFileName("template-nieuwsbericht.tpg");

                addTitleToTeletextPage(bericht, subpage);
                addTextToTeletextPage(bericht, subpage);

                updatePackage.addTeletextPage(teletextPage);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    private List<RSSItem> getNewsData() {
        String url = "http://teletekst.ibbroadcast.nl/getFeed.ashx?id=190";
        try {
            String data EntityUtils.toString(Web.doWebRequest(url), "UTF-8");
            return parser.ParseNewsData(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addTitleToTeletextPage(RSSItem bericht, TeletextSubpage subpage)
    {
        subpage.setTextOnLine(0, bericht.getTitle());
    }

    private void addTextToTeletextPage(RSSItem item, TeletextSubpage subpage)
    {
        int lineNumber = 2;
        for(String line : item.getText().split("\n"))
        {
            subpage.setTextOnLine(lineNumber, line);
            lineNumber++;
        }
    }

    private TeletextPage createTeletextPage(RSSItem item) throws Exception
    {
        TeletextPage teletextPage;
        switch (item.getCategory())
        {
            case "nieuws":
                teletextPage = new TeletextPage(pageNumberNews);
                pageNumberNews++;
                break;
            case "sport":
                teletextPage = new TeletextPage(pageNumberSport);
                pageNumberSport++;
                break;
            default:
                throw new Exception("Bericht heeft geen geldige categorie: " + item.getCategory());
        }
        return teletextPage;
    }
}