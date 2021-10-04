package www.jasmine.report;

public class Report {
    String host;
    String message;

    public Report(String host, String message) {
        this.host = host;
        this.message = message;
    }

    public String getHost() {
        return host;
    }

    public String getMessage() {
        return message;
    }
}
