package www.jasmine.report;

import www.jasmine.config.AppConfig;

import java.util.List;
import java.util.stream.Collectors;

public class Reporter {
    String url;

    public Reporter(AppConfig config) {
        this.url = config.getReportURL();
    }

    public void forwardReports(List<Report> reports) {
        List<String> allReports = reports.stream().map(Report::toString).collect(Collectors.toList());
        String finalReport = String.join("\n", allReports);
        System.out.println("To forward report about a host: " + finalReport);
        System.out.println("DONE");
    }
}
