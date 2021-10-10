package www.jasmine.task;

import www.jasmine.Command;
import www.jasmine.SingletonLogger;
import www.jasmine.model.network.NetworkParameter;
import www.jasmine.model.report.Report;

import java.util.logging.Logger;

public abstract class AbstractTask {
    String host;
    Command command;
    NetworkParameter parameter;
    Logger logger = SingletonLogger.SingletonLogger().logger;

    public AbstractTask(String host, Command command, NetworkParameter parameter) {
        this.host = host;
        this.command = command;
        this.parameter = parameter;
    }

    public abstract Report run();
}
