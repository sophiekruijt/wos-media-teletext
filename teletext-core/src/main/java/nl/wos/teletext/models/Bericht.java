package nl.wos.teletext.models;

public class Bericht {
    private Integer bericht;
    private Integer teletekstPagina;
    private String titel;
    private String tekst;

    public Bericht(Integer bericht, Integer teletekstPage, String titel, String tekst) {
        super();
        this.bericht = bericht;
        this.teletekstPagina = teletekstPage;
        this.titel = titel;
        this.tekst = tekst;
    }

    public Integer getBericht() {
        return bericht;
    }

    public void setBericht(Integer bericht) {
        this.bericht = bericht;
    }

    public Integer getTeletekstPagina() {
        return teletekstPagina;
    }

    public void setTeletekstPagina(Integer teletekstPagina) {
        this.teletekstPagina = teletekstPagina;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getTekst() {
        return tekst;
    }

    public void setTekst(String tekst) {
        this.tekst = tekst;
    }

    @Override
    public String toString() {
        return "Bericht{" +
                "bericht=" + bericht +
                ", teletekstPagina=" + teletekstPagina +
                ", titel='" + titel + '\'' +
                ", tekst='" + tekst + '\'' +
                '}';
    }
}
