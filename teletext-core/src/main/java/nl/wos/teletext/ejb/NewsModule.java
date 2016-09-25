package nl.wos.teletext.ejb;

import nl.wos.teletext.core.RSSItem;
import nl.wos.teletext.core.TeletextPage;
import nl.wos.teletext.core.TeletextSubpage;
import nl.wos.teletext.core.TeletextUpdatePackage;
import nl.wos.teletext.dao.ItemsDao;
import nl.wos.teletext.dao.TeletextPaginaDao;
import nl.wos.teletext.entity.Items;
import nl.wos.teletext.util.TextOperations;
import nl.wos.teletext.util.Web;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class NewsModule extends TeletextModule {
    private static final Logger log = Logger.getLogger(NewsModule.class.getName());

    @Inject private TeletextPaginaDao teletextPaginaDao;
    @Inject private ItemsDao itemDao;

    private final int pageNumberLatestNews = Integer.parseInt(properties.getProperty("PAGENUMBER_LAATSTE_NIEUWS"));
    private final int pageNumberNews = Integer.parseInt(properties.getProperty("PAGENUMBER_NIEUWS_OVERZICHT"));
    private final int pageNumberSport = Integer.parseInt(properties.getProperty("PAGENUMBER_SPORT_BERICHTEN_START"));
    private final String newsDataSource = properties.getProperty("newsDataSource");

    private int newsPageNumberCounter = 0;
    private int sportPageNumberCounter = 0;

    @Schedule(second="0,10,20,30,40,50", hour="*", persistent=false)
    public void doTeletextUpdate() {
        log.info("News module is going to update teletext.");
        this.newsPageNumberCounter = 0;
        this.sportPageNumberCounter= 0;

        try {
            TeletextUpdatePackage updatePackage = new TeletextUpdatePackage();
            List<RSSItem> newsData = getNewsData();
            if(newsData.isEmpty()) {
                log.warning("Geen data ontvangen uit RSS feed van IB Broadcast.");
                return;
            }

            updateNieuwsEnSportBerichten(updatePackage, newsData);
            updateNieuwsOverzicht(updatePackage, newsData);
            updateLaatsteNieuwsOverzicht(updatePackage, newsData);

            Items p648 = itemDao.findById("item001");
            Items p649 = itemDao.findById("item002");
            Items p656 = itemDao.findById("item003");
            Items p657 = itemDao.findById("item004");

            publiceerSportOverzicht(newsData, updatePackage, p648, p649, p656, p657);
            publiceerExtraSportPaginas(Arrays.asList(p648, p649, p656, p657), updatePackage);

            updatePackage.generateTextFiles();
            phecapConnector.uploadFilesToTeletextServer(updatePackage);
        }
        catch(Exception ex) {
            log.log(Level.SEVERE, "Exception occured", ex);
        }
    }

    private void publiceerExtraSportPaginas(List<Items> items, TeletextUpdatePackage updatePackage) {
        updatePackage.addRemovePagesTask(648, 649);
        updatePackage.addRemovePagesTask(656, 657);

        publiceerExtraSportPagina(items.get(0), 648, updatePackage);
        publiceerExtraSportPagina(items.get(1), 649, updatePackage);
        publiceerExtraSportPagina(items.get(2), 656, updatePackage);
        publiceerExtraSportPagina(items.get(3), 657, updatePackage);
    }

    private void publiceerExtraSportPagina(Items item, int pageNumber, TeletextUpdatePackage updatePackage) {
        String titel = item.getPublicationTitle();
        String[] subpageTexts = item.getPublicationText().split("#NEWSUBPAGE#");

        TeletextPage teletextPage = new TeletextPage(pageNumber);
        for (int i=0; i<subpageTexts.length; i++)
        {
            String[] subpageText = subpageTexts[i].split("\n");

            TeletextSubpage subpage = teletextPage.addNewSubpage();
            subpage.setLayoutTemplateFileName("template-nieuwsbericht.tpg");
            subpage.setTextOnLine(0, titel);

            for(int j=0; j<subpageText.length; j++) {
                subpage.setTextOnLine(j+2, subpageText[j]);
            }
        }
        updatePackage.addTeletextPage(teletextPage);
    }

    private void updateLaatsteNieuwsOverzicht(TeletextUpdatePackage updatePackage, List<RSSItem> berichten)
    {
        try {
            TeletextPage teletextPage = new TeletextPage(pageNumberLatestNews);
            TeletextSubpage subpage = teletextPage.addNewSubpage();
            subpage.setLayoutTemplateFileName("template-laatstenieuws.tpg");

            int line = 0;
            RSSItem[] newsItems = berichten.stream().filter(b -> b.getCategory().equals("nieuws")).toArray(RSSItem[]::new);
            for(RSSItem item: newsItems)
            {
                int lineNumber = line * 2 + 1;
                subpage.setTextOnLine(lineNumber, TextOperations.makeBerichtTitelVoorIndexPagina(item.getTitle()) + "\u0003" + (103 + line));
                line++;
            }
            updatePackage.addTeletextPage(teletextPage);
        }
        catch(Exception ex) {
            log.log(Level.SEVERE, "Exception occured", ex);
        }
    }

    private void updateNieuwsOverzicht(TeletextUpdatePackage updatePackage, List<RSSItem> berichten)
    {
        TeletextPage teletextPage = new TeletextPage(pageNumberNews);
        TeletextSubpage subpage = teletextPage.addNewSubpage();
        subpage.setLayoutTemplateFileName("template-nieuwsoverzicht.tpg");

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
        this.newsPageNumberCounter = 0;
        this.sportPageNumberCounter= 0;

        for(RSSItem bericht : berichten) {
            try {
                TeletextPage teletextPage = createTeletextPage(bericht);
                TeletextSubpage subpage = teletextPage.addNewSubpage();
                subpage.setLayoutTemplateFileName("template-nieuwsbericht.tpg");

                addTitleToTeletextPage(bericht, subpage);
                addTextToTeletextPage(bericht, subpage);

                updatePackage.addTeletextPage(teletextPage);
            }
            catch(Exception ex) {
                log.log(Level.SEVERE, "Exception occured", ex);
            }
        }
    }

    public void publiceerSportOverzicht(List<RSSItem> items, TeletextUpdatePackage updatePackage, Items i1, Items i2, Items i3, Items i4) {

        RSSItem[] sportItems = items.stream().filter(b -> b.getCategory().equals("sport")).toArray(RSSItem[]::new);

        TeletextPage page = new TeletextPage(601);
        TeletextSubpage subPage = page.addNewSubpage();
        subPage.setLayoutTemplateFileName("template-sportoverzicht.tpg");
        subPage.setTextOnLine(0, "\u0003Uitslagen amateurvoetbal");
        subPage.setTextOnLine(1, " " + TextOperations.makeBerichtTitelVoorIndexPagina(i1.getPublicationTitle()) + "\u0003" + 648);
        subPage.setTextOnLine(2, " " + TextOperations.makeBerichtTitelVoorIndexPagina(i2.getPublicationTitle()) + "\u0003" + 649);
        subPage.setTextOnLine(4, "\u0003Sportnieuws");
        int row = 5;
        for(int i=0; i < sportItems.length; i++) {
            if(i >= 6) {
                break;
            }
            subPage.setTextOnLine(row, " " + TextOperations.makeBerichtTitelVoorIndexPagina(sportItems[i].getTitle()) + "\u0003" + (650 + i));
            row++;
        }

        subPage.setTextOnLine(12, "\u0003Inhoud WOS Sport radio 87.6 FM");
        subPage.setTextOnLine(13, " " + TextOperations.makeBerichtTitelVoorIndexPagina(i3.getPublicationTitle()) + "\u0003" + 656);
        subPage.setTextOnLine(14, " " + TextOperations.makeBerichtTitelVoorIndexPagina(i4.getPublicationTitle()) + "\u0003" + 657);

        updatePackage.addTeletextPage(page);
    }

    private List<RSSItem> getNewsData() throws Exception {
        List<RSSItem> result = new ArrayList<>();
        try {
            log.info("Request naar IB Broadcast wordt verstuurd.");
            String data = EntityUtils.toString(Web.doWebRequest(newsDataSource), "UTF-8");
            log.info("Request naar IB Broadcast is klaar.");

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            ByteArrayInputStream input =  new ByteArrayInputStream(data.getBytes("UTF-8"));
            Document doc = builder.parse(input);

            XPath xPath =  XPathFactory.newInstance().newXPath();
            String expression = "/rss/channel/item";
            NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node nNode = nodeList.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    RSSItem item = new RSSItem();
                    Element eElement = (Element) nNode;
                    item.setCategory(eElement.getElementsByTagName("category").item(0).getTextContent());
                    item.setTitle(eElement.getElementsByTagName("title").item(0).getTextContent());
                    item.setText(eElement.getElementsByTagName("description").item(0).getTextContent());
                    result.add(item);
                }
            }
        } catch (Exception ex) {
            log.log(Level.SEVERE, "Exception occured", ex);
        }
        return result;
    }

    private void addTitleToTeletextPage(RSSItem bericht, TeletextSubpage subpage) {
        subpage.setTextOnLine(0, bericht.getTitle());
    }

    private void addTextToTeletextPage(RSSItem item, TeletextSubpage subpage) {
        int lineNumber = 2;
        for(String line : item.getText().split("\n")) {
            subpage.setTextOnLine(lineNumber, line);
            lineNumber++;
        }
    }

    private TeletextPage createTeletextPage(RSSItem item) throws Exception {
        TeletextPage teletextPage;
        switch (item.getCategory()) {
            case "nieuws":
                teletextPage = new TeletextPage(pageNumberNews + newsPageNumberCounter);
                this.newsPageNumberCounter = newsPageNumberCounter + 1;
                break;
            case "sport":
                teletextPage = new TeletextPage(pageNumberSport + sportPageNumberCounter);
                this.sportPageNumberCounter = sportPageNumberCounter + 1;
                break;
            default:
                throw new Exception("Bericht heeft geen geldige categorie: " + item.getCategory());
        }
        return teletextPage;
    }
}