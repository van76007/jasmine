package www.jasmine.report;

import www.jasmine.Command;
import www.jasmine.SingletonLogger;
import www.jasmine.config.AppConfig;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Reporter {
    final static String NOT_AVAILABLE_REPORT = "N/A";
    String url;
    Uploader uploader = new Uploader();
    Logger logger = SingletonLogger.SingletonLogger().logger;

    public Reporter(AppConfig config) {
        this.url = config.getReportURL();
    }

    public void forwardReports(String host, List<Report> reports) {
        List<String> allReports = reports.stream().map(Report::toString).collect(Collectors.toList());
        String finalReport = String.join("\n", allReports);
        logger.info("To forward report about a host: " + finalReport);

        Optional<Report> reportOfPingICMP= reports.stream().filter(r -> r.getCommand() == Command.PING_ICMP).findFirst();
        String icmpPingOutput = reportOfPingICMP.isPresent() ? reportOfPingICMP.get().getMessage() : NOT_AVAILABLE_REPORT;

        Optional<Report> reportOfPingHTTP= reports.stream().filter(r -> r.getCommand() == Command.PING_HTTP).findFirst();
        String tcpPingOutput = reportOfPingHTTP.isPresent() ? reportOfPingHTTP.get().getMessage() : NOT_AVAILABLE_REPORT;

        Optional<Report> reportOfTraceRoute= reports.stream().filter(r -> r.getCommand() == Command.TRACEROUTE).findFirst();
        String traceOutput = reportOfTraceRoute.isPresent() ? reportOfTraceRoute.get().getMessage() : NOT_AVAILABLE_REPORT;

        String jsonData = JsonUtil.convertStringToJson(host, icmpPingOutput, tcpPingOutput, traceOutput);
        try {
            String response = uploader.uploadReport(url, jsonData);
            logger.info(response);
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
    }
}
