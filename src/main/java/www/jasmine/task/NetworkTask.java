package www.jasmine.task;

import www.jasmine.Command;

public abstract class NetworkTask {
    Command command;
    public abstract void run();
    abstract void report();
}
