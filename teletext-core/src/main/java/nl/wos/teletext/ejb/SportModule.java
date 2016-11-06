package nl.wos.teletext.ejb;

import nl.wos.teletext.dao.SportPouleDao;
import nl.wos.teletext.entity.PropertyManager;
import nl.wos.teletext.entity.SportPoule;
import nl.wos.teletext.util.SportModuleDataParser;
import nl.wos.teletext.util.Web;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class SportModule extends TeletextModule {
    private static final Logger log = Logger.getLogger(SportModule.class.getName());

    @Autowired private SportPouleDao sportPouleDao;

    private SportModuleDataParser parser = new SportModuleDataParser();

    @Scheduled(fixedRate = 3600000, initialDelay = 3600000)
    //@Schedule(minute="*",hour="*/100", persistent=false)
    public void doTeletextUpdate() {
        log.info("Sport module is going to update teletext.");

        List<SportPoule> poules = sportPouleDao.findAllOrderedByProperty("naam");
        try {
            String data = EntityUtils.toString(Web.doWebRequest("http://sportstanden.infothuis.nl/public/internet-rss.php"), "UTF-8");
        } catch (Exception ex) {
            log.log(Level.SEVERE, "Exception occured", ex);
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