package www.jasmine.task;

import www.jasmine.Command;
import www.jasmine.config.PingConfig;
import www.jasmine.network.NetworkParameter;
import www.jasmine.network.Ping;
import www.jasmine.report.Report;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;

public class PingByICMPTask extends NetworkTask {
    PingConfig config;
    ExecutorService executor;
    NetworkParameter networkParameter;

    public PingByICMPTask(String[] hosts, PingConfig config, ExecutorService executor, NetworkParameter networkParameter) {
        super(hosts);
        this.config = config;
        this.command = Command.PING_ICMP;
        this.executor = executor;
        this.networkParameter = networkParameter;
    }

    @Override
    public void run() {
        logger.info(String.format("To run: %s on hosts: %s with config timeout %d ms", command.name(), Arrays.toString(hosts), config.getTimeout()));
        for(String host: hosts) {
            Runnable runnable = () -> {
                Ping ping = new Ping(networkParameter, host);
                report(ping.pingByICMP());
            };
            this.executor.submit(runnable);
        }
    }

    @Override
    void report(Report report) {
        logger.info(String.format("To report to: %s about host: %s data: %s", config.getReportURL(), report.getHost(), report.getMessage()));
    }
}
