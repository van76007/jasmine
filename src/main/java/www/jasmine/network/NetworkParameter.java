package www.jasmine.network;

import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.util.MacAddress;

import java.net.InetAddress;

public class NetworkParameter {
    // A network interface of this *host* that can communicate with the Internet through default gateway
    private PcapNetworkInterface nif;
    // IP of this *host*
    private InetAddress sourceIP;
    // The MAC of the *nif*
    private MacAddress sourceMac;
    // The MAC of the default gateway
    private MacAddress defaultGatewayMac;

    public NetworkParameter(PcapNetworkInterface nif, InetAddress sourceIP, MacAddress sourceMac) {
        this.nif = nif;
        this.sourceIP = sourceIP;
        this.sourceMac = sourceMac;
    }

    public PcapNetworkInterface getNif() {
        return nif;
    }

    public InetAddress getSourceIP() {
        return sourceIP;
    }

    public MacAddress getSourceMac() {
        return sourceMac;
    }

    public MacAddress getDefaultGatewayMac() {
        return defaultGatewayMac;
    }

    public void setDefaultGatewayMac(MacAddress defaultGatewayMac) {
        this.defaultGatewayMac = defaultGatewayMac;
    }

    @Override
    public String toString() {
        return "NetworkParameter{" +
                "nif=" + (nif == null ? "null" : nif.toString()) +
                ", sourceIP=" + (sourceIP == null ? "null" : sourceIP.toString()) +
                ", sourceMac=" + (sourceMac == null ? "null" : sourceMac.toString()) +
                ", defaultGatewayMac=" + (defaultGatewayMac == null ? "null" : defaultGatewayMac.toString()) +
                '}';
    }
}
