package nl.wos.teletext.ejb;

import nl.wos.teletext.core.TeletextPage;
import nl.wos.teletext.core.TeletextSubpage;
import nl.wos.teletext.core.TeletextUpdatePackage;
import nl.wos.teletext.dao.TrainStationDao;
import nl.wos.teletext.entity.PropertyManager;
import nl.wos.teletext.entity.TrainStation;
import nl.wos.teletext.objects.PublicTransportModuleHelper;
import nl.wos.teletext.objects.TrainDeparture;
import nl.wos.teletext.util.ConfigurationLoader;
import nl.wos.teletext.util.Web;
import nl.wos.teletext.util.XMLParser;
import org.apache.http.auth.*;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class PublicTransportModule extends TeletextModule {
    private static final Logger log = Logger.getLogger(PublicTransportModule.class.getName());

    @Autowired private TrainStationDao trainStationDao;

    @Scheduled(fixedRate = 10)
    //@Schedule(minute="4,9,14,19,24,29,34,39,44,49,54,59", hour="*", persistent=false)
    public void doTeletextUpdate() {
        log.info(this.getClass().getName() + " is going to update teletext.");
        List<TrainStation> trainStations = getTrainStations();

        Map<String, List<TrainDeparture>> trainDepartures = new HashMap<>(trainStations.size());

        for(TrainStation station : trainStations) {
            try {
                String stationData = doAPICallToWebservice(station.getCode());
                Element root = XMLParser.XMLParser(stationData).getDocumentElement();
                NodeList trainStationDepartureNodeList = root.getElementsByTagName("VertrekkendeTrein");

                List stationDepartureList = new ArrayList<TrainDeparture>(trainStationDepartureNodeList.getLength());
                for(int i=0; i<trainStationDepartureNodeList.getLength(); i++) {
                    Node trainDeparture = trainStationDepartureNodeList.item(i);
                    NodeList trainDeparturePropertiesNodeList = trainDeparture.getChildNodes();

                    parseDeparture(stationDepartureList, trainDeparturePropertiesNodeList);
                }
                trainDepartures.put(station.getCode(), stationDepartureList);
            } catch (Exception ex) {
                log.log(Level.SEVERE, "Exception occured at train station " + station.getName(), ex);
            }
        }

        TeletextUpdatePackage updatePackage = new TeletextUpdatePackage();

        for(TrainStation station : trainStations) {
            List<TrainDeparture> stationDepartures = trainDepartures.get(station.getCode());
            TeletextPage teletextPage = new TeletextPage(station.getTeletextPageNumber());
            TeletextSubpage subPage = teletextPage.addNewSubpage();
            subPage.setLayoutTemplateFileName("template-treinen.tpg");
            PublicTransportModuleHelper.addContentToPage(subPage, stationDepartures, station.getName());

            updatePackage.addTeletextPage(teletextPage);
        }
        updatePackage.generateTextFiles();
        phecapConnector.uploadFilesToTeletextServer(updatePackage);
    }

    public List<TrainStation> getTrainStations() {
        return null;
    }

    private void parseDeparture(List stationDepartureList, NodeList trainDeparturePropertiesNodeList) {
        TrainDeparture departure = new TrainDeparture();

        for (int j=0; j<trainDeparturePropertiesNodeList.getLength(); j++) {
            Node node = trainDeparturePropertiesNodeList.item(j);

            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            if(node.hasChildNodes()) {
                departure.setValue(node.getNodeName(), node.getFirstChild().getNodeValue());
            }

            if(node.hasAttributes()) {
                for(int a =0 ; a < node.getAttributes().getLength(); a++) {
                    Node n = node.getAttributes().item(a);
                    if(n.getNodeType() != n.ATTRIBUTE_NODE && n.getNodeName().equals("wijziging")) {
                        departure.setValue(n.getNodeName(), n.getNodeValue());
                    }
                }
            }
        }
        stationDepartureList.add(departure);
    }

    public void setTrainStationDao(TrainStationDao trainStationDao) {
        this.trainStationDao = trainStationDao;
    }

    /***
     * @param stationIdentifier The code (abbreviation) or short, medium or full name or synonym for the trainstation name.
     * @return
     */
    public String doAPICallToWebservice(String stationIdentifier)
    {
        try {
            BasicCredentialsProvider credentials = new BasicCredentialsProvider();
            credentials.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(
                    "stefank@wos.nl",
                    "4IaKyMDu4rMx3yxccTsjDAjJX1SOGTiYN_4D1HEcETu1hc66xKIbeA"));

            String url = "http://webservices.ns.nl/ns-api-avt?station=" + stationIdentifier;
            return EntityUtils.toString(Web.doWebRequest(url, credentials), "UTF-8");

        } catch (Exception ex) {
            log.log(Level.SEVERE, "Exception occured", ex);
        }
        return "";
    }
}