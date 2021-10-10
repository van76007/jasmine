package www.jasmine.report;

public class JsonUtil {
    public static String convertStringToJson(String host, String icmpPingOutput, String tcpPingOutput, String traceOutput) {
        return String.format("{\"host\":\"%s\",\"icmp_ping\":\"%s\",\"tcp_ping\":\"%s\",\"trace\":\"%s\"}",
                host, icmpPingOutput, tcpPingOutput, traceOutput);
    }
}
