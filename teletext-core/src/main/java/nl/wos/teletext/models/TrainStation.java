package nl.wos.teletext.models;

import java.util.ArrayList;
import java.util.List;

public class TrainStation {
    private String code;
    private String name;
    private Integer teletextPageNumber;
    private Boolean broadcastOnTeletext;
    private List departures = new ArrayList<TrainDeparture>(25);

    public TrainStation(String code, String name, Integer teletextPageNumber, Boolean broadcastOnTeletext) {
        this.code = code;
        this.name = name;
        this.teletextPageNumber = teletextPageNumber;
        this.broadcastOnTeletext = broadcastOnTeletext;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public Integer getTeletextPageNumber() {
        return teletextPageNumber;
    }

    public Boolean isBroadcastOnTeletext() {
        return broadcastOnTeletext;
    }

    public List<TrainDeparture> getDepartures() {
        return departures;
    }

    public void addDeparture(TrainDeparture departure) {
        this.departures.add(departure);
    }

    @Override
    public String toString() {
        return "TrainStation{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", teletextPageNumber=" + teletextPageNumber +
                ", broadcastOnTeletext=" + broadcastOnTeletext +
                '}';
    }
}
