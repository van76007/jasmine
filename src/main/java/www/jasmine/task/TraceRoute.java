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

    public TraceRoute(String host, TracertConfig config, NetworkParameter parameter) {
        super(host, Command.TRACEROUTE, parameter);
        this.config = config;
    }

    @Override
    public Report run() {
        logger.info(String.format("To run: %s on hosts: %s with config timeout %d ms", command.name(), host, config.getPause()));

        Report report = null;
        try {
            TraceRouteCommand traceRoute = new TraceRouteCommand(parameter, host, config);
            report = traceRoute.trace();
        } catch (UnknownHostException e) {
            logger.severe(e.getMessage());
        }
        if (report == null) {
            return new Report(host, "Unable to trace route to host: " + host, Command.TRACEROUTE);
        } else {
            return report;
        }
    }
}
