package www.jasmine;

import www.jasmine.config.AppConfig;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;

public class Processor {
    AppConfig appConfig;
    Command command;
    Logger logger = SingletonLogger.SingletonLogger().logger;
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public Processor(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public boolean run() {
        if(!isValidCommand()) {
            logger.severe("Invalid command in the configuration: " + this.appConfig.getCommand());
            return false;
        }
        switch (command) {
            case PING_ICMP:
            case PING_HTTP:
            case TRACERT:
            default:
                logger.warning("Unhandled command: " + command.name());
        }
        return true;
    }

    private boolean isValidCommand() {
        try {
            this.command = Command.valueOf(this.appConfig.getCommand());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
