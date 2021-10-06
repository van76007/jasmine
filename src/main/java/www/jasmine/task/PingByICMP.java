package www.jasmine.task;

import www.jasmine.Command;
import www.jasmine.config.PingConfig;
import www.jasmine.network.NetworkParameter;
import www.jasmine.report.Report;

import java.util.Arrays;

public class PingByICMP extends AbstractTask {
    PingConfig config;
    public PingByICMP(String host, PingConfig config, NetworkParameter parameter) {
        super(host, Command.PING_ICMP, parameter);
        this.config = config;
    }

    @Override
    public void run() {
        logger.info(String.format("To run: %s on hosts: %s with config timeout %d ms", command.name(), host, config.getWait()));

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    void report(Report report) {
        logger.info(String.format("To report to: %s about host: %s data: %s", config.getReportURL(), report.getHost(), report.getMessage()));
    }
}
