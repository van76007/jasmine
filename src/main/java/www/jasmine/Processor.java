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
    Logger logger = SingletonLogger.SingletonLogger().logger;

    ScheduledExecutorService executorForAllHosts = Executors.newScheduledThreadPool(2);
    ScheduledExecutorService executorForAllTasks = Executors.newScheduledThreadPool(2);
    ScheduledExecutorService executorForNetworkingTasks = Executors.newScheduledThreadPool(2);
    ScheduledExecutorService scheduledShutdownExecutor = Executors.newSingleThreadScheduledExecutor();

    public Processor(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public void run() {
        NetworkParameterBuilder builder = new NetworkParameterBuilder();
        NetworkParameter networkParameter = builder.buildNetworkParameter();

        runNetworkCommand(appConfig.getHosts(), networkParameter, appConfig);
    }

    private void runNetworkCommand(String[] hosts, NetworkParameter networkParameter, AppConfig config) {
        for(String host: hosts) {
            Runnable runnable = () -> {
                runNetworkCommandOnAHost(host, networkParameter, config);
            };
            Future resultFuture = executorForAllHosts.submit(runnable);
            scheduledShutdownExecutor.schedule(() -> {
                resultFuture.cancel(true);
                executorForAllTasks.shutdown();
                executorForAllHosts.shutdown();
                logger.info("Finish running network commands for all hosts");
            }, appConfig.getShutdownPeriod(), TimeUnit.SECONDS);
        }
        scheduledShutdownExecutor.shutdown();
    }

    private void runNetworkCommandOnAHost(String host, NetworkParameter networkParameter, AppConfig config) {
        AbstractTask pingByHTTP = new PingByHTTP(host, config.getPingConfig(), networkParameter);
        AbstractTask pingByICMP = new PingByICMP(host, config.getPingConfig(), networkParameter, executorForNetworkingTasks);
        AbstractTask traceRoute = new TraceRoute(host, config.getTracertConfig(), networkParameter, executorForNetworkingTasks);

        AbstractTask[] tasks = new AbstractTask[] { pingByHTTP, pingByICMP, traceRoute };
        for (AbstractTask task : tasks) {
            Runnable runnable = task::run;
            executorForAllTasks
                    .scheduleAtFixedRate(runnable, 0, config.getDelay(), TimeUnit.MILLISECONDS);
        }
    }
}
