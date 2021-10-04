package www.jasmine.task;

import www.jasmine.Command;
import www.jasmine.config.PingConfig;

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
        System.out.println("To run: " + command.name() + " with config timeout: " + config.getTimeout());
        System.out.println("On hosts: " + Arrays.toString(hosts));
    }

    @Override
    void report() {
        System.out.println("To report to: " + config.getReportURL());
    }
}
