package www.jasmine.taskV1;

import www.jasmine.Command;
import www.jasmine.config.PingConfig;
import www.jasmine.network.NetworkParameter;
import www.jasmine.network.PingCommand;
import www.jasmine.report.Report;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;

public class PingByICMPTask extends NetworkTask {
    PingConfig config;
    ExecutorService executor;
    NetworkParameter parameter;

    public PingByICMPTask(String[] hosts, PingConfig config, ExecutorService executor, NetworkParameter networkParameter) {
        super(hosts);
        this.config = config;
        this.command = Command.PING_ICMP;
        this.executor = executor;
        this.parameter = networkParameter;
    }

    @Override
    public void run() {
        logger.info(String.format("To run: %s on hosts: %s with config timeout %d ms", command.name(), Arrays.toString(hosts), config.getWait()));
        for(String host: hosts) {
            Runnable runnable = () -> {
                Report report = null;
                try {
                    InetAddress remoteInetAddress = InetAddress.getByName(host);
                    PingCommand ping = new PingCommand(parameter, remoteInetAddress, config);
                    report = ping.ping();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                if (report == null) {
                    report(new Report(host, "Unable to ping"));
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
