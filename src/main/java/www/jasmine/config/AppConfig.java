package www.jasmine.config;

import java.util.Arrays;
import java.util.stream.Collectors;

public class AppConfig {
    public AppConfig(PingConfig pingConfig, TracertConfig tracertConfig, HostConfig[] hosts) {
        this.pingConfig = pingConfig;
        this.tracertConfig = tracertConfig;
        this.hosts = hosts;
    }

    public PingConfig getPingConfig() {
        return pingConfig;
    }

    public TracertConfig getTracertConfig() {
        return tracertConfig;
    }

    public HostConfig[] getHosts() {
        return hosts;
    }

    PingConfig pingConfig;
    TracertConfig tracertConfig;
    HostConfig[] hosts;

    @Override
    public String toString() {
        return "AppConfig{" +
                "pingConfig=" + pingConfig.toString() +
                ", tracertConfig=" + tracertConfig.toString() +
                ", hosts=" + Arrays.stream(hosts).map(HostConfig::toString).collect(Collectors.joining(", ")) +
                '}';
    }
}
