package nl.wos.teletekst.rest;

import nl.wos.teletekst.dao.BerichtDao;
import org.jboss.resteasy.annotations.cache.NoCache;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Stateless
@NoCache
@Path("/")
public class MessageController {
    @Inject private BerichtDao berichtDao;

    @GET
    @Path("/bericht/")
    @Produces(MediaType.APPLICATION_JSON)
    public Object getBericht() {
        return berichtDao.getAll();
    }
}
