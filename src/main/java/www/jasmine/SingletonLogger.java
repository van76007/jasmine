package www.jasmine;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SingletonLogger {
    private final static String APP_NAME = "jasmine";
    private final static String LOG_FILE_NAME = APP_NAME + ".log";
    private static SingletonLogger instance = null;
    public Logger logger;

    private SingletonLogger() {
        logger = Logger.getLogger(APP_NAME);
        try {
            FileHandler fileHandler = new FileHandler(LOG_FILE_NAME);
            logger.addHandler(fileHandler);
            fileHandler.setLevel(Level.INFO);
            logger.setLevel(Level.INFO);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SingletonLogger SingletonLogger() {
        if (instance == null) {
            instance = new SingletonLogger();
        }
        return instance;
    }
}
