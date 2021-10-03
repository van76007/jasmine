package www.jasmine.config;

public class ReportConfig {
    String reportURL;

    public ReportConfig(String reportURL) {
        this.reportURL = reportURL;
    }

    public String getReportURL() {
        return reportURL;
    }

    @Override
    public String toString() {
        return "ReportConfig{" +
                "reportURL='" + reportURL + '\'' +
                '}';
    }
}
