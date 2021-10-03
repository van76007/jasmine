package www.jasmine.config;

import java.util.Arrays;
import java.util.stream.Collectors;

public class AppConfig {
    public AppConfig(PingConfig pingConfig, TracertConfig tracertConfig, ReportConfig reportConfig, HostConfig[] hosts) {
        this.pingConfig = pingConfig;
        this.tracertConfig = tracertConfig;
        this.reportConfig = reportConfig;
        this.hosts = hosts;
    }

    public PingConfig getPingConfig() {
        return pingConfig;
    }

    public TracertConfig getTracertConfig() {
        return tracertConfig;
    }

    public ReportConfig getReportConfig() {
        return reportConfig;
    }

    public HostConfig[] getHosts() {
        return hosts;
    }

    PingConfig pingConfig;
    TracertConfig tracertConfig;
    ReportConfig reportConfig;
    HostConfig[] hosts;

    @Override
    public String toString() {
        return "AppConfig{" +
                "pingConfig=" + pingConfig +
                ", tracertConfig=" + tracertConfig +
                ", reportConfig=" + reportConfig +
                ", hosts=" + Arrays.stream(hosts).map(HostConfig::toString).collect(Collectors.joining(", ")) +
                '}';
    }
}
