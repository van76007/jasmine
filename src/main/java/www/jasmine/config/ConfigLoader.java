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
            String command = prop.getProperty("command");
            String reportUrl = prop.getProperty("report.url");
            PingConfig pingConfig = new PingConfig(Integer.parseInt(prop.getProperty("ping.count")), Integer.parseInt(prop.getProperty("ping.delay")), reportUrl);
            TracertConfig tracertConfig = new TracertConfig(Integer.parseInt(prop.getProperty("tracert.delay")), reportUrl);
            HostConfig[] hosts = new HostConfig[] {
              new HostConfig(prop.getProperty("host.site1")), new HostConfig(prop.getProperty("host.site2"))
            };
            return new AppConfig(pingConfig, tracertConfig, hosts, command);
        }
    }
}
