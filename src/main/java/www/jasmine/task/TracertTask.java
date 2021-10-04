package www.jasmine.task;

import www.jasmine.Command;
import www.jasmine.config.TracertConfig;
import www.jasmine.report.Report;

import java.util.Arrays;

public class TracertTask extends NetworkTask {
    TracertConfig config;

    public TracertTask(String[] hosts, TracertConfig config) {
        super(hosts);
        this.config = config;
        this.command = Command.TRACERT;
    }

    @Override
    public void run() {
        logger.info(String.format("To run: %s on hosts: %s with config timeout %d ms", command.name(), Arrays.toString(hosts), config.getTimeout()));
    }

    @Override
    void report(Report report) {
        logger.info(String.format("To report to: %s about host: %s data: %s", config.getReportURL(), report.getHost(), report.getMessage()));
    }
}
