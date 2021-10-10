package www.jasmine.task;

import www.jasmine.Command;
import www.jasmine.config.PingConfig;
import www.jasmine.network.NetworkParameter;
import www.jasmine.report.Report;

import java.io.IOException;
import java.net.InetAddress;

public class PingByHTTP extends AbstractTask {
    PingConfig config;

    public PingByHTTP(String host, PingConfig config, NetworkParameter parameter) {
        super(host, Command.PING_HTTP, parameter);
        this.config = config;
    }

    @Override
    public Report run() {
        logger.info(String.format("To run: %s on host: %s with config timeout %d ms", command.name(), host, config.getTimeout()));

        boolean isReachable;
        long tStart = System.nanoTime();
        try {
            isReachable = isHostReachable(host);
        } catch(IOException e) {
            logger.severe(e.getMessage());
            isReachable = false;
        }
        long delay = (System.nanoTime() - tStart) / 1000000;
        return new Report(host, String.format("%s is reachable %s in %d ms", host, isReachable, delay), Command.PING_HTTP);
    }

    private boolean isHostReachable(String host) throws IOException {
        InetAddress address = InetAddress.getByName(host);
        return address.isReachable(config.getTimeout());
    }
}
