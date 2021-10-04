package www.jasmine;

import www.jasmine.config.AppConfig;
import www.jasmine.task.PingByHTTPTask;
import www.jasmine.task.PingByICMPTask;
import www.jasmine.task.TracertTask;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Processor {
    private final static long SHUTDOWN_AFTER_PERIOD = 10; // seconds
    AppConfig appConfig;
    Command command;
    Logger logger = SingletonLogger.SingletonLogger().logger;
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

    public Processor(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public boolean run() {
        if(!isValidCommand()) {
            logger.severe("Invalid command in the configuration: " + appConfig.getCommand());
            return false;
        }
        switch (command) {
            case PING_ICMP:
                final PingByICMPTask pingByICMPTask = new PingByICMPTask(appConfig.getPingConfig());
                runPeriodicTaskThenStop(() -> {
                    pingByICMPTask.run();
                }, appConfig.getPingConfig().getDelay());
                break;
            case PING_HTTP:
                final PingByHTTPTask pingByHTTPTask = new PingByHTTPTask(appConfig.getPingConfig());
                runPeriodicTaskThenStop(() -> {
                    pingByHTTPTask.run();
                }, appConfig.getPingConfig().getDelay());
                break;
            case TRACERT:
                final TracertTask tracertTask = new TracertTask(appConfig.getTracertConfig());
                runPeriodicTaskThenStop(() -> {
                    tracertTask.run();
                }, appConfig.getTracertConfig().getDelay());
                break;
            default:
                logger.warning("Unhandled command: " + command.name());
                break;
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

    private void runPeriodicTaskThenStop(Runnable runnableTask, Long delay) {
        Future resultFuture = executorService
                .scheduleAtFixedRate(runnableTask, 0, delay, TimeUnit.MILLISECONDS);
        executor.schedule(() -> {
            resultFuture.cancel(true);
            executorService.shutdown();
        }, SHUTDOWN_AFTER_PERIOD, TimeUnit.SECONDS);
        executor.shutdown();
    }
}
