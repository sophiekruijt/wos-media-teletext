package nl.wos.teletext.util;

import nl.wos.teletext.entity.SportPoule;
import nl.wos.teletext.objects.SportPouleRssItem;
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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class SportModuleDataParser {
    private static final Logger log = Logger.getLogger(SportModuleDataParser.class.getName());

    public List<SportPouleRssItem> parsePoules(String sportData, List<SportPoule> requestedPoules)
    {
        List<SportPouleRssItem> sportPouleLijst = new ArrayList<>();
        try {

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            ByteArrayInputStream input =  new ByteArrayInputStream(sportData.getBytes("UTF-8"));
            Document doc = builder.parse(input);

            XPath xPath =  XPathFactory.newInstance().newXPath();
            String expression = "/rss/channel/item";
            NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node nNode = nodeList.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String pouleTitle = eElement.getElementsByTagName("title").item(0).getTextContent();

                    SportPouleRssItem item = new SportPouleRssItem();
                    item.setTitle(pouleTitle);

                    Node description = eElement.getElementsByTagName("description").item(0);
                    String tekst1 = description.getFirstChild().getNodeValue();
                    String tekst2 = description.getFirstChild().getNextSibling().getNodeValue();

                    System.out.println(tekst1);
                    System.out.println(tekst2);
                }
            }
        }
        catch (Exception e)
        {
            log.severe("Exception: " + e.toString());
        }
        return sportPouleLijst;
    }

    /*

    private void postProcessResult(List<SportPouleRssItem> result) {
        searchForNullValuesInSportPoulesAndFillThem(result);
        setTeletextPageNumbers(result);
    }

    private List<SportPouleRssItem> parseRSSFeed(String rawSportRSSString, List<String> sportclubs) {
        HtmlDocument doc = new HtmlDocument();
        doc.LoadHtml(rawSportRSSString);

        HtmlNode rssNode = doc.DocumentNode.ChildNodes.FindFirst("rss");
        HtmlNode channelNode = rssNode.ChildNodes.FindFirst("channel");
        HtmlNodeCollection itemNodes = channelNode.SelectNodes("./item");

        List<SportPouleRssItem> tempResult = new ArrayList<>();

        for (int i = 0; i < itemNodes.Count; i++) {
            if(sportclubs.Contains(itemNodes[i].SelectSingleNode("./title").InnerText)) {
                SportPoule poule = ParseItem(itemNodes[i]);
                tempResult.Add(poule);
            }
        }

        List<SportPouleRssItem> result = new List<SportPouleRssItem>(sportclubs.size());
        for (int i = 0; i < sportclubs.size(); i++) {
            result.add(tempResult.Find(e => e.Title.Equals(sportclubs[i])));
        }

        return result; ;
    }

    private SportPouleRssItem parseItem(HtmlNode itemNode) {
        String itemName = itemNode.SelectSingleNode("./title").InnerText;
        SportPoule poule = new SportPoule(itemName);

        HtmlNode descriptioNode = itemNode.SelectSingleNode("./description");
        HtmlNodeCollection nodes = descriptioNode.SelectNodes("./body");
        poule.UitslagenData = ParseProgrammaEnUitslagen(nodes[0]);
        poule.StandenData = ParseRangenEnStanden(nodes[1]);
        return poule;
    }

    private List<List<String>> parseRangenEnStanden(HtmlNode standenBodyNode) {
        List<List<String>> standenData = new List<List<String>>();

        String htmlNode = standenBodyNode.InnerHtml;
        HtmlDocument doc = new HtmlDocument();
        doc.LoadHtml(htmlNode);
        HtmlNode tableNode = doc.DocumentNode.SelectSingleNode("//table");
        HtmlNodeCollection trNodes = tableNode.SelectNodes("//tr");
        foreach (HtmlNode n in trNodes) {
            List<String> data = new List<String>();

            foreach (HtmlNode m in n.ChildNodes) {
                if (m.Name.Equals("td") || m.Name.Equals("th")) {
                    String innertext = m.InnerText;
                    data.add(innertext);
                }
            }
            standenData.Add(data);
        }
        standenBodyNode = null;
        return standenData;
    }

    private List<List<String>> parseProgrammaEnUitslagen(HtmlNode programmaBodyNode) {
        List<List<String>> uitslagenData = new List<List<String>>();
        for (int i = 0; i < programmaBodyNode.ChildNodes.Count; i++) {
            HtmlNode node = programmaBodyNode.ChildNodes[i];
            if (node.Name.Equals("p")) {
                List<String> uitslagenList = new List<String>();
                uitslagenList.Add(node.InnerText);
                uitslagenData.Add(uitslagenList);
            }
            else if (node.Name.Equals("table")) {
                HtmlNodeCollection tr = node.SelectNodes("tr");
                foreach (HtmlNode n in tr) {
                    List<String> uitslagenList = new List<String>();
                    HtmlNodeCollection tdCollection = n.SelectNodes("td");
                    for (int j = 0; j < tdCollection.Count; j++) {
                        uitslagenList.Add(tdCollection[j].InnerText);
                    }
                    uitslagenData.Add(uitslagenList);
                }
            }
        }
        return uitslagenData;
    }

    private void searchForNullValuesInSportPoulesAndFillThem (List<SportPouleRssItem> result)
    {
        for (int i = 0; i < result.size(); i++) {
            if (result[i] == null) {
                result[i] = new SportPouleRssItem("Sportpoule is afgelopen");
            }
        }
    }

    private void setTeletextPageNumbers(List<SportPouleRssItem> result) {
        int sportUitslagenBeginPageNumber = 611;
        for(int i = 0; i < result.size(); i++) {
            result[i].TextPageNumber = sportUitslagenBeginPageNumber + i;
        }
    }*/
}