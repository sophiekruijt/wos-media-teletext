package nl.wos.teletekst.ejb;

import nl.wos.teletekst.entity.PropertyManager;
import nl.wos.teletekst.util.ConfigurationLoader;

import javax.inject.Inject;
import java.util.Properties;

public abstract class TeletextModule {

    @Inject protected PropertyManager propertyManager;
    @Inject protected PhecapConnector phecapConnector;

    protected Properties properties = new ConfigurationLoader().getProperties();

    public abstract void doTeletextUpdate() throws Exception;
}
