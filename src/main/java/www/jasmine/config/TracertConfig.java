package www.jasmine.config;

public class TracertConfig {
    int delay;

    public TracertConfig(int delay) {
        this.delay = delay;
    }

    public int getDelay() {
        return delay;
    }

    @Override
    public String toString() {
        return "TracertConfig{" +
                "delay=" + delay +
                '}';
    }
}
