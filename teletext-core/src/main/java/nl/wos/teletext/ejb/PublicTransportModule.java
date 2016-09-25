package nl.wos.teletext.ejb;

import nl.wos.teletext.core.TeletextPage;
import nl.wos.teletext.core.TeletextSubpage;
import nl.wos.teletext.core.TeletextUpdatePackage;
import nl.wos.teletext.dao.TrainStationDao;
import nl.wos.teletext.entity.PropertyManager;
import nl.wos.teletext.entity.TrainStation;
import nl.wos.teletext.objects.PublicTransportModuleHelper;
import nl.wos.teletext.objects.TrainDeparture;
import nl.wos.teletext.util.Web;
import nl.wos.teletext.util.XMLParser;
import org.apache.http.auth.*;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.ejb.*;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Singleton
public class PublicTransportModule extends TeletextModule {
    private static final Logger log = Logger.getLogger(PublicTransportModule.class.getName());

    @Inject private PropertyManager propertyManager;
    @Inject private TrainStationDao trainStationDao;
    @Inject private PhecapConnector phecapConnector;

    @Schedule(minute="4,9,14,19,24,29,34,39,44,49,54,59", hour="*", persistent=false)
    public void doTeletextUpdate() {
        log.info(this.getClass().getName() + " is going to update teletext.");
        List<TrainStation> trainStations = trainStationDao.findAll();

        Map<String, List<TrainDeparture>> trainDepartures = getTrainDepartureData(trainStations);
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

    private Map<String, List<TrainDeparture>> getTrainDepartureData(List<TrainStation> trainStations) {
        Map<String, List<TrainDeparture>> trainDepartures = new HashMap<>(trainStations.size());

        for(TrainStation station : trainStations) {
            try {
                Element root = XMLParser.XMLParser(getTrainDeparturesForTrainstation(station.getCode())).getDocumentElement();
                NodeList trainStationDepartureNodeList = root.getElementsByTagName("VertrekkendeTrein");

                List stationDepartureList = new ArrayList<TrainDeparture>(trainStationDepartureNodeList.getLength());
                for(int i=0; i<trainStationDepartureNodeList.getLength(); i++) {
                    Node trainDeparture = trainStationDepartureNodeList.item(i);
                    NodeList trainDeparturePropertiesNodeList = trainDeparture.getChildNodes();

                    parseDeparture(stationDepartureList, trainDeparturePropertiesNodeList);
                }
                trainDepartures.put(station.getCode(), stationDepartureList);
            } catch (Exception e) {
                log.severe(e.toString());
                e.printStackTrace();
            }
        }
        return trainDepartures;
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

    /***
     * @param stationIdentifier The code (abbreviation) or short, medium or full name or synonym for the trainstation name.
     * @return
     */
    private String getTrainDeparturesForTrainstation(String stationIdentifier)
    {
        try {
            BasicCredentialsProvider credentials = new BasicCredentialsProvider();
            credentials.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(
                    "stefank@wos.nl",
                    "4IaKyMDu4rMx3yxccTsjDAjJX1SOGTiYN_4D1HEcETu1hc66xKIbeA"));

            String url = "http://webservices.ns.nl/ns-api-avt?station=" + stationIdentifier;
            return EntityUtils.toString(Web.doWebRequest(url, credentials), "UTF-8");

        } catch (Exception e) {
            log.severe(e.toString());
            return "";
        }
    }
}