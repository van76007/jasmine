package www.jasmine.task;

import www.jasmine.Command;
import www.jasmine.config.PingConfig;
import www.jasmine.config.TracertConfig;

public class TracertTask extends NetworkTask {
    Command command = Command.TRACERT;
    TracertConfig config;

    public TracertTask(TracertConfig config) {
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
