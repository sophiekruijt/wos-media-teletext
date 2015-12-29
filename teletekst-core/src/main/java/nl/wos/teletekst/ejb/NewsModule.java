package nl.wos.teletekst.ejb;

import nl.wos.teletekst.core.RSSItem;
import nl.wos.teletekst.core.TeletextPage;
import nl.wos.teletekst.core.TeletextSubpage;
import nl.wos.teletekst.core.TeletextUpdatePackage;
import nl.wos.teletekst.dao.TeletextPaginaDao;
import nl.wos.teletekst.entity.TeletextPagina;
import nl.wos.teletekst.util.Configuration;
import nl.wos.teletekst.util.TextOperations;
import nl.wos.teletekst.util.Web;
import nl.wos.teletekst.util.XMLParser;
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
import java.util.List;
import java.util.logging.Logger;

@Singleton
public class NewsModule {
    private static final Logger log = Logger.getLogger(NewsModule.class.getName());

    @Inject private PhecapConnector phecapConnector;
    @Inject private TeletextPaginaDao teletextPaginaDao;

    private int pageNumberNews = Configuration.PAGENUMBER_NIEUWS_BERICHTEN_START;
    private int pageNumberSport = Configuration.PAGENUMBER_SPORT_BERICHTEN_START;

    @Schedule(second="*/15", minute="*",hour="*", persistent=false)
    public void doTeletextUpdate() throws Exception {
        if(!Configuration.NEWS_MODULE_ENABLED) {
            return;
        }

        log.info(this.getClass().getName() + " is going to update teletext.");

        try {
            TeletextUpdatePackage updatePackage = new TeletextUpdatePackage();
            List<RSSItem> newsData = getNewsData();

            updateNieuwsEnSportBerichten(updatePackage, newsData);
            updateNieuwsOverzicht(updatePackage, newsData);
            updateLaatsteNieuwsOverzicht(updatePackage, newsData);

            TeletextPagina p648 = teletextPaginaDao.findPagina(648);
            TeletextPagina p649 = teletextPaginaDao.findPagina(649);
            TeletextPagina p656 = teletextPaginaDao.findPagina(656);
            TeletextPagina p657 = teletextPaginaDao.findPagina(657);

            publiceerSportOverzicht(newsData, updatePackage, p648, p649, p656, p657);

            updatePackage.generateTextFiles();
            phecapConnector.uploadFilesToTeletextServer(updatePackage);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
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
                subpage.setTextOnLine(lineNumber, TextOperations.makeBerichtTitelVoorIndexPagina(item.getTitle()) + "\u0003" + (103 + line));
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

    public void publiceerSportOverzicht(List<RSSItem> items, TeletextUpdatePackage updatePackage,
                                        TeletextPagina i1, TeletextPagina i2, TeletextPagina i3, TeletextPagina i4) {

        RSSItem[] sportItems = items.stream().filter(b -> b.getCategory().equals("sport")).toArray(RSSItem[]::new);

        TeletextPage page = new TeletextPage(601);
        TeletextSubpage subPage = page.addNewSubpage();
        subPage.setLayoutTemplateFileName("template-sportoverzicht.tpg");
        subPage.setTextOnLine(0, "\u0003Uitslagen amateurvoetbal");
        subPage.setTextOnLine(1, " " + TextOperations.makeBerichtTitelVoorIndexPagina(i1.getTitel()) + "\u0003" + 648);
        subPage.setTextOnLine(2, " " + TextOperations.makeBerichtTitelVoorIndexPagina(i2.getTitel()) + "\u0003" + 649);
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
        subPage.setTextOnLine(13, " " + TextOperations.makeBerichtTitelVoorIndexPagina(i3.getTitel()) + "\u0003" + 656);
        subPage.setTextOnLine(14, " " + TextOperations.makeBerichtTitelVoorIndexPagina(i4.getTitel()) + "\u0003" + 657);

        updatePackage.addTeletextPage(page);
    }

    private List<RSSItem> getNewsData() throws Exception {
        List<RSSItem> result = new ArrayList<>();
        String url = "http://teletekst.ibbroadcast.nl/getFeed.ashx?id=190";
        try {
            String data = EntityUtils.toString(Web.doWebRequest(url), "UTF-8");

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
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Parse error" + e.toString());
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