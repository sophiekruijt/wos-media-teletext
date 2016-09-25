package nl.wos.teletext.util;

import nl.wos.teletext.objects.PublicTransportModuleHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigurationLoader {
    private static final Logger log = Logger.getLogger(ConfigurationLoader.class.getName());

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

    /*public boolean getBooleanByKey(String key) {
        return Boolean.getBoolean(properties.getProperty(key));
    }

    public int getIntByKey(String key) {
        return Integer.getInteger(properties.getProperty(key));
    }*/
}
