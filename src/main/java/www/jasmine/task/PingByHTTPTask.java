package www.jasmine.task;

import www.jasmine.Command;
import www.jasmine.config.PingConfig;

public class PingByHTTPTask extends NetworkTask {
    Command command = Command.PING_HTTP;
    PingConfig config;

    public PingByHTTPTask(PingConfig config) {
        this.config = config;
    }

    @Override
    public void run() {
        System.out.println("To run: " + command.name() + "with config: " + config.getDelay());
    }

    @Override
    void report() {
        System.out.println("To report to: " + config.getReportURL());
    }
}
