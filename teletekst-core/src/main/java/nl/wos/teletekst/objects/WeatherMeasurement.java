package nl.wos.teletekst.objects;

public class WeatherMeasurement {
    private String date;
    private String humidity;
    private String temperature;
    private String windSpeedBf;
    private String windDirection;
    private String airPressure;
    private String rainMm;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getWindSpeedBf() {
        return windSpeedBf;
    }

    public void setWindSpeedBf(String windSpeedBf) {
        this.windSpeedBf = windSpeedBf;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public String getAirPressure() {
        return airPressure;
    }

    public void setAirPressure(String airPressure) {
        this.airPressure = airPressure;
    }

    public String getRainMm() {
        return rainMm;
    }

    public void setRainMm(String rainMm) {
        this.rainMm = rainMm;
    }
}