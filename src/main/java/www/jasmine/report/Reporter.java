package www.jasmine.report;

import www.jasmine.Command;
import www.jasmine.SingletonLogger;
import www.jasmine.config.AppConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Reporter {
    final static String NOT_AVAILABLE_REPORT = "N/A";
    final static String LOG_FILE_NAME = "jasmine_report.log";
    String url;
    String pathToLogFile;
    Uploader uploader = new Uploader();
    Logger logger = SingletonLogger.SingletonLogger().logger;

    public Reporter(AppConfig config) {
        this.url = config.getReportURL();
        String[] pathNames = { config.getReportLogFilePath(), LOG_FILE_NAME };
        this.pathToLogFile = String.join(File.separator, pathNames);
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

        uploadReport(jsonData);
        logReport(jsonData);
    }

    private void uploadReport(String jsonData) {
        try {
            String response = uploader.uploadReport(url, jsonData);
            logger.info(response);
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
    }

    private void logReport(String jsonData) {
        Path path = Paths.get(pathToLogFile);
        try {
            Files.createFile(path);
        } catch (IOException e) {
            logger.warning(String.format("report log file %s exists", pathToLogFile));
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        try {
            Files.write(path, dateTimeFormatter.format(now).getBytes(), StandardOpenOption.APPEND);
            Files.write(path, jsonData.getBytes(), StandardOpenOption.APPEND);
            Files.write(path, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
    }
}
