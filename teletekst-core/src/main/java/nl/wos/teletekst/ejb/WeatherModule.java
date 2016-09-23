package nl.wos.teletekst.ejb;

public class WeatherModule {
}
/*



import nl.wos.teletekst.core.TeletextPage;
import nl.wos.teletekst.core.TeletextSubpage;
import nl.wos.teletekst.core.TeletextUpdatePackage;
import nl.wos.teletekst.entity.PropertyManager;
import nl.wos.teletekst.objects.WeatherMeasurement;
import nl.wos.teletekst.objects.WeatherForecast;
import nl.wos.teletekst.util.TextOperations;
import nl.wos.teletekst.util.Web;


import org.apache.http.util.EntityUtils;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@Singleton
public class WeatherModuleI extends TeletextModule {
    private static final Logger log = Logger.getLogger(WeatherModuleI.class.getName());

    @Inject private PropertyManager propertyManager;
    @Inject private PhecapConnector phecapConnector;

    @Schedule(minute="3,18,33,48", hour="*", persistent=false)
    public void doTeletextUpdate() throws Exception {
        log.info("Weather module is going to update teletext.");

        TeletextUpdatePackage updatePackage = new TeletextUpdatePackage();

        String data = EntityUtils.toString(Web.doWebRequest("http://xml.buienradar.nl/"));
        if (data.isEmpty()) {
            log.severe("Weerdata is empty!");
            return;
        }

        SAXBuilder saxBuilder = new SAXBuilder();
        Document document = saxBuilder.build(new ByteArrayInputStream(data.getBytes("UTF-8")));
        String weerbericht = parseWeerbericht(document);
        List<WeatherForecast> meerdaagse = parseMeerdaagse(document);
        WeatherMeasurement actualWeather = parseActualWeather(document);

        updateWeersVerwachting(weerbericht, updatePackage);
        updateMeerdaagse(meerdaagse, updatePackage);
        updateCurrentWeatherMeasurements(actualWeather, updatePackage);

        updatePackage.generateTextFiles();
        phecapConnector.uploadFilesToTeletextServer(updatePackage);
        log.info("Weather module teletext update is finished.");
    }

    private void updateCurrentWeatherMeasurements(WeatherMeasurement actualWeather, TeletextUpdatePackage updatePackage) throws java.text.ParseException {
        TeletextPage page = new TeletextPage(703);
        TeletextSubpage subpage = page.addNewSubpage();
        subpage.setLayoutTemplateFileName("template-weersverwachting.tpg");

        String format = "%-20s\u0003%-10s";

        subpage.setTextOnLine(0, "MEETSTATION HOEK VAN HOLLAND");
        subpage.setTextOnLine(2, String.format(format, "Temperatuur:", actualWeather.getTemperature() + " graden"));
        subpage.setTextOnLine(3, String.format(format, "Luchtdruk:", actualWeather.getAirPressure()));
        subpage.setTextOnLine(4, String.format(format, "Luchtvochtigheid:", actualWeather.getHumidity()));
        subpage.setTextOnLine(5, String.format(format, "MM regen p/u:", actualWeather.getRegenMM()));
        subpage.setTextOnLine(6, String.format(format, "Windrichting:", actualWeather.getWinddirection()));
        subpage.setTextOnLine(7, String.format(format, "Windkracht:", actualWeather.getWindspeedbf() + " BF"));

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = dateFormat.parse(actualWeather.getDate());
        subpage.setTextOnLine(15, "Tijdstip meting: \u0003" + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(date));

        updatePackage.addTeletextPage(page);
    }

    private void updateMeerdaagse(List<WeatherForecast> meerdaagse, TeletextUpdatePackage updatePackage) {
        if(meerdaagse.isEmpty()) {
            log.warning("Meerdaagselijst is leeg!");
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

    /*private String parseWeerbericht(Document document) {
        return document.getRootElement()
                .getChild("weergegevens")
                .getChild("verwachting_vandaag")
                .getChild("tekst").getText();
    }

    private void updateWeersVerwachting(String weerbericht, TeletextUpdatePackage updatePackage) {
        try {
            List<List<String>> pageTextList = TextOperations.parseTekstToTeletextPageSizeArray(weerbericht, 15);
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
            log.severe("UpdateWeersVerwachting error: " + e.toString());
        }
    }

    private List<WeatherForecast> parseMeerdaagse(Document document) {
        List<WeatherForecast> result = new ArrayList<>();

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
        WeatherForecast result = new WeatherForecast();

        result.setDate(element.getChildText("datum"));
        result.setDayOfWeek(element.getChildText("dagweek"));
        result.setChanceSunshine(element.getChildText("kanszon"));
        result.setChanceRain(element.getChildText("kansregen"));
        result.setMinmmRain(element.getChildText("minmmregen"));
        result.setMaxmmRain(element.getChildText("maxmmregen"));
        result.setMinTemp(element.getChildText("mintemp"));
        result.setMinTempMax(element.getChildText("mintempmax"));
        result.setMaxTemp(element.getChildText("maxtemp"));
        result.setMaxTempMax(element.getChildText("maxtempmax"));
        result.setWindForce(element.getChildText("windkracht"));
        result.setWindDirection(element.getChildText("windrichting"));
        result.setSnowCms(element.getChildText("sneeuwcms"));

        return result;
    }

    private WeatherMeasurement parseActualWeather(Document document) {
        WeatherMeasurement result = new WeatherMeasurement();

        Element weerstations = document.getRootElement()
                .getChild("weergegevens")
                .getChild("actueel_weer")
                .getChild("weerstations");

        for(Element e : weerstations.getChildren()) {
            if(e.getAttributeValue("id").equals("6330")) {
                result.setDate(e.getChildText("datum"));
                result.setAirPressure(e.getChildText("luchtdruk"));
                result.setHumidity(e.getChildText("luchtvochtigheid"));
                result.setTemperature(e.getChildText("temperatuurGC"));
                result.setRegenMM(e.getChildText("regenMMPU"));
                result.setAirPressure(e.getChildText("luchtdruk"));
                result.setWinddirection(e.getChildText("windrichting"));
                result.setWindspeedbf(e.getChildText("windsnelheidBF"));
                break;
            }
        }

        return result;
    }
}*/