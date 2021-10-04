package www.jasmine.task;

import www.jasmine.Command;
import www.jasmine.config.PingConfig;
import www.jasmine.report.Report;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;

public class PingByHTTPTask extends NetworkTask {
    PingConfig config;

    public PingByHTTPTask(String[] hosts, PingConfig config) {
        super(hosts);
        this.config = config;
        this.command = Command.PING_HTTP;
    }

    @Override
    public void run() {
        logger.info(String.format("To run: %s on hosts: %s with config timeout %d ms", command.name(), Arrays.toString(hosts), config.getTimeout()));
        for(String host: hosts) {
            Runnable runnable = () -> {
                boolean isReachable;
                long tStart = System.nanoTime();
                try {
                    isReachable = isHostReachable(host);
                } catch(IOException e) {
                    isReachable = false;
                }
                long delay = (System.nanoTime() - tStart) / 1000000;
                report(new Report(host, String.format("is reachable %s in %d ms", isReachable, delay)));
            };

            Thread thread = new Thread(runnable);
            thread.start();
        }
    }

    @Override
    void report(Report report) {
        logger.info(String.format("To report to: %s about host: %s data: %s", config.getReportURL(), report.getHost(), report.getMessage()));
    }

    private boolean isHostReachable(String host) throws IOException {
        InetAddress address = InetAddress.getByName(host);
        return address.isReachable((int) config.getTimeout());
    }
}
