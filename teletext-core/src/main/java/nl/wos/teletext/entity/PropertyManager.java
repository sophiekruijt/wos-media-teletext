package nl.wos.teletext.entity;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class PropertyManager {
    private static final Logger log = Logger.getLogger(String.valueOf(PropertyManager.class));

    private String defaultConfigurationFile = "config.properties";

    public Properties getProperties() {
        return getProperties(defaultConfigurationFile);
    }

    public Properties getProperties(String configurationFileName) {
        Properties properties = new java.util.Properties();
        InputStream input = getClass().getClassLoader().getResourceAsStream(configurationFileName);

        try {
            properties.load(input);
        } catch (IOException ex) {
            log.log(Level.SEVERE, "Exception occured", ex);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException ex) {
                    log.log(Level.SEVERE, "Exception occured", ex);
                }
            }
        }
        return properties;
    }
}
