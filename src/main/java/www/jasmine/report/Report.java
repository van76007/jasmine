package www.jasmine.report;

public class Report {
    private String host;
    private final String message;

    public Report(String host, String message) {
        this.host = host;
        this.message = message;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public String toString() {
        return "Report{" +
                "host='" + host + '\'' +
                ", message=\n'" + message + '\'' +
                '}';
    }
}
