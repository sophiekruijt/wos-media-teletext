package nl.wos.teletext.restapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.wos.teletext.dao.SportPouleDao;
import nl.wos.teletext.models.SportPoule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.StringWriter;
import java.util.List;
import java.util.Map;

@Controller
@EnableAutoConfiguration
public class SportPouleController {

    @Autowired private SportPouleDao sportPouleDao;

    @RequestMapping("/sportpoules")
    @ResponseBody
    String getAllSportPoules() {
        final StringWriter sw = new StringWriter();
        final Map<String, SportPoule> sportPoules = sportPouleDao.getAllSportPoules();
        final ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.writeValue(sw, sportPoules);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sw.toString();
    }
}