package nl.wos.teletext.ejb;

import nl.wos.teletext.entity.PropertyManager;
import nl.wos.teletext.util.ConfigurationLoader;

import javax.inject.Inject;
import java.util.Properties;

public abstract class TeletextModule {

    @Inject protected PropertyManager propertyManager;
    @Inject protected PhecapConnector phecapConnector;

    protected Properties properties = new ConfigurationLoader().getProperties();

    public abstract void doTeletextUpdate() throws Exception;

    public void setTeletextConnector(PhecapConnector teletextConnector) {
        this.phecapConnector = teletextConnector;
    }
}
