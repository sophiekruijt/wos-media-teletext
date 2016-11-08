package nl.wos.teletext.components;

import nl.wos.teletext.core.TeletextPage;
import nl.wos.teletext.core.TeletextSubpage;
import nl.wos.teletext.core.TeletextUpdatePackage;
import nl.wos.teletext.dao.SportPouleDao;
import nl.wos.teletext.models.SportPoule;
import nl.wos.teletext.util.SportModuleDataParser;
import nl.wos.teletext.util.Web;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.http.util.EntityUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
public class SportModule extends TeletextModule {
    private static final Logger logger = Logger.getLogger(SportModule.class.getName());

    @Autowired
    private SportPouleDao sportPouleDao;

    private SportModuleDataParser parser = new SportModuleDataParser();

    public static void main(String[] args) {
        new SportModule();
    }

    public SportModule() {
        this.sportPouleDao = new SportPouleDao();
        doTeletextUpdate();
    }

    public void doTeletextUpdate() {
        logger.info("Sport module is going to update teletext.");

        TeletextUpdatePackage updatePackage = new TeletextUpdatePackage();
        updatePackage.addRemovePagesTask(611, 645);

        Map<String, SportPoule> poules = sportPouleDao.getAllSportPoules();
        String sportData = getSportData();

        try {
            if (!sportData.isEmpty()) {
                int pageNumber = 611;
                SAXBuilder saxBuilder = new SAXBuilder();
                Document document = saxBuilder.build(new ByteArrayInputStream(sportData.getBytes("UTF-8")));

                List<Element> sportItems = document.getRootElement().getChild("channel").getChildren("item");
                for(Element item : sportItems) {
                    if(poules.containsKey(item.getChild("title").getValue())) {

                        Element description = item.getChild("description");
                        Element body1 = description.getChildren("body").get(0);
                        Element body2 = description.getChildren("body").get(1);

                        List<String> programAndScoresPageText = parseProgramAndScores(body1);
                        List<String> seasonScoresPageText = parseSeasonOverview(body2);

                        TeletextPage sportPage = new TeletextPage(pageNumber);
                        TeletextSubpage page1 = sportPage.addNewSubpage();
                        page1.setLayoutTemplateFileName("template-sport1.tpg");
                        page1.addText(programAndScoresPageText);

                        TeletextSubpage page2 = sportPage.addNewSubpage();
                        page2.setLayoutTemplateFileName("sportUitslagen2Template");
                        page2.addText(seasonScoresPageText);

                        updatePackage.addTeletextPage(sportPage);

                        pageNumber++;
                        if(pageNumber>650) {
                            break;
                        }
                    }
                }

                updatePackage.generateTextFiles();
                phecapConnector.uploadFilesToTeletextServer(updatePackage);

                logger.info("Sport module teletext update is finished.");
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Exception occured", ex);
        }
    }

    private List<String> parseProgramAndScores(Element programElement) {
        List<String> pageText = new ArrayList<>(25);

        for(Element element : programElement.getChildren()) {
            switch(element.getName()) {
                case "p":
                    pageText.add(programElement.getChild("p").getValue());
                    pageText.add("");
                    break;
                case "table":
                    List<Element> scores = programElement.getChild("table").getChildren("tr");
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

                        if (!score.getChildren("td").isEmpty()) {
                            club1 = score.getChildren("td").get(0).getValue().trim();
                            club2 = score.getChildren("td").get(2).getValue().trim();
                        }
                        // When table contains more than 3 td's there is also a score.
                        if (score.getChildren("td").size() > 3) {
                            score1 = score.getChildren("td").get(3).getValue().trim();
                            score2 = score.getChildren("td").get(5).getValue().trim();
                        }

                        if(!score1.isEmpty() && !score2.isEmpty()) {
                            pageText.add(String.format("%-15.15s" + "-" + "%-16.16s %3.3s-%-3.3s", club1, club2, score1, score2));
                        }
                        // When there are no scores for this game, there is a little more space on the page in case of long club names.
                        else {
                            pageText.add(String.format("%-20.20s" + " - " + "%-19.19s", club1, club2));
                        }
                    }
            }
        }
        return pageText;
    }

    private List<String> parseSeasonOverview(Element scoresElement) {
        List<String> pageText = new ArrayList<>(25);

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

    private String getSportData() {
        try {
            return EntityUtils.toString(Web.doWebRequest("http://sportstanden.infothuis.nl/public/internet-rss.php"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}