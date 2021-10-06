package www.jasmine.task;

import www.jasmine.Command;
import www.jasmine.config.TracertConfig;
import www.jasmine.network.NetworkParameter;
import www.jasmine.network.TraceRouteCommand;
import www.jasmine.report.Report;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;

public class TraceRoute extends AbstractTask {
    TracertConfig config;
    ExecutorService executor;

    public TraceRoute(String host, TracertConfig config, NetworkParameter parameter, ExecutorService executor) {
        super(host, Command.TRACEROUTE, parameter);
        this.config = config;
        this.executor = executor;
    }

    @Override
    public void run() {
        logger.info(String.format("To run: %s on hosts: %s with config timeout %d ms", command.name(), host, config.getPause()));

        Report report = null;
        try {
            InetAddress remoteInetAddress = InetAddress.getByName(host);
            TraceRouteCommand traceRoute = new TraceRouteCommand(parameter, remoteInetAddress, config);
            report = traceRoute.ping();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        if (report == null) {
            report(new Report(host, "Unable to trace route to host: " + host));
        } else {
            report(report);
        }
    }

    @Override
    void report(Report report) {
        logger.info(String.format("To report result of %s about host: %s data: %s", command, report.getHost(), report.getMessage()));
    }
}
