package nl.wos.teletext.restapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.wos.teletext.components.PhecapConnector;
import nl.wos.teletext.dao.BerichtDao;
import nl.wos.teletext.models.Bericht;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

@Controller
@EnableAutoConfiguration
public class StatusCheck {
    @Autowired private PhecapConnector phecapConnector;

    @RequestMapping("/status")
    @ResponseBody
    private String getAllMessages() {
        List<String> errors = new ArrayList<>();
        if (phecapConnector.checkConnection()) {
            return "OK";
        }
        else {
            errors.add("Phecap connection is down");
            return errors.toString();
        }
    }
}
