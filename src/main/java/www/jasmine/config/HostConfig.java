package www.jasmine.config;

public class HostConfig {
    String hostName;

    public HostConfig(String hostName) {
        this.hostName = hostName;
    }

    public String getHostName() {
        return hostName;
    }

    @Override
    public String toString() {
        return "HostConfig{" +
                "hostName='" + hostName + '\'' +
                '}';
    }
}
