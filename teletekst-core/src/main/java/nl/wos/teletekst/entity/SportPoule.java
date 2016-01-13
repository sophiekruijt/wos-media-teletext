package nl.wos.teletekst.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "sport_poule")
public class SportPoule extends BaseEntity<Integer>{

    @Id
    @Column(name = "id")
    private int id;
    @Column(name = "naam")
    private String naam;

    public SportPoule() {

    }

    public SportPoule(int id, String naam) {
        this.id = id;
        this.naam = naam;
    }

    @Override
    protected Integer getPrimaryKey() {
        return id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }
}
