package nl.wos.teletekst.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bericht")
public class Bericht extends BaseEntity<String>{

    @Id
    @Column(name = "bericht")
    private int bericht;
    @Column(name = "teletekstPagina")
    private int teletekstPagina;
    @Column(name = "titel")
    private String titel;
    @Column(name = "tekst")
    private String tekst;

    public Bericht() {

    }

    public Bericht(int bericht, int teletekstPage, String titel, String tekst) {
        this.bericht = bericht;
        this.teletekstPagina = teletekstPage;
        this.titel = titel;
        this.tekst = tekst;
    }

    @Override
    protected String getPrimaryKey() {
        return Integer.toString(bericht);
    }

    public String getTekst() {
        return tekst;
    }

    public void setTekst(String tekst) {
        this.tekst = tekst;
    }

    public int getBericht() {
        return bericht;
    }

    public void setBericht(int bericht) {
        this.bericht = bericht;
    }

    public int getTeletekstPagina() {
        return teletekstPagina;
    }

    public void setTeletekstPagina(int teletekstPagina) {
        this.teletekstPagina = teletekstPagina;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }
}
