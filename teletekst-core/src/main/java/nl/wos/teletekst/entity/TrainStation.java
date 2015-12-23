package nl.wos.teletekst.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "trainstation")
public class TrainStation extends BaseEntity<String>{

    @Id
    @Column
    private String trainStation;
    @Column
    private String fullName;
    @Column
    private short teletextPage;
    @Column
    private boolean enabled;

    public TrainStation() {

    }

    public TrainStation(String trainStation, String fullName, short teletextPage, boolean enabled) {
        this.trainStation = trainStation;
        this.fullName = fullName;
        this.teletextPage = teletextPage;
        this.enabled = enabled;
    }

    @Override
    protected String getPrimaryKey() {
        return trainStation;
    }

    public String getTrainStation() {
        return trainStation;
    }

    public void setTrainStation(String trainStation) {
        this.trainStation = trainStation;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public short getTeletextPage() {
        return teletextPage;
    }

    public void setTeletextPage(short teletextPage) {
        this.teletextPage = teletextPage;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
