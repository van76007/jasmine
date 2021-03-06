package www.jasmine.config;

import www.jasmine.model.config.AppConfig;
import www.jasmine.model.config.PingConfig;
import www.jasmine.model.config.TracertConfig;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Load configuration from the config file
 */
public class ConfigLoader {
    public AppConfig buildAppConfig(String pathToConfigFile) throws IOException, NumberFormatException {
        try (InputStream input = new FileInputStream(pathToConfigFile)) {
            Properties prop = new Properties();
            prop.load(input);

            long delay = Long.parseLong(prop.getProperty("delay"));
            long shutdownPeriod = Long.parseLong(prop.getProperty("shutdown.period"));
            String reportUrl = prop.getProperty("report.url");
            String reportLogFilePath = prop.getProperty("report.path");
            int pingCount = Integer.parseInt(prop.getProperty("ping.count"));
            int pingTimeout = Integer.parseInt(prop.getProperty("ping.timeout"));
            long pingWait = Long.parseLong(prop.getProperty("ping.wait"));
            long traceRoutePause = Long.parseLong(prop.getProperty("traceRoute.pause"));
            int traceRouteMaxTTL = Integer.parseInt(prop.getProperty("traceRoute.maxTtl"));
            int traceRouteNumberOfProbes = Integer.parseInt(prop.getProperty("traceRoute.numberOfProbes"));

            PingConfig pingConfig = new PingConfig(pingCount, pingTimeout, pingWait);
            TracertConfig tracertConfig = new TracertConfig(traceRoutePause, traceRouteMaxTTL, traceRouteNumberOfProbes);
            String[] hosts = new String[] { prop.getProperty("host.site1"), prop.getProperty("host.site2") };

            return new AppConfig(pingConfig, tracertConfig, hosts, delay, shutdownPeriod, reportUrl, reportLogFilePath);
        }
    }
}
