package nl.wos.teletext.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.wos.teletext.dao.BerichtDao;
import nl.wos.teletext.entity.Bericht;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.StringWriter;
import java.util.List;

@Controller
@EnableAutoConfiguration
public class MessageController {
    @Autowired BerichtDao berichtDao;

    @RequestMapping("/")
    @ResponseBody
    private String getAllMessages() {
        final StringWriter sw = new StringWriter();
        final List<Bericht> berichten = berichtDao.getAllBerichten();
        final ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.writeValue(sw, berichten);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sw.toString();
    }
}
