package nl.wos.teletext.components;

import nl.wos.teletext.core.RSSItem;
import nl.wos.teletext.core.TeletextPage;
import nl.wos.teletext.core.TeletextSubpage;
import nl.wos.teletext.core.TeletextUpdatePackage;
import nl.wos.teletext.dao.ItemDao;
import nl.wos.teletext.models.Item;
import nl.wos.teletext.util.TextOperations;
import nl.wos.teletext.util.Web;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class NewsModule extends TeletextModule {
    private static final Logger logger = Logger.getLogger(NewsModule.class.getName());

    @Autowired private ItemDao itemDao;

    private final int pageNumberLatestNews = Integer.parseInt(properties.getProperty("PAGENUMBER_LAATSTE_NIEUWS"));
    private final int pageNumberNews = Integer.parseInt(properties.getProperty("PAGENUMBER_NIEUWS_OVERZICHT"));
    private final int pageNumberNewsStart = Integer.parseInt(properties.getProperty("PAGENUMBER_NEWS_BERICHTEN_START"));
    private final int pageNumberSportStart = Integer.parseInt(properties.getProperty("PAGENUMBER_SPORT_BERICHTEN_START"));
    private final String newsDataSource = properties.getProperty("newsDataSource");

    private int newsPageNumberCounter = 0;
    private int sportPageNumberCounter = 0;

    @Scheduled(fixedRate = 600000, initialDelay = 600000)
    public void doTeletextUpdate() {
        logger.info("News module is going to update teletext.");
        this.newsPageNumberCounter = 0;
        this.sportPageNumberCounter= 0;

        try {
            List<RSSItem> newsData = getNewsData();
            if(newsData.isEmpty()) {
                logger.warning("No data received from IB Broadcast. Teletext update will be aborted.");
                return;
            }

            TeletextUpdatePackage updatePackage = new TeletextUpdatePackage();

            List<RSSItem> newsItems = newsData.stream()
                    .filter(b -> b.getCategory().equals("nieuws"))
                    .limit(20).collect(Collectors.toList());
            List<RSSItem> sportItems = newsData.stream()
                    .filter(b -> b.getCategory().equals("sport"))
                    .limit(6).collect(Collectors.toList());

            Item p648 = itemDao.getItem("item001");
            Item p649 = itemDao.getItem("item002");
            Item p656 = itemDao.getItem("item003");
            Item p657 = itemDao.getItem("item004");

            updateAllNewsAndSportPages(updatePackage, newsItems, sportItems);
            updateNewsOverview102(updatePackage, newsItems);
            updateLatestNewsPage101(updatePackage, newsItems);

            publiceerSportOverzicht(sportItems, updatePackage, p648, p649, p656, p657);
            publiceerExtraSportPaginas(Arrays.asList(p648, p649, p656, p657), updatePackage);

            updatePackage.generateTextFiles();
            phecapConnector.uploadFilesToTeletextServer(updatePackage);
        }
        catch(Exception ex) {
            logger.log(Level.SEVERE, "Exception occured", ex);
        }
    }

    private void publiceerExtraSportPaginas(List<Item> items, TeletextUpdatePackage updatePackage) {
        updatePackage.addRemovePagesTask(648, 649);
        updatePackage.addRemovePagesTask(656, 657);

        publiceerExtraSportPagina(items.get(0), 648, updatePackage);
        publiceerExtraSportPagina(items.get(1), 649, updatePackage);
        publiceerExtraSportPagina(items.get(2), 656, updatePackage);
        publiceerExtraSportPagina(items.get(3), 657, updatePackage);
    }

    private void publiceerExtraSportPagina(Item item, int pageNumber, TeletextUpdatePackage updatePackage) {
        String titel = item.getTitle();
        String[] subpageTexts = item.getText().split("#NEWSUBPAGE#");

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

    private void updateLatestNewsPage101(TeletextUpdatePackage updatePackage, List<RSSItem> newsItems)
    {
        try {
            TeletextPage teletextPage = new TeletextPage(pageNumberLatestNews);
            TeletextSubpage subpage = teletextPage.addNewSubpage();
            subpage.setLayoutTemplateFileName("template-laatstenieuws.tpg");

            int line = 0;
            for(RSSItem newsItem : newsItems.stream().limit(6).collect(Collectors.toList()))
            {
                int lineNumber = line * 2 + 1;
                subpage.setTextOnLine(lineNumber, TextOperations.makeBerichtTitelVoorIndexPagina(newsItem.getTitle()) + "\u0003" + (103 + line));
                line++;
            }
            updatePackage.addTeletextPage(teletextPage);
        }
        catch(Exception ex) {
            logger.log(Level.SEVERE, "Exception occured", ex);
        }
    }

    private void updateNewsOverview102(TeletextUpdatePackage updatePackage, List<RSSItem> newsItems)
    {
        TeletextPage teletextPage = new TeletextPage(pageNumberNews);
        TeletextSubpage subpage = teletextPage.addNewSubpage();
        subpage.setLayoutTemplateFileName("template-nieuwsoverzicht.tpg");

        int line = 0;
        for(RSSItem bericht : newsItems)
        {
            subpage.setTextOnLine(line,
                    TextOperations.makeBerichtTitelVoorIndexPagina(bericht.getTitle()) + "\u0003" + (pageNumberNewsStart + line));
            line++;
        }
        updatePackage.addTeletextPage(teletextPage);
    }

    private void updateAllNewsAndSportPages(TeletextUpdatePackage updatePackage, List<RSSItem>... items) {
        this.newsPageNumberCounter = 0;
        this.sportPageNumberCounter= 0;

        for(List<RSSItem> list : items) {
            for (RSSItem bericht : list) {
                try {
                    TeletextPage teletextPage = createTeletextPage(bericht);
                    TeletextSubpage subpage = teletextPage.addNewSubpage();
                    subpage.setLayoutTemplateFileName("template-nieuwsbericht.tpg");

                    addTitleToTeletextPage(bericht, subpage);
                    addTextToTeletextPage(bericht, subpage);

                    updatePackage.addTeletextPage(teletextPage);
                } catch (Exception ex) {
                    logger.log(Level.SEVERE, "Exception occured", ex);
                }
            }
        }
    }

    public void publiceerSportOverzicht(List<RSSItem> sportItems, TeletextUpdatePackage updatePackage, Item i1, Item i2, Item i3, Item i4) {
        TeletextPage page = new TeletextPage(601);
        TeletextSubpage subPage = page.addNewSubpage();
        subPage.setLayoutTemplateFileName("template-sportoverzicht.tpg");
        subPage.setTextOnLine(0, "\u0003Uitslagen amateurvoetbal");
        subPage.setTextOnLine(1, " " + TextOperations.makeBerichtTitelVoorIndexPagina(i1.getTitle()) + "\u0003" + 648);
        subPage.setTextOnLine(2, " " + TextOperations.makeBerichtTitelVoorIndexPagina(i2.getTitle()) + "\u0003" + 649);
        subPage.setTextOnLine(4, "\u0003Sportnieuws");
        int row = 5;
        for(int i=0; i < sportItems.size(); i++) {
            if(i >= 6) {
                break;
            }
            subPage.setTextOnLine(row, " " + TextOperations.makeBerichtTitelVoorIndexPagina(sportItems.get(i).getTitle()) + "\u0003" + (650 + i));
            row++;
        }

        subPage.setTextOnLine(12, "\u0003Inhoud WOS Sport radio 87.6 FM");
        subPage.setTextOnLine(13, " " + TextOperations.makeBerichtTitelVoorIndexPagina(i3.getTitle()) + "\u0003" + 656);
        subPage.setTextOnLine(14, " " + TextOperations.makeBerichtTitelVoorIndexPagina(i4.getTitle()) + "\u0003" + 657);

        updatePackage.addTeletextPage(page);
    }

    private List<RSSItem> getNewsData() throws Exception {
        List<RSSItem> result = new ArrayList<>();
        try {
            String data = EntityUtils.toString(Web.doWebRequest(newsDataSource), "UTF-8");

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
            logger.log(Level.SEVERE, "Exception occured", ex);
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

    private TeletextPage createTeletextPage(RSSItem item) {
        TeletextPage teletextPage = null;
        switch (item.getCategory()) {
            case "nieuws":
                teletextPage = new TeletextPage(pageNumberNewsStart + newsPageNumberCounter);
                this.newsPageNumberCounter = newsPageNumberCounter + 1;
                break;
            case "sport":
                teletextPage = new TeletextPage(pageNumberSportStart + sportPageNumberCounter);
                this.sportPageNumberCounter = sportPageNumberCounter + 1;
                break;
            default:
                logger.log(Level.WARNING, "News item has no valid category: " + item.getCategory());
        }
        return teletextPage;
    }
}