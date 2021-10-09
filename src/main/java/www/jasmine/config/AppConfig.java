package www.jasmine.config;

import java.util.Arrays;

public class AppConfig {
    PingConfig pingConfig;
    TracertConfig tracertConfig;
    String[] hosts;
    long delay;
    long shutdownPeriod;

    public AppConfig(PingConfig pingConfig, TracertConfig tracertConfig, String[] hosts, long delay, long shutdownPeriod) {
        this.pingConfig = pingConfig;
        this.tracertConfig = tracertConfig;
        this.hosts = hosts;
        this.delay = delay;
        this.shutdownPeriod = shutdownPeriod;
    }

    public PingConfig getPingConfig() {
        return pingConfig;
    }

    public TracertConfig getTracertConfig() {
        return tracertConfig;
    }

    public String[] getHosts() {
        return hosts;
    }

    public long getDelay() {
        return delay;
    }

    public long getShutdownPeriod() {
        return shutdownPeriod;
    }

    @Override
    public String toString() {
        return "AppConfig{" +
                "pingConfig=" + pingConfig +
                ", tracertConfig=" + tracertConfig +
                ", hosts=" + Arrays.toString(hosts) +
                ", delay=" + delay +
                ", shutdownPeriod=" + shutdownPeriod +
                '}';
    }
}
