package www.jasmine;

import www.jasmine.model.config.AppConfig;
import www.jasmine.model.network.NetworkParameter;
import www.jasmine.network.NetworkParameterBuilder;
import www.jasmine.model.report.Report;
import www.jasmine.report.Reporter;
import www.jasmine.task.AbstractTask;
import www.jasmine.task.PingByHTTP;
import www.jasmine.task.PingByICMP;
import www.jasmine.task.TraceRoute;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Processor {
    AppConfig appConfig;
    Logger logger = SingletonLogger.SingletonLogger().logger;

    ScheduledExecutorService executorForAllHosts = Executors.newScheduledThreadPool(2);
    ScheduledExecutorService executorForAllTasks = Executors.newScheduledThreadPool(2);
    ScheduledExecutorService scheduledShutdownExecutor = Executors.newSingleThreadScheduledExecutor();

    Reporter reporter;

    public Processor(AppConfig appConfig) {
        this.appConfig = appConfig;
        this.reporter = new Reporter(appConfig);
    }

    public void run() {
        NetworkParameterBuilder builder = new NetworkParameterBuilder();
        NetworkParameter networkParameter = builder.buildNetworkParameter();
        logger.info("NetworkParameter: " + networkParameter.toString());
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
                logger.info("Finish running network commands for a host: " + host);
            }, appConfig.getShutdownPeriod(), TimeUnit.SECONDS);
        }
        scheduledShutdownExecutor.shutdown();
    }

    private void runNetworkCommandOnAHost(String host, NetworkParameter networkParameter, AppConfig config) {
        AbstractTask pingByHTTP = new PingByHTTP(host, config.getPingConfig(), networkParameter);
        AbstractTask pingByICMP = new PingByICMP(host, config.getPingConfig(), networkParameter);
        AbstractTask traceRoute = new TraceRoute(host, config.getTracertConfig(), networkParameter);
        AbstractTask[] tasks = new AbstractTask[] { pingByHTTP, pingByICMP, traceRoute };
        Runnable runnable = () -> {
            List<Report> reports = Arrays.stream(tasks).sequential().map(AbstractTask::run).collect(Collectors.toList());
            reporter.forwardReports(host, reports);
        };
        executorForAllTasks.scheduleAtFixedRate(runnable, 0, config.getDelay(), TimeUnit.MILLISECONDS);
    }
}
