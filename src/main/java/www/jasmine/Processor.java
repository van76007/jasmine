package www.jasmine;

import www.jasmine.config.AppConfig;
import www.jasmine.network.NetworkParameter;
import www.jasmine.network.NetworkParameterBuilder;
import www.jasmine.task.AbstractTask;
import www.jasmine.task.PingByHTTP;
import www.jasmine.task.PingByICMP;
import www.jasmine.task.TraceRoute;

import java.util.concurrent.*;
import java.util.logging.Logger;

public class Processor {
    AppConfig appConfig;
    Command command;
    Logger logger = SingletonLogger.SingletonLogger().logger;
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(4);
    // ScheduledExecutorService executorForWorkers = Executors.newScheduledThreadPool(4);

    public Processor(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public boolean run() {
        if(!isValidCommand()) {
            logger.severe("Invalid command in the configuration: " + appConfig.getCommand());
            return false;
        }
        NetworkParameterBuilder builder = new NetworkParameterBuilder();
        NetworkParameter networkParameter = builder.buildNetworkParameter();

        runNetworkCommand(appConfig.getHosts(), networkParameter, appConfig);
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

    // V.1
    /*
    private void runNetworkCommand(String[] hosts, NetworkParameter networkParameter, AppConfig config) {
        for(String host: hosts) {
            runNetworkCommandOnAHost(host, networkParameter, config);
        }
    }

    private void runNetworkCommandOnAHost(String host, NetworkParameter networkParameter, AppConfig config) {
        AbstractTask pingByHTTP = new PingByHTTP(host, config.getPingConfig(), networkParameter);
        AbstractTask pingByICMP = new PingByICMP(host, config.getPingConfig(), networkParameter);
        AbstractTask traceRoute = new TraceRoute(host, config.getTracertConfig(), networkParameter);

        AbstractTask tasks[] = new AbstractTask[] { pingByHTTP, pingByICMP, traceRoute };
        for (AbstractTask task : tasks) {
            Runnable runnable = () -> {
                task.run();
            };
            Future resultFuture = executorService
                    .scheduleAtFixedRate(runnable, 0, config.getDelay(), TimeUnit.MILLISECONDS);
            executor.schedule(() -> {
                resultFuture.cancel(true);
                executorService.shutdown();
                System.out.println("FINISH ALL");
            }, appConfig.getShutdownPeriod(), TimeUnit.SECONDS);
        }
        executor.shutdown();
    }
    */

    // V.2
    private void runNetworkCommand(String[] hosts, NetworkParameter networkParameter, AppConfig config) {
        for(String host: hosts) {
            Runnable runnable = () -> {
                runNetworkCommandOnAHost(host, networkParameter, config);
            };
            Future resultFuture = executorService.submit(runnable);
            executor.schedule(() -> {
                resultFuture.cancel(true);
                executorService.shutdown();
            }, appConfig.getShutdownPeriod(), TimeUnit.SECONDS);
        }
    }

    private void runNetworkCommandOnAHost(String host, NetworkParameter networkParameter, AppConfig config) {
        AbstractTask pingByHTTP = new PingByHTTP(host, config.getPingConfig(), networkParameter);
        AbstractTask pingByICMP = new PingByICMP(host, config.getPingConfig(), networkParameter);
        AbstractTask traceRoute = new TraceRoute(host, config.getTracertConfig(), networkParameter);

        AbstractTask tasks[] = new AbstractTask[] { pingByHTTP, pingByICMP, traceRoute };
        for (AbstractTask task : tasks) {
            Runnable runnable = () -> {
                task.run();
            };
            Future resultFuture = executorService
                    .scheduleAtFixedRate(runnable, 0, config.getDelay(), TimeUnit.MILLISECONDS);
            executor.schedule(() -> {
                resultFuture.cancel(true);
                executorService.shutdown();
                System.out.println("FINISH ALL");
            }, appConfig.getShutdownPeriod(), TimeUnit.SECONDS);
        }
        executor.shutdown();
    }
}
