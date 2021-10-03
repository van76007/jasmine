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
            PingConfig pingConfig = new PingConfig(Integer.parseInt(prop.getProperty("ping.count")), Integer.parseInt(prop.getProperty("ping.delayInMilliseconds")));
            TracertConfig tracertConfig = new TracertConfig(Integer.parseInt(prop.getProperty("tracert.delay")));
            ReportConfig reportConfig = new ReportConfig(prop.getProperty("report.url"));
            HostConfig[] hosts = new HostConfig[] {
              new HostConfig(prop.getProperty("host.site1")), new HostConfig(prop.getProperty("host.site2"))
            };
            return new AppConfig(pingConfig, tracertConfig, reportConfig, hosts);
        }
    }
}
