package www.jasmine.task;

import www.jasmine.Command;
import www.jasmine.config.PingConfig;
import www.jasmine.network.NetworkParameter;
import www.jasmine.network.PingCommand;
import www.jasmine.report.Report;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;

public class PingByICMP extends AbstractTask {
    PingConfig config;

    public PingByICMP(String host, PingConfig config, NetworkParameter parameter) {
        super(host, Command.PING_ICMP, parameter);
        this.config = config;
    }

    @Override
    public Report run() {
        logger.info(String.format("To run: %s on host: %s with config timeout %d ms", command.name(), host, config.getWait()));

        Report report = null;
        try {
            PingCommand ping = new PingCommand(parameter, host, config);
            report = ping.ping();
        } catch (UnknownHostException e) {
            logger.severe(e.getMessage());
        }
        if (report == null) {
            return new Report(host, "Unknown host to ping", Command.PING_ICMP);
        } else {
            return report;
        }
    }
}
