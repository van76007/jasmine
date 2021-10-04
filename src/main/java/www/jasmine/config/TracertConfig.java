package www.jasmine.config;

public class TracertConfig {
    long delay;
    String reportURL;

    public TracertConfig(int delay, String reportURL) {
        this.delay = delay;
        this.reportURL = reportURL;
    }

    public long getDelay() {
        return delay;
    }

    public String getReportURL() {
        return reportURL;
    }

    @Override
    public String toString() {
        return "TracertConfig{" +
                "delay=" + delay +
                ", reportURL='" + reportURL + '\'' +
                '}';
    }
}
