package nl.wos.teletext.components;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Properties;

public abstract class TeletextModule {

    @Autowired protected PhecapConnector phecapConnector;

    protected Properties properties = new PropertyManager().getProperties();

    public abstract void doTeletextUpdate() throws Exception;

    public void setTeletextConnector(PhecapConnector teletextConnector) {
        this.phecapConnector = teletextConnector;
    }
}
