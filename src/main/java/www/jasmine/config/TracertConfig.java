package www.jasmine.config;

public class TracertConfig {
    long pause;
    int maxTtl;
    int numberOfProbes;
    String reportURL;

    public TracertConfig(long pause, int maxTtl, int numberOfProbes, String reportURL) {
        this.pause = pause;
        this.maxTtl = maxTtl;
        this.numberOfProbes = numberOfProbes;
        this.reportURL = reportURL;
    }

    public long getPause() {
        return pause;
    }

    public int getMaxTtl() {
        return maxTtl;
    }

    public int getNumberOfProbes() {
        return numberOfProbes;
    }

    @Override
    public String toString() {
        return "TracertConfig{" +
                "pause=" + pause +
                ", maxTtl=" + maxTtl +
                ", numberOfProbes=" + numberOfProbes +
                ", reportURL='" + reportURL + '\'' +
                '}';
    }
}
