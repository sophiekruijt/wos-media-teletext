package nl.wos.teletekst.ejb;

import nl.wos.teletekst.dao.TrainStationDao;
import nl.wos.teletekst.entity.TrainStation;
import nl.wos.teletekst.util.Web;
import org.apache.http.auth.*;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.util.EntityUtils;

import javax.ejb.*;
import javax.inject.Inject;
import java.util.List;

@Singleton
public class PublicTransportModule {

    @Inject private TrainStationDao trainStationDao;

    @Schedule(second="*/10", minute="*",hour="*", persistent=false)
    public void doTeletextUpdate() {
        System.out.println(getClass().toString() + " is going to update teletext.");
        List<TrainStation> trainStations = trainStationDao.findAll();

        for(TrainStation station : trainStations) {
            String departures = getTrainDeparturesForTrainstation(station.getTrainStation());
            System.out.println(departures);
        }
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
            e.printStackTrace();
            return "";
        }
    }
}