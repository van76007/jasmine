package www.jasmine.taskV1;

import www.jasmine.Command;
import www.jasmine.config.TracertConfig;
import www.jasmine.network.NetworkParameter;
import www.jasmine.network.TraceRouteCommand;
import www.jasmine.report.Report;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;

public class TracertTask extends NetworkTask {
    TracertConfig config;
    ExecutorService executor;
    NetworkParameter parameter;

    public TracertTask(String[] hosts, TracertConfig config, ExecutorService executor, NetworkParameter networkParameter) {
        super(hosts);
        this.config = config;
        this.command = Command.TRACEROUTE;
        this.executor = executor;
        this.parameter = networkParameter;
    }

    @Override
    public void run() {
        logger.info(String.format("To run: %s on hosts: %s with config timeout %d ms", command.name(), Arrays.toString(hosts), config.getPause()));
        for(String host: hosts) {
            Runnable runnable = () -> {
                Report report = null;
                try {
                    InetAddress remoteInetAddress = InetAddress.getByName(host);
                    TraceRouteCommand traceRoute = new TraceRouteCommand(parameter, remoteInetAddress, config);
                    report = traceRoute.ping();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                if (report == null) {
                    report(new Report(host, "Unable to trace route"));
                } else {
                    report(report);
                }
            };
            this.executor.submit(runnable);
        }
    }

    @Override
    void report(Report report) {
        logger.info(String.format("To report to: %s about host: %s data: %s", config.getReportURL(), report.getHost(), report.getMessage()));
    }
}
