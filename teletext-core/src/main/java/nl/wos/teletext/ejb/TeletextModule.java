package nl.wos.teletext.ejb;

import nl.wos.teletext.entity.PropertyManager;
import nl.wos.teletext.util.ConfigurationLoader;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Properties;

public abstract class TeletextModule {

    @Autowired protected PropertyManager propertyManager;
    @Autowired protected PhecapConnector phecapConnector;

    protected Properties properties = new ConfigurationLoader().getProperties();

    public abstract void doTeletextUpdate() throws Exception;

    public void setTeletextConnector(PhecapConnector teletextConnector) {
        this.phecapConnector = teletextConnector;
    }

    public void setPropertyManager(PropertyManager propertyManager) {
        this.propertyManager = propertyManager;
    }
}
