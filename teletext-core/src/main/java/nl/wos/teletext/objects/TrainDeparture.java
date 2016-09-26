package nl.wos.teletext.objects;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class TrainDeparture {
    private static final Logger log = Logger.getLogger(TrainDeparture.class.getName());

    private String ritNummer = "";
    private Date departureTime = new Date();
    private String finalDestination = "";
    private String trainType = "";
    private String routeText = "";
    private String transportCompany = "";
    private String departureTrack = "";
    private String departureDelay = "";
    private List<String> opmerkingen = new ArrayList<>();
    private boolean changed = false;

    public String getDepartureDelay() {
        return departureDelay;
    }

    public String getRitNummer() {
        return ritNummer;
    }

    public Date getDepartureTime() {
        return departureTime;
    }

    public String getFinalDestination() {
        return finalDestination;
    }

    public String getTrainType() {
        return trainType;
    }

    public String getRouteText() {
        return routeText;
    }

    public String getTransportCompany() {
        return transportCompany;
    }

    public String getDepartureTrack() {
        return departureTrack;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setValue(String property, String waarde) {
        switch(property) {
            case "RitNummer":
                this.ritNummer = waarde;
                break;
            case "VertrekTijd":
                try {
                    this.departureTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(waarde);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case "EindBestemming":
                this.finalDestination = waarde;
                break;
            case "TreinSoort":
                this.trainType = waarde;
                break;
            case "RouteTekst":
                this.routeText = waarde;
                break;
            case "Vervoerder":
                this.transportCompany = waarde;
                break;
            case "VertrekSpoor":
                this.departureTrack = waarde;
                break;
            case "VertrekVertragingTekst":
                this.departureDelay = waarde;
                break;
            case "wijziging":
                this.changed = Boolean.valueOf(waarde);
                break;
            default:
                log.warning("Unknown property to set for TrainDeparture (" + property + ")");
                break;
        }
    }
}