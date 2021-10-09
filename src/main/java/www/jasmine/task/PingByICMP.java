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
    public void run() {
        logger.info(String.format("To run: %s on host: %s with config timeout %d ms", command.name(), host, config.getWait()));

        Report report = null;
        try {
            InetAddress remoteInetAddress = InetAddress.getByName(host);
            PingCommand ping = new PingCommand(parameter, remoteInetAddress, config);
            report = ping.ping();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        if (report == null) {
            report(new Report(host, "Unknown host to ping"));
        } else {
            report(report);
        }
    }

    @Override
    void report(Report report) {
        logger.info(String.format("To report result of %s about host: %s data:\n%s", command, report.getHost(), report.getMessage()));
    }
}
