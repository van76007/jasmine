package www.jasmine.config;

public class PingConfig {
    int count;
    long wait;
    int timeout;
    String reportURL;

    public PingConfig(int count, int timeout, long wait, String reportURL) {
        this.count = count;
        this.timeout = timeout;
        this.wait = wait;
        this.reportURL = reportURL;
    }

    public int getCount() {
        return count;
    }

    public int getTimeout() { return timeout; }

    public long getWait() {
        return wait;
    }

    public String getReportURL() {
        return reportURL;
    }

    @Override
    public String toString() {
        return "PingConfig{" +
                "count=" + count +
                ", timeout=" + timeout +
                ", wait=" + wait +
                ", reportURL='" + reportURL + '\'' +
                '}';
    }
}
