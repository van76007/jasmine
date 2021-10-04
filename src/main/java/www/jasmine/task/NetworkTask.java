package www.jasmine.task;

import www.jasmine.Command;

public abstract class NetworkTask {
    Command command;
    String[] hosts;

    public NetworkTask(String[] hosts) {
        this.hosts = hosts;
    }

    public abstract void run();
    abstract void report();
}
