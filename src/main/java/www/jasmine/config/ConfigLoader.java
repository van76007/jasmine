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
            PingConfig pingConfig = new PingConfig(Integer.parseInt(prop.getProperty("ping.count")), Integer.parseInt(prop.getProperty("ping.timeout")), Long.parseLong(prop.getProperty("ping.wait")), reportUrl);
            TracertConfig tracertConfig = new TracertConfig(Integer.parseInt(prop.getProperty("tracert.timeout")), reportUrl);
            String[] hosts = new String[] { prop.getProperty("host.site1"), prop.getProperty("host.site2") };
            return new AppConfig(pingConfig, tracertConfig, hosts, command, delay, shutdownPeriod);
        }
    }
}
