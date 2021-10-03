package www.jasmine.config;

public class PingConfig {
    int count;
    int delay;

    public PingConfig(int count, int delay) {
        this.count = count;
        this.delay = delay;
    }

    public int getCount() {
        return count;
    }

    public int getDelay() {
        return delay;
    }

    @Override
    public String toString() {
        return "PingConfig{" +
                "count=" + count +
                ", delay=" + delay +
                '}';
    }
}
