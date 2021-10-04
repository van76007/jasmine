package www.jasmine;

import www.jasmine.config.AppConfig;
import www.jasmine.config.ConfigLoader;

import java.io.IOException;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) {
        Logger logger = SingletonLogger.SingletonLogger().logger;

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
            // Step 2: Execute the command
            Processor processor = new Processor(appConfig);
            processor.run();
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }
}
