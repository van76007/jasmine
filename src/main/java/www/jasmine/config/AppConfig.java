package www.jasmine.config;

import java.util.Arrays;

public class AppConfig {
    public AppConfig(PingConfig pingConfig, TracertConfig tracertConfig, String[] hosts, String command, long delay) {
        this.pingConfig = pingConfig;
        this.tracertConfig = tracertConfig;
        this.hosts = hosts;
        this.command = command;
        this.delay = delay;
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

    public String getCommand() { return command; }

    public long getDelay() {
        return delay;
    }

    PingConfig pingConfig;
    TracertConfig tracertConfig;
    String[] hosts;
    String command;
    long delay;

    @Override
    public String toString() {
        return "AppConfig{" +
                "pingConfig=" + pingConfig +
                ", tracertConfig=" + tracertConfig +
                ", hosts=" + Arrays.toString(hosts) +
                ", command='" + command + '\'' +
                ", delay=" + delay +
                '}';
    }
}
