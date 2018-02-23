package nl.wos.teletext.components;

import nl.wos.teletext.core.TeletextPage;
import nl.wos.teletext.core.TeletextSubpage;
import nl.wos.teletext.core.TeletextUpdatePackage;
import nl.wos.teletext.models.WeatherMeasurement;
import nl.wos.teletext.models.WeatherForecast;
import nl.wos.teletext.util.TextOperations;
import nl.wos.teletext.util.Web;

import org.apache.http.util.EntityUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class WeatherModule extends TeletextModule {
    private static final Logger logger = Logger.getLogger(WeatherModule.class.getName());

    @Scheduled(fixedRate = 900000, initialDelay = 20000)
    public void doTeletextUpdate() {
        logger.info("Weather module is going to update teletext.");

        TeletextUpdatePackage updatePackage = new TeletextUpdatePackage();

        String weatherData = getWeatherData();

        if (!weatherData.isEmpty()) {
            try {
                SAXBuilder saxBuilder = new SAXBuilder();
                Document document = saxBuilder.build(new ByteArrayInputStream(weatherData.getBytes("UTF-8")));
                String weerbericht = parseWeerbericht(document);
                List<WeatherForecast> meerdaagse = parseMeerdaagse(document);
                WeatherMeasurement actualWeather = parseActualWeather(document);

                updateWeersVerwachting(weerbericht, updatePackage);
                updateMeerdaagse(meerdaagse, updatePackage);
                updateCurrentWeatherMeasurements(actualWeather, updatePackage);

                updatePackage.generateTextFiles();
                phecapConnector.uploadFilesToTeletextServer(updatePackage);
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "Exception occurred", ex);
            }


            logger.info("Weather module teletext update is finished.");
        }
    }

    private String getWeatherData() {
        try {
            return EntityUtils.toString(Web.doWebRequest("https://xml.buienradar.nl/"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String parseWeerbericht(Document document) {
        return document.getRootElement()
                .getChild("weergegevens")
                .getChild("verwachting_vandaag")
                .getChild("tekst").getText();
    }

    private List<WeatherForecast> parseMeerdaagse(Document document) {
        List<WeatherForecast> result = new ArrayList<>(5);

        Element meerdaagse = document.getRootElement()
                .getChild("weergegevens")
                .getChild("verwachting_meerdaags");

        result.add(parseMeerdaagseDag(meerdaagse.getChild("dag-plus1")));
        result.add(parseMeerdaagseDag(meerdaagse.getChild("dag-plus2")));
        result.add(parseMeerdaagseDag(meerdaagse.getChild("dag-plus3")));
        result.add(parseMeerdaagseDag(meerdaagse.getChild("dag-plus4")));
        result.add(parseMeerdaagseDag(meerdaagse.getChild("dag-plus5")));

        return result;
    }

    private WeatherForecast parseMeerdaagseDag(Element element) {
        WeatherForecast forecast = new WeatherForecast();

        forecast.setDate(element.getChildText("datum"));
        forecast.setDayOfWeek(element.getChildText("dagweek"));
        forecast.setChanceSunshine(element.getChildText("kanszon"));
        forecast.setChanceRain(element.getChildText("kansregen"));
        forecast.setMinMmRain(element.getChildText("minmmregen"));
        forecast.setMaxMmRain(element.getChildText("maxmmregen"));
        forecast.setMinTemp(element.getChildText("mintemp"));
        forecast.setMinTempMax(element.getChildText("mintempmax"));
        forecast.setMaxTemp(element.getChildText("maxtemp"));
        forecast.setMaxTempMax(element.getChildText("maxtempmax"));
        forecast.setWindForce(element.getChildText("windkracht"));
        forecast.setWindDirection(element.getChildText("windrichting"));
        forecast.setSnowCms(element.getChildText("sneeuwcms"));

        return forecast;
    }

    private WeatherMeasurement parseActualWeather(Document document) {
        WeatherMeasurement result = new WeatherMeasurement();

        Element weatherStations = document.getRootElement().getChild("weergegevens").getChild("actueel_weer").getChild("weerstations");
        // Weather station Hoek van Holland has id 6330.
        Element dataHvH = weatherStations.getChildren().stream().filter(e -> e.getAttributeValue("id").equals("6330")).findFirst().get();

        result.setDate(dataHvH.getChildText("datum"));
        result.setAirPressure(dataHvH.getChildText("luchtdruk"));
        result.setHumidity(dataHvH.getChildText("luchtvochtigheid"));
        result.setTemperature(dataHvH.getChildText("temperatuurGC"));
        result.setRainMm(dataHvH.getChildText("regenMMPU"));
        result.setAirPressure(dataHvH.getChildText("luchtdruk"));
        result.setWindDirection(dataHvH.getChildText("windrichting"));
        result.setWindSpeedBf(dataHvH.getChildText("windsnelheidBF"));

        return result;
    }

    private void updateCurrentWeatherMeasurements(WeatherMeasurement actualWeather, TeletextUpdatePackage updatePackage) {
        TeletextPage page = new TeletextPage(703);
        TeletextSubpage subpage = page.addNewSubpage();
        subpage.setLayoutTemplateFileName("template-weersverwachting.tpg");

        String format = "%-20s\u0003%-10s";

        subpage.setTextOnLine(0, "WEERSTATION HOEK VAN HOLLAND");
        subpage.setTextOnLine(2, String.format(format, "Temperatuur:", actualWeather.getTemperature() + " graden"));
        subpage.setTextOnLine(3, String.format(format, "Luchtdruk:", actualWeather.getAirPressure()));
        subpage.setTextOnLine(4, String.format(format, "Luchtvochtigheid:", actualWeather.getHumidity()));
        subpage.setTextOnLine(5, String.format(format, "MM regen p/u:", actualWeather.getRainMm()));
        subpage.setTextOnLine(6, String.format(format, "Windrichting:", actualWeather.getWindDirection()));
        subpage.setTextOnLine(7, String.format(format, "Windkracht:", actualWeather.getWindSpeedBf() + " BF"));

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            Date date = dateFormat.parse(actualWeather.getDate());
            subpage.setTextOnLine(15, "Tijdstip meting: \u0003" + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        updatePackage.addTeletextPage(page);
    }

    private void updateMeerdaagse(List<WeatherForecast> meerdaagse, TeletextUpdatePackage updatePackage) {
        if(meerdaagse.isEmpty()) {
            logger.warning("Meerdaagselijst is leeg!");
            return;
        }

        TeletextPage page = new TeletextPage(702);
        TeletextSubpage subpage = page.addNewSubpage();
        subpage.setLayoutTemplateFileName("template-weersverwachting.tpg");

        subpage.setTextOnLine(0, "Verwachting voor de komende 5 dagen");

        String format = "\u0006%-15s\u0007%-4s%-4s%-4s%-4s%-4s";
        String line0 = String.format(format, "\u0003",
                meerdaagse.get(0).getDayOfWeek(),
                meerdaagse.get(1).getDayOfWeek(),
                meerdaagse.get(2).getDayOfWeek(),
                meerdaagse.get(2).getDayOfWeek(),
                meerdaagse.get(3).getDayOfWeek(),
                meerdaagse.get(4).getDayOfWeek());
        subpage.setTextOnLine(2, line0);

        String line1 = String.format(format, "Kans zon:",
                meerdaagse.get(0).getChanceSunshine(),
                meerdaagse.get(1).getChanceSunshine(),
                meerdaagse.get(2).getChanceSunshine(),
                meerdaagse.get(3).getChanceSunshine(),
                meerdaagse.get(4).getChanceSunshine());
        subpage.setTextOnLine(4, line1);

        String line2 = String.format(format, "Neerslagkans:",
                meerdaagse.get(0).getChanceRain(),
                meerdaagse.get(1).getChanceRain(),
                meerdaagse.get(2).getChanceRain(),
                meerdaagse.get(3).getChanceRain(),
                meerdaagse.get(4).getChanceRain());
        subpage.setTextOnLine(5, line2);

        String line4 = String.format(format, "Max temp:",
                meerdaagse.get(0).getMaxTemp(),
                meerdaagse.get(1).getMaxTemp(),
                meerdaagse.get(2).getMaxTemp(),
                meerdaagse.get(3).getMaxTemp(),
                meerdaagse.get(4).getMaxTemp());
        subpage.setTextOnLine(7, line4);

        String line5 = String.format(format, "Min temp:",
                meerdaagse.get(0).getMinTemp(),
                meerdaagse.get(1).getMinTemp(),
                meerdaagse.get(2).getMinTemp(),
                meerdaagse.get(3).getMinTemp(),
                meerdaagse.get(4).getMinTemp());
        subpage.setTextOnLine(8, line5);

        String line6 = String.format(format, "Windrichting:",
                meerdaagse.get(0).getWindDirection(),
                meerdaagse.get(1).getWindDirection(),
                meerdaagse.get(2).getWindDirection(),
                meerdaagse.get(3).getWindDirection(),
                meerdaagse.get(4).getWindDirection());
        subpage.setTextOnLine(10, line6);

        String line7 = String.format(format, "Windkracht:",
                meerdaagse.get(0).getWindForce(),
                meerdaagse.get(1).getWindForce(),
                meerdaagse.get(2).getWindForce(),
                meerdaagse.get(3).getWindForce(),
                meerdaagse.get(4).getWindForce());
        subpage.setTextOnLine(11, line7);

        updatePackage.addTeletextPage(page);
    }

    private void updateWeersVerwachting(String weerbericht, TeletextUpdatePackage updatePackage) {
        try {
            List<List<String>> pageTextList = TextOperations.parseTextToTeletextPageSizeArray(weerbericht, 15);
            TeletextPage weerberichtPage = new TeletextPage(701);

            for(int i=0; i<pageTextList.size(); i++)
            {
                TeletextSubpage subpage = weerberichtPage.addNewSubpage();
                subpage.setLayoutTemplateFileName("template-weersverwachting.tpg");

                String title = "Weersverwachting";

                if(pageTextList.size() > 1) {
                    title += "                   "+(i+1) + "/"+pageTextList.size();
                }

                subpage.setTextOnLine(0, title);
                int line = 2;
                for(int j=0; j < pageTextList.get(i).size(); j++) {
                    subpage.setTextOnLine(line, pageTextList.get(i).get(j).toString());
                    line++;
                }
            }
            updatePackage.addTeletextPage(weerberichtPage);
        }
        catch(Exception e) {
            logger.severe("UpdateWeersVerwachting error: " + e.toString());
        }
    }
}