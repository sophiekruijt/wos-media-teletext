package nl.wos.teletext.components;

import nl.wos.teletext.core.TeletextPage;
import nl.wos.teletext.core.TeletextSubpage;
import nl.wos.teletext.core.TeletextUpdatePackage;
import nl.wos.teletext.dao.TrainStationDao;
import nl.wos.teletext.models.TrainStation;
import nl.wos.teletext.models.TrainDeparture;
import nl.wos.teletext.util.Web;
import org.apache.http.auth.*;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.util.EntityUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class PublicTransportModule extends TeletextModule {
    private static final Logger logger = Logger.getLogger(PublicTransportModule.class.getName());

    @Autowired private TrainStationDao trainStationDao;

    @Scheduled(fixedRate = 999999, initialDelay = 9999999)
    public void doTeletextUpdate() {
        logger.info(this.getClass().getName() + " is going to update teletext.");
        /*List<TrainStation> trainStations = trainStationDao.getAllTrainStations();
        
        for(TrainStation station : trainStations) {
            try {
                String stationData = doAPICallToWebservice(station.getCode());

                if (!stationData.isEmpty()) {
                    SAXBuilder saxBuilder = new SAXBuilder();
                    Document document = saxBuilder.build(new ByteArrayInputStream(stationData.getBytes("UTF-8")));

                    List<Element> departures = document.getRootElement().getChildren("VertrekkendeTrein");

                    for(Element departure : departures) {
                        TrainDeparture trainDeparture = new TrainDeparture();
                        for(Element child : departure.getChildren()) {
                            trainDeparture.setValue(child.getName(), child.getValue());
                        }
                        station.addDeparture(trainDeparture);
                    }
                }
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "Exception occured at train station " + station.getName(), ex);
            }
        }

        TeletextUpdatePackage updatePackage = new TeletextUpdatePackage();

        for(TrainStation station : trainStations) {
            List<TrainDeparture> stationDepartures = station.getDepartures();
            TeletextPage teletextPage = new TeletextPage(station.getTeletextPageNumber());
            TeletextSubpage subPage = teletextPage.addNewSubpage();
            subPage.setLayoutTemplateFileName("template-treinen.tpg");
            addContentToPage(subPage, stationDepartures, station.getName());

            updatePackage.addTeletextPage(teletextPage);
        }
        updatePackage.generateTextFiles();
        phecapConnector.uploadFilesToTeletextServer(updatePackage);*/
    }

    public List<TrainStation> getTrainStations() {
        return trainStationDao.getAllTrainStations();
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
            logger.log(Level.SEVERE, "Exception occured", ex);
        }
        return "";
    }

    private void addContentToPage(TeletextSubpage page, List<TrainDeparture> departures, String stationName)
    {
        try {
            page.setTextOnLine(0,"\u0002" + stationName.toUpperCase());
            page.setTextOnLine(1,"\u0003TIJD        BESTEMMING            SPOOR");

            if(departures.size() == 0) {
                page.setTextOnLine(3, "Op dit station zijn momenteel geen");
                page.setTextOnLine(4, "vertrekkende treinen");
            }

            int line = 2;
            for(TrainDeparture d : departures) {
                if(line > 17) {
                    break;
                }

                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                format.setTimeZone(TimeZone.getTimeZone("GMT+1"));
                String departureTime = format.format(d.getDepartureTime());
                String departureDelay = (!d.getDepartureDelayText().isEmpty()) ? "\u0001" + d.getDepartureDelayText().trim() : "";
                String destination = d.getFinalDestination();
                String opmerking = "";
                //for(int j=0; j<departures.get(i).getRouteText().length(); j++)
                //{
                //    //opmerking += departures.get(i).OpmerkingList1[j];
                //}

                if(destination.length() >= 22) {
                    destination = destination.substring(0,22);
                }

                String vertrekspoor = (d.isChanged()) ? "\u0001"+d.getDepartureTrack() : "\u0003"+d.getDepartureTrack();

                String text = String.format(" %-11s" + "\u0007" + "%-23s %3s", departureTime + departureDelay, destination, vertrekspoor);
                page.setTextOnLine(line, text);
                line++;
                if(d.isChanged()) {
                    opmerking += " Gewijzigd vertrekspoor!";
                }
                if(!opmerking.isEmpty() && !opmerking.equals("geen")) {
                    page.setTextOnLine(line,"\u0001"+opmerking);
                    line++;
                    line++;
                }
            }
        }
        catch(Exception ex) {
            logger.log(Level.SEVERE, "Exception occured", ex);
        }
    }
}