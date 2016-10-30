package nl.wos.teletext.steps;

import cucumber.api.PendingException;
import cucumber.api.java8.En;
import nl.wos.teletext.dao.TrainStationDao;
import nl.wos.teletext.ejb.PublicTransportModule;
import nl.wos.teletext.ejb.TeletextModule;
import nl.wos.teletext.entity.TrainStation;
import org.mockito.Mock;

import java.util.LinkedList;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TeletextModuleSteps implements En {
    public TeletextModuleSteps() {
        PublicTransportModule publicTransportModule = new PublicTransportModule();

        When("^The application does a telext-update of the \"([^\"]*)\"$", (String teletextModule) -> {
            switch(teletextModule) {
                case "PublicTransportModule":
                    doTeletextUpdate(publicTransportModule);
                    break;
            }
        });
    }

    private void doTeletextUpdate(TeletextModule module) {
        try {
            module.doTeletextUpdate();
        } catch (Exception e) {
            fail(e.toString());
        }
    }
}
