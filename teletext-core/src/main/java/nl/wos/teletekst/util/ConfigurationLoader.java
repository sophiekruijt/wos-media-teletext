package nl.wos.teletekst.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationLoader {
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
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
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
