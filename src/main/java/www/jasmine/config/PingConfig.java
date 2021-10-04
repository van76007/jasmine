package www.jasmine.config;

public class PingConfig {
    int count;
    long timeout;
    String reportURL;

    public PingConfig(int count, int timeout, String reportURL) {
        this.count = count;
        this.timeout = timeout;
        this.reportURL = reportURL;
    }

    public int getCount() {
        return count;
    }

    public long getTimeout() {
        return timeout;
    }

    public String getReportURL() {
        return reportURL;
    }

    @Override
    public String toString() {
        return "PingConfig{" +
                "count=" + count +
                ", timeout=" + timeout +
                ", reportURL='" + reportURL + '\'' +
                '}';
    }
}
