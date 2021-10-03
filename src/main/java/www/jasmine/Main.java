package www.jasmine;

import www.jasmine.config.AppConfig;
import www.jasmine.config.ConfigLoader;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String pathToConfigFile = args[0];
        try {
            // Step 1: Load config
            ConfigLoader configLoader = new ConfigLoader();
            AppConfig appConfig = configLoader.buildAppConfig(pathToConfigFile);
            System.out.println("Load appConfig: " + appConfig.toString());
            //
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }
}
