package nl.wos.teletext.components;

import nl.wos.teletext.core.TeletextPage;
import nl.wos.teletext.core.TeletextSubpage;
import nl.wos.teletext.core.TeletextUpdatePackage;
import nl.wos.teletext.util.TextOperations;
import nl.wos.teletext.util.Web;
import org.apache.http.util.EntityUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
public class SportModule extends TeletextModule {
    private static final Logger logger = Logger.getLogger(SportModule.class.getName());

    @Scheduled(fixedRate = 900000, initialDelay = 900000)
    public void doTeletextUpdate() {
        logger.info("Sport module is going to update teletext.");

        TeletextUpdatePackage updatePackage = new TeletextUpdatePackage();
        updatePackage.addRemovePagesTask(611, 645);

        List<String> poules = getSportPoules();
        String sportData = getSportData();

        try {
            if (!sportData.isEmpty()) {
                final AtomicInteger pageNumber = new AtomicInteger(611);
                SAXBuilder saxBuilder = new SAXBuilder();
                Document document = saxBuilder.build(new ByteArrayInputStream(sportData.getBytes("UTF-8")));
                List<String> indexPageText = new ArrayList<>(50);

                Comparator<Element> byTitle =
                        (Element e1, Element e2) -> e1.getChild("title").getValue().compareTo(e2.getChild("title").getValue());

                List<Element> sportItems = document.getRootElement().getChild("channel").getChildren("item");
                sportItems.stream().sorted(byTitle).forEach((item) -> {
                    if(poules.contains(item.getChild("title").getValue())) {
                        Element description = item.getChild("description");
                        Element body1 = description.getChildren("body").get(0);
                        Element body2 = description.getChildren("body").get(1);

                        List<String> programAndScoresPageText = parseProgramAndScores(body1, item.getChild("title").getValue());
                        List<String> seasonScoresPageText = parseSeasonOverview(body2, item.getChild("title").getValue());

                        TeletextPage sportPage = new TeletextPage(pageNumber.get());
                        TeletextSubpage page1 = sportPage.addNewSubpage();
                        page1.setLayoutTemplateFileName("template-sport1.tpg");
                        page1.addText(programAndScoresPageText);

                        TeletextSubpage page2 = sportPage.addNewSubpage();
                        page2.setLayoutTemplateFileName("template-sport2.tpg");
                        page2.addText(seasonScoresPageText);

                        String title = TextOperations.removeIllegalCharacters(item.getChild("title").getValue());
                        title = TextOperations.createIndexPageTitle(title);
                        indexPageText.add(title.concat(" ") + pageNumber);

                        updatePackage.addTeletextPage(sportPage);

                        pageNumber.incrementAndGet();
                    }
                });

                TeletextPage indexPage = new TeletextPage(610);
                TeletextSubpage subPage = indexPage.addNewSubpage();
                subPage.setLayoutTemplateFileName("template-sportuitslagenIndex.tpg");
                int i=0;
                for(String title : indexPageText) {
                    if(i<17) {
                        subPage.setTextOnLine(i, title);
                        i++;
                    }
                    else {
                        i=0;
                        subPage = indexPage.addNewSubpage();
                        subPage.setLayoutTemplateFileName("template-sportuitslagenIndex.tpg");
                        subPage.setTextOnLine(i, title);
                        i++;

                    }
                }

                updatePackage.addTeletextPage(indexPage);
                sendFilesToTeletextServer(updatePackage);

                logger.info("Sport module teletext update is finished.");
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Exception occured", ex);
        }
    }

    public List<String> getSportPoules() {
        List<String> sportPoules = new ArrayList<String>(50);
        try {
            org.jsoup.nodes.Document doc = Jsoup.parse(getSportPouleData());
            for(org.jsoup.nodes.Element poule : doc.getElementsByClass("uitslagPoule")) {
                sportPoules.add(poule.attributes().get("data-id"));
            }
        } catch (Exception e) {
            System.out.println("this is bad!, TODO: Add custom exception and cancel teletext update");
        }

        return sportPoules;
    }

    public String getSportPouleData() throws Exception {
        return Web.doWebRequest("http://www.wos.nl/sport/uitslagen").toString();
    }

    public void sendFilesToTeletextServer(TeletextUpdatePackage updatePackage) {
        updatePackage.generateTextFiles();
        phecapConnector.uploadFilesToTeletextServer(updatePackage);
    }

    private List<String> parseProgramAndScores(Element programElement, String title) {
        List<String> pageText = new ArrayList<>(25);
        pageText.add(title.toUpperCase());
        pageText.add("");

        if(programElement.getChildren().size() == 0) {
            pageText.add("Momenteel zijn er geen uitslagen");
            pageText.add("of programma bekend.");
        }

        for(Element element : programElement.getChildren()) {
            switch(element.getName()) {
                case "p":
                    pageText.add(element.getValue());
                    pageText.add("");
                    break;
                case "table":
                    List<Element> scores = element.getChildren("tr");
                    if(scores.isEmpty()) {
                        pageText.add("Momenteel zijn er geen uitslagen");
                        pageText.add("of programma bekend.");
                        return pageText;
                    }

                    for (Element score : scores) {
                        String club1 = "";
                        String club2 = "";
                        String score1 = "";
                        String score2 = "";

                        final List<Element> tdList = score.getChildren("td");

                        if (!tdList.isEmpty()) {
                            if(tdList.size() == 1) {
                                club1 = tdList.get(0).getValue().trim();
                            }
                            else {
                                club1 = tdList.get(0).getValue().trim();
                                club2 = tdList.get(2).getValue().trim();
                            }
                        }

                        // When table contains more than 3 td's there could also a score.
                        if (tdList.size() >= 5) {
                            try {
                                score1 = tdList.get(3).getValue().trim();
                                score2 = tdList.get(5).getValue().trim();
                            }
                            catch(Exception ex) {
                                System.out.println(score);
                            }
                        }

                        if(!score1.isEmpty() && !score2.isEmpty()) {
                            pageText.add(String.format("%-15.15s" + "-" + "%-16.16s %3.3s-%-3.3s", club1, club2, score1, score2));
                        }
                        // When there are no scores for this game, there is a little more space on the page in case of long club names.
                        else {
                            pageText.add(String.format("%-20.20s" + " - " + "%-19.19s", club1, club2));
                        }

                    }
                    pageText.add("");
                    break;
                default:
                    logger.warning("Unknown element found." + element);
                    break;
            }
        }
        return pageText;
    }

    private List<String> parseSeasonOverview(Element scoresElement, String title) {
        List<String> pageText = new ArrayList<>(25);
        pageText.add(title.toUpperCase());
        pageText.add("");

        Element table = scoresElement.getChild("table");
        List<Element> clubs = table.getChildren();

        if(clubs.isEmpty()) {
            pageText.add("Momenteel zijn er geen standen bekend.");
            return pageText;
        }

        for(Element tr : clubs) {
            List<String> values = new ArrayList<>(tr.getChildren().size());
            values.addAll(tr.getChildren().stream().map(Element::getValue).collect(Collectors.toList()));
            String formatString = "";

            for(int i=0; i<values.size(); i++) {
                if(i == 0) {
                    formatString += "%11.11s ";
                    continue;
                }

                if(values.get(i).isEmpty()) {
                    values.remove(i);
                    values.add(i, " ");
                }

                formatString += "%-3.3s ";
            }
            pageText.add(String.format(formatString, String.join("!@#", values).split("!@#")));
        }
        return pageText;
    }

    public String getSportData() {
        try {
            return EntityUtils.toString(Web.doWebRequest("http://sportstanden.infothuis.nl/public/internet-rss.php"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}