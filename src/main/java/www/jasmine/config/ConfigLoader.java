package www.jasmine.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    public AppConfig buildAppConfig(String pathToConfigFile) throws IOException, NumberFormatException {
        try (InputStream input = new FileInputStream(pathToConfigFile)) {
            Properties prop = new Properties();
            prop.load(input);

            long delay = Long.parseLong(prop.getProperty("delay"));
            long shutdownPeriod = Long.parseLong(prop.getProperty("shutdown.period"));
            String command = prop.getProperty("command");
            String reportUrl = prop.getProperty("report.url");
            int pingCount = Integer.parseInt(prop.getProperty("ping.count"));
            int pingTimeout = Integer.parseInt(prop.getProperty("ping.timeout"));
            long pingWait = Long.parseLong(prop.getProperty("ping.wait"));
            long traceRoutePause = Long.parseLong(prop.getProperty("traceRoute.pause"));
            int traceRouteMaxTTL = Integer.parseInt(prop.getProperty("traceRoute.maxTtl"));
            int traceRouteNumberOfProbes = Integer.parseInt(prop.getProperty("traceRoute.numberOfProbes"));

            PingConfig pingConfig = new PingConfig(pingCount, pingTimeout, pingWait, reportUrl);
            TracertConfig tracertConfig = new TracertConfig(traceRoutePause, traceRouteMaxTTL, traceRouteNumberOfProbes, reportUrl);
            String[] hosts = new String[] { prop.getProperty("host.site1"), prop.getProperty("host.site2") };

            return new AppConfig(pingConfig, tracertConfig, hosts, command, delay, shutdownPeriod);
        }
    }
}
