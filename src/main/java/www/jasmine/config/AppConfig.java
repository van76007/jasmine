package www.jasmine.config;

import java.util.Arrays;
import java.util.stream.Collectors;

public class AppConfig {
    public AppConfig(PingConfig pingConfig, TracertConfig tracertConfig, HostConfig[] hosts, String command) {
        this.pingConfig = pingConfig;
        this.tracertConfig = tracertConfig;
        this.hosts = hosts;
        this.command = command;
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

    public String getCommand() { return command; }

    PingConfig pingConfig;
    TracertConfig tracertConfig;
    HostConfig[] hosts;
    String command;

    @Override
    public String toString() {
        return "AppConfig{" +
                "pingConfig=" + pingConfig +
                ", tracertConfig=" + tracertConfig +
                ", hosts=" + Arrays.stream(hosts).map(HostConfig::toString).collect(Collectors.joining(", ")) +
                ", command='" + command + '\'' +
                '}';
    }
}
