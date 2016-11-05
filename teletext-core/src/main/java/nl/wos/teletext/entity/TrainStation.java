package nl.wos.teletext.entity;

public class TrainStation {
    private String code;
    private String name;
    private Integer teletextPageNumber;
    private Boolean broadcastOnTeletext;

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
