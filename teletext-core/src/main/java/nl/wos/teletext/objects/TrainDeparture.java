package nl.wos.teletext.objects;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
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
    private String departeDelay = "";
    private String departureDelayText = "";
    private String reisTip = "";
    private List<String> opmerkingen = new ArrayList<>();
    private boolean changed = false;

    public String getDepartureDelayText() {
        return departureDelayText;
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

    public String getReisTip() {
        return reisTip;
    }

    public void setReisTip(String reisTip) {
        this.reisTip = reisTip;
    }

    public void setValue(String property, String value){
        switch(property) {
            case "RitNummer":
                this.ritNummer = value;
                break;
            case "VertrekTijd":
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+1"));
                    this.departureTime = dateFormat.parse(value);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case "EindBestemming":
                this.finalDestination = value;
                break;
            case "TreinSoort":
                this.trainType = value;
                break;
            case "RouteTekst":
                this.routeText = value;
                break;
            case "Vervoerder":
                this.transportCompany = value;
                break;
            case "VertrekSpoor":
                this.departureTrack = value;
                break;
            case "VertrekVertraging":
                this.departeDelay = value;
                break;
            case "VertrekVertragingTekst":
                this.departureDelayText = value;
                break;
            case "wijziging":
                this.changed = Boolean.valueOf(value);
                break;
            case "ReisTip":
                this.reisTip = value;
                break;
            default:
                log.warning("Unknown property to set for TrainDeparture (" + property + ")");
                break;
        }
    }
}