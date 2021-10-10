package www.jasmine.model.config;

public class PingConfig {
    int count;
    long wait;
    int timeout;

    public PingConfig(int count, int timeout, long wait) {
        this.count = count;
        this.timeout = timeout;
        this.wait = wait;
    }

    public int getCount() { return count; }

    public int getTimeout() { return timeout; }

    public long getWait() { return wait; }

    @Override
    public String toString() {
        return "PingConfig{" +
                "count=" + count +
                ", wait=" + wait +
                ", timeout=" + timeout +
                '}';
    }
}
