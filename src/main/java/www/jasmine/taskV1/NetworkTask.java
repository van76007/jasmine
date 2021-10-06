package www.jasmine.taskV1;

import www.jasmine.Command;
import www.jasmine.SingletonLogger;
import www.jasmine.report.Report;

import java.util.logging.Logger;

public abstract class NetworkTask {
    Command command;
    String[] hosts;
    Logger logger = SingletonLogger.SingletonLogger().logger;

    public NetworkTask(String[] hosts) {
        this.hosts = hosts;
    }

    public abstract void run();
    abstract void report(Report report);
}
