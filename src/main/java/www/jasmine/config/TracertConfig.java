package www.jasmine.config;

public class TracertConfig {
    long timeout;
    String reportURL;

    public TracertConfig(int timeout, String reportURL) {
        this.timeout = timeout;
        this.reportURL = reportURL;
    }

    public long getTimeout() {
        return timeout;
    }

    public String getReportURL() {
        return reportURL;
    }

    @Override
    public String toString() {
        return "TracertConfig{" +
                "timeout=" + timeout +
                ", reportURL='" + reportURL + '\'' +
                '}';
    }
}
