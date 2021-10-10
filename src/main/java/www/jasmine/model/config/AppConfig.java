package www.jasmine.model.config;

import java.util.Arrays;

public class AppConfig {
    PingConfig pingConfig;
    TracertConfig tracertConfig;
    String[] hosts;
    long delay;
    long shutdownPeriod;
    String reportURL;
    String reportLogFilePath;

    public AppConfig(PingConfig pingConfig, TracertConfig tracertConfig, String[] hosts, long delay, long shutdownPeriod, String reportURL, String reportLogFilePath) {
        this.pingConfig = pingConfig;
        this.tracertConfig = tracertConfig;
        this.hosts = hosts;
        this.delay = delay;
        this.shutdownPeriod = shutdownPeriod;
        this.reportURL = reportURL;
        this.reportLogFilePath = reportLogFilePath;
    }

    public PingConfig getPingConfig() { return pingConfig; }

    public TracertConfig getTracertConfig() { return tracertConfig; }

    public String[] getHosts() {
        return hosts;
    }

    public long getDelay() {
        return delay;
    }

    public long getShutdownPeriod() {
        return shutdownPeriod;
    }

    public String getReportURL() { return reportURL; }

    public String getReportLogFilePath() { return reportLogFilePath; }

    @Override
    public String toString() {
        return "AppConfig{" +
                "pingConfig=" + pingConfig +
                ", tracertConfig=" + tracertConfig +
                ", hosts=" + Arrays.toString(hosts) +
                ", delay=" + delay +
                ", shutdownPeriod=" + shutdownPeriod +
                ", reportURL='" + reportURL + '\'' +
                ", logFile='" + reportLogFilePath + '\'' +
                '}';
    }
}
