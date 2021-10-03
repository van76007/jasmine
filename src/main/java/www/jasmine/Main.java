package www.jasmine;

import www.jasmine.config.AppConfig;
import www.jasmine.config.ConfigLoader;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger;
    static {
        logger = Logger.getLogger(Main.class.getName());
        try {
            FileHandler fileHandler = new FileHandler("jasmine.log");
            logger.addHandler(fileHandler);
            fileHandler.setLevel(Level.INFO);
            logger.setLevel(Level.INFO);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            logger.warning("Usage: java -jar uber-jasmine-1.0-SNAPSHOT.jar path2config.properties");
            System.exit(0);
        }
        String pathToConfigFile = args[0];
        try {
            // Step 1: Load config
            ConfigLoader configLoader = new ConfigLoader();
            AppConfig appConfig = configLoader.buildAppConfig(pathToConfigFile);
            logger.info("Load appConfig: " + appConfig.toString());
            //
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }
}
