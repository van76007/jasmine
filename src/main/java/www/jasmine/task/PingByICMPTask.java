package www.jasmine.task;

import www.jasmine.Command;
import www.jasmine.config.PingConfig;
import www.jasmine.report.Report;

import java.util.Arrays;

public class PingByICMPTask extends NetworkTask {
    PingConfig config;

    public PingByICMPTask(String[] hosts, PingConfig config) {
        super(hosts);
        this.config = config;
        this.command = Command.PING_ICMP;
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
