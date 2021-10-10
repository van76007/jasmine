package www.jasmine.model.config;

public class TracertConfig {
    long pause;
    int maxTtl;
    int numberOfProbes;

    public TracertConfig(long pause, int maxTtl, int numberOfProbes) {
        this.pause = pause;
        this.maxTtl = maxTtl;
        this.numberOfProbes = numberOfProbes;
    }

    public long getPause() { return pause; }

    public int getMaxTtl() { return maxTtl; }

    public int getNumberOfProbes() { return numberOfProbes; }

    @Override
    public String toString() {
        return "TracertConfig{" +
                "pause=" + pause +
                ", maxTtl=" + maxTtl +
                ", numberOfProbes=" + numberOfProbes +
                '}';
    }
}
