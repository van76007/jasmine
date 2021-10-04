package www.jasmine;

import www.jasmine.config.AppConfig;
import www.jasmine.network.NetworkParameter;
import www.jasmine.network.NetworkParameterBuilder;
import www.jasmine.task.PingByHTTPTask;
import www.jasmine.task.PingByICMPTask;
import www.jasmine.task.TracertTask;

import java.util.concurrent.*;
import java.util.logging.Logger;

public class Processor {
    AppConfig appConfig;
    Command command;
    Logger logger = SingletonLogger.SingletonLogger().logger;
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    ExecutorService executorForWorkers = Executors.newFixedThreadPool(4);

    public Processor(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public boolean run() {
        if(!isValidCommand()) {
            logger.severe("Invalid command in the configuration: " + appConfig.getCommand());
            return false;
        }
        NetworkParameterBuilder builder = new NetworkParameterBuilder();
        // ToDo: Use this parameter in the tasks
        NetworkParameter networkParameter = builder.buildNetworkParameter();
        switch (command) {
            case PING_ICMP:
                final PingByICMPTask pingByICMPTask = new PingByICMPTask(appConfig.getHosts(), appConfig.getPingConfig(), executorForWorkers, networkParameter);
                runPeriodicTaskThenStop(pingByICMPTask::run, appConfig.getDelay());
                break;
            case PING_HTTP:
                final PingByHTTPTask pingByHTTPTask = new PingByHTTPTask(appConfig.getHosts(), appConfig.getPingConfig(), executorForWorkers);
                runPeriodicTaskThenStop(pingByHTTPTask::run, appConfig.getDelay());
                break;
            case TRACERT:
                final TracertTask tracertTask = new TracertTask(appConfig.getHosts(), appConfig.getTracertConfig());
                runPeriodicTaskThenStop(tracertTask::run, appConfig.getDelay());
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
            executorForWorkers.shutdown();
        }, appConfig.getShutdownPeriod(), TimeUnit.SECONDS);
        executor.shutdown();
    }
}
