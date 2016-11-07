package nl.wos.teletext.components;

import nl.wos.teletext.dao.SportPouleDao;
import nl.wos.teletext.models.SportPoule;
import nl.wos.teletext.models.WeatherForecast;
import nl.wos.teletext.models.WeatherMeasurement;
import nl.wos.teletext.util.SportModuleDataParser;
import nl.wos.teletext.util.Web;
import org.apache.http.util.EntityUtils;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class SportModule extends TeletextModule {
    private static final Logger logger = Logger.getLogger(SportModule.class.getName());

    @Autowired private SportPouleDao sportPouleDao;

    private SportModuleDataParser parser = new SportModuleDataParser();

    @Scheduled(fixedRate = 3600000, initialDelay = 3600000)
    //@Schedule(minute="*",hour="*/100", persistent=false)
    public void doTeletextUpdate() {
        logger.info("Sport module is going to update teletext.");

        List<SportPoule> poules = sportPouleDao.getAllSportPoules();
        try {/*
            String sportData = EntityUtils.toString(Web.doWebRequest("http://sportstanden.infothuis.nl/public/internet-rss.php"), "UTF-8");
            if (!sportData.isEmpty()) {
                try {
                    /*SAXBuilder saxBuilder = new SAXBuilder();
                    Document document = saxBuilder.build(new ByteArrayInputStream(weatherData.getBytes("UTF-8")));
                    String weerbericht = parseWeerbericht(document);
                    List<WeatherForecast> meerdaagse = parseMeerdaagse(document);
                    WeatherMeasurement actualWeather = parseActualWeather(document);

                    //updateWeersVerwachting(weerbericht, updatePackage);
                    //updateMeerdaagse(meerdaagse, updatePackage);
                    //updateCurrentWeatherMeasurements(actualWeather, updatePackage);

                    updatePackage.generateTextFiles();
                    phecapConnector.uploadFilesToTeletextServer(updatePackage);
                } catch (Exception ex) {
                    logger.log(Level.SEVERE, "Exeption occured", ex);
                }


                logger.info("Weather module teletext update is finished.");
            }*/
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Exception occured", ex);
        }


        /*if(data.isEmpty()) {
            log.severe("Gedownloade sportdata is null or leeg!, teletext update will be cancelled");
            return;
        }

        List<SportPouleRssItem> teletextData = parser.parsePoules(data, poules);
        log.info(teletextData.toString());

        TeletextUpdatePackage updatePackage = new TeletextUpdatePackage();
        updatePackage.addRemovePagesTask(611, 645);

        for(int i = 0; i < poules.size(); i++)
        {
            TeletextPage sportPage = new TeletextPage(i + 611);
            TeletextSubpage page1 = sportPage.addNewSubpage();
            page1.setLayoutTemplateFileName("template-sport1.tpg");

            TeletextSubpage page2 = sportPage.addNewSubpage();
            page2.setLayoutTemplateFileName("sportUitslagen2Template");


            // TODO Inhoud aan pagina's toevoegen

            //renderer.AddContentToTextPage1(sportPoules[i], ref subPage1);
            //renderer.AddContentToTextPage2(sportPoules[i], ref subPage2);

            //sportPage.AddNewTextPage(subPage1);
            //sportPage.AddNewTextPage(subPage2);
        }

        updatePackage.generateTextFiles();
        phecapConnector.uploadFilesToTeletextServer(updatePackage);*/
    }
}