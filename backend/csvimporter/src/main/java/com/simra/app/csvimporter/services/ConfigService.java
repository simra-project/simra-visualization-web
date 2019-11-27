package main.java.com.simra.app.csvimporter.services;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * The type Config properties service.
 */
public class ConfigService {
    private static final Logger logger = Logger.getLogger(ConfigService.class);


    public static Properties config;

    /**
     * Config properties service.
     */
    public ConfigService() {
        readProperties();
    }

    public static void readProperties() {
        try (InputStream input = ConfigService.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            config = prop;
        } catch (IOException ex) {
            logger.error(ex);
        }
    }
}
