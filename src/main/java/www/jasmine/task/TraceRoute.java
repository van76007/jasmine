package www.jasmine.task;

import www.jasmine.Command;
import www.jasmine.config.TracertConfig;
import www.jasmine.network.NetworkParameter;
import www.jasmine.report.Report;

public class TraceRoute extends AbstractTask {
    TracertConfig config;
    public TraceRoute(String host, TracertConfig config, NetworkParameter parameter) {
        super(host, Command.TRACEROUTE, parameter);
        this.config = config;
    }

    @Override
    public void run() {
        logger.info(String.format("To run: %s on hosts: %s with config timeout %d ms", command.name(), host, config.getPause()));

        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    void report(Report report) {
        logger.info(String.format("To report to: %s about host: %s data: %s", config.getReportURL(), report.getHost(), report.getMessage()));
    }
}
