package nl.wos.teletekst.objects;

public class ActualWeather {
    private String datum;
    private String luchtvochtigheid;
    private String temperatuur;
    private String windsnelheidbf;
    private String windrichting;
    private String luchtdruk;
    private String regenMM;

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public String getLuchtvochtigheid() {
        return luchtvochtigheid;
    }

    public void setLuchtvochtigheid(String luchtvochtigheid) {
        this.luchtvochtigheid = luchtvochtigheid;
    }

    public String getTemperatuur() {
        return temperatuur;
    }

    public void setTemperatuur(String temperatuur) {
        this.temperatuur = temperatuur;
    }

    public String getWindsnelheidbf() {
        return windsnelheidbf;
    }

    public void setWindsnelheidbf(String windsnelheidbf) {
        this.windsnelheidbf = windsnelheidbf;
    }

    public String getWindrichting() {
        return windrichting;
    }

    public void setWindrichting(String windrichting) {
        this.windrichting = windrichting;
    }

    public String getLuchtdruk() {
        return luchtdruk;
    }

    public void setLuchtdruk(String luchtdruk) {
        this.luchtdruk = luchtdruk;
    }

    public String getRegenMM() {
        return regenMM;
    }

    public void setRegenMM(String regenMM) {
        this.regenMM = regenMM;
    }
}