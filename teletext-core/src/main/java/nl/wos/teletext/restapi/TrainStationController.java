package nl.wos.teletext.restapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.wos.teletext.dao.TrainStationDao;
import nl.wos.teletext.models.TrainStation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.StringWriter;
import java.util.List;

@Controller
@EnableAutoConfiguration
public class TrainStationController {

    @Autowired private TrainStationDao trainStationDao;

    @RequestMapping("/trainstation")
    @ResponseBody
    String getAllTrainStations() {
        final StringWriter sw = new StringWriter();
        final List<TrainStation> trainStations = trainStationDao.getAllTrainStations();
        final ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.writeValue(sw, trainStations);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sw.toString();
    }
}