package nl.wos.teletext.rest;

import nl.wos.teletext.components.NewsModule;
import nl.wos.teletext.components.PublicTransportModule;
import nl.wos.teletext.components.SportModule;
import org.jboss.resteasy.annotations.cache.NoCache;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Stateless
@NoCache
@Path("/update")
public class TeletextActionController {
    @Inject private PublicTransportModule publicTransportModule;
    @Inject private NewsModule newsModule;
    @Inject private SportModule sportModule;
    //@Inject private WeatherModule weatherModule;

    private static final String SUCCESS_RESULT="<result>success</result>";
    private static final String FAILURE_RESULT="<result>failure, teletext modules allowed are: train_departures, news, sport or weather</result>";

    @GET
    @Path("/module/{moduleName}")
    @Produces(MediaType.APPLICATION_XML)
    public String executeAction(@PathParam("moduleName") String moduleName) {
        switch (moduleName) {
            case "train_departures":
                publicTransportModule.doTeletextUpdate();
                return SUCCESS_RESULT;
            case "news":
                newsModule.doTeletextUpdate();
                return SUCCESS_RESULT;
            case "sport":
                sportModule.doTeletextUpdate();
                return SUCCESS_RESULT;
            case "weather":
                //weatherModule.doTele();
                return SUCCESS_RESULT;
            default:
                return FAILURE_RESULT;
        }
    }
}
