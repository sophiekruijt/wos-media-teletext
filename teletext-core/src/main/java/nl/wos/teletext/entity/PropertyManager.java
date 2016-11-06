package nl.wos.teletext.entity;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class PropertyManager {
    private static final Logger log = Logger.getLogger(String.valueOf(PropertyManager.class));

    private static String defaultConfigurationFile = "config.properties";

    public static Properties getProperties() {
        return getProperties(defaultConfigurationFile);
    }

    public static Properties getProperties(String configurationFileName) {
        Properties properties = new java.util.Properties();
        InputStream input = new PropertyManager().getClass().getClassLoader().getResourceAsStream(configurationFileName);

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
