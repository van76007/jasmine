package www.jasmine.config;

public class PingConfig {
    int count;
    long delay;
    String reportURL;

    public PingConfig(int count, int delay, String reportURL) {
        this.count = count;
        this.delay = delay;
        this.reportURL = reportURL;
    }

    public int getCount() {
        return count;
    }

    public long getDelay() {
        return delay;
    }

    public String getReportURL() {
        return reportURL;
    }

    @Override
    public String toString() {
        return "PingConfig{" +
                "count=" + count +
                ", delay=" + delay +
                ", reportURL='" + reportURL + '\'' +
                '}';
    }
}
