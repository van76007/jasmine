package www.jasmine;

import www.jasmine.model.config.AppConfig;
import www.jasmine.config.ConfigLoader;

import java.io.IOException;
import java.util.logging.Logger;

public class Main {
    static {
        String nameOfOS = System.getProperty("os.name").toLowerCase();
        String bitnessOfJVM = System.getProperty("os.arch");

        try {
            // Tested on Centos OS, OpenJDK 64-Bit
            if ((nameOfOS.contains("nix") || nameOfOS.contains("nux") || nameOfOS.contains("aix"))) {
                if (bitnessOfJVM.contains("64")) {
                    System.load("/tmp/libpcap64.so");
                } else {
                    System.load("/tmp/libpcap64.so");
                }
            }
            // ToDo: Test in Windows machine, which I do not have at the moment
            //       Note that there are 2 different versions of Packet.dll and wpcap.dll: 64 and 32-bit
            if (nameOfOS.contains("win")) {
                System.load("Packet.dll");
                System.load("wpcap.dll");
            }
            // Tested on Mac OSX BigSur, OpenJDK 64-Bit
            if (nameOfOS.contains("mac")) {
                System.load("/tmp/libpcap.1.8.1.dylib");
            }
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Native code library failed to load.\n" + e);
            System.exit(1);
        }
    }

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
            logger.severe(e.getMessage());
        }
    }
}
