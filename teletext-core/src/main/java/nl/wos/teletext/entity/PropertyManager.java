package nl.wos.teletext.entity;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@Startup
@Singleton
public class PropertyManager {
    private static final Logger log = Logger.getLogger(String.valueOf(PropertyManager.class));
    private Properties properties = new Properties();

    @PostConstruct
    public void init() {
        String propFileName = "config.properties";
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(propFileName);

        if (inputStream != null) {
            try {
                properties.load(inputStream);
            }
            catch (IOException ex) {
                log.log(Level.SEVERE, "Exception occured", ex);
            }
        } else {
            log.info("Property file '" + propFileName + "' not found in the classpath");
        }
    }
}
