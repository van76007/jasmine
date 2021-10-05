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
            Long delay = Long.parseLong(prop.getProperty("delay"));
            Long shutdownPeriod = Long.parseLong(prop.getProperty("shutdown.period"));

            String command = prop.getProperty("command");
            String reportUrl = prop.getProperty("report.url");
            int pingCount = Integer.parseInt(prop.getProperty("ping.count"));
            int pingTimeout = Integer.parseInt(prop.getProperty("ping.timeout"));
            long pingWait = Long.parseLong(prop.getProperty("ping.wait"));
            long tracertPause = Long.parseLong(prop.getProperty("tracert.pause"));
            int tracertMaxTTL = Integer.parseInt(prop.getProperty("tracert.maxTtl"));
            int tracertNumberOfProbes = Integer.parseInt(prop.getProperty("tracert.numberOfProbes"));
            PingConfig pingConfig = new PingConfig(pingCount, pingTimeout, pingWait, reportUrl);

            TracertConfig tracertConfig = new TracertConfig(tracertPause, tracertMaxTTL, tracertNumberOfProbes, reportUrl);
            String[] hosts = new String[] { prop.getProperty("host.site1"), prop.getProperty("host.site2") };
            return new AppConfig(pingConfig, tracertConfig, hosts, command, delay, shutdownPeriod);
        }
    }
}
