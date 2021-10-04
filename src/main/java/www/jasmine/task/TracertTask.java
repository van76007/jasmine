package www.jasmine.task;

import www.jasmine.Command;
import www.jasmine.config.TracertConfig;

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
        System.out.println("To run: " + command.name() + " with config timeout: " + config.getTimeout());
        System.out.println("On hosts: " + Arrays.toString(hosts));
    }

    @Override
    void report() {
        System.out.println("To report to: " + config.getReportURL());
    }
}
