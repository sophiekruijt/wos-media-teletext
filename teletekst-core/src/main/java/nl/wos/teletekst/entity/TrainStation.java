package nl.wos.teletekst.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "train_station")
public class TrainStation extends BaseEntity<String>{

    @Id
    @Column
    private String code;
    @Column
    private String name;
    @Column
    private int teletextPageNumber;
    @Column
    private boolean broadcastOnTeletext;

    public TrainStation() {

    }

    public TrainStation(String code, String name, short teletextPageNumber, boolean broadcastOnTeletext) {
        this.code = code;
        this.name = name;
        this.teletextPageNumber = teletextPageNumber;
        this.broadcastOnTeletext = broadcastOnTeletext;
    }

    @Override
    protected String getPrimaryKey() {
        return code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String fullName) {
        this.name = fullName;
    }

    public int getTeletextPageNumber() {
        return teletextPageNumber;
    }

    public void setTeletextPageNumber(short teletextPage) {
        this.teletextPageNumber = teletextPage;
    }

    public boolean isBroadcastOnTeletext() {
        return broadcastOnTeletext;
    }

    public void setBroadcastOnTeletext(boolean enabled) {
        this.broadcastOnTeletext = enabled;
    }
}
