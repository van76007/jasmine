package www.jasmine.task;

import www.jasmine.Command;
import www.jasmine.model.config.TracertConfig;
import www.jasmine.model.network.NetworkParameter;
import www.jasmine.network.TraceRouteCommand;
import www.jasmine.model.report.Report;

import java.net.UnknownHostException;

public class TraceRoute extends AbstractTask {
    TracertConfig config;

    public TraceRoute(String host, TracertConfig config, NetworkParameter parameter) {
        super(host, Command.TRACEROUTE, parameter);
        this.config = config;
    }

    @Override
    public Report run() {
        logger.info(String.format("To run: %s on hosts: %s with config pause %d ms between probing the same hop", command.name(), host, config.getPause()));

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
