package www.jasmine.model.network;

import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.util.MacAddress;

import java.net.InetAddress;

public class NetworkParameter {
    // A network interface of this *host* that can communicate with the Internet through default gateway
    private PcapNetworkInterface nif;
    // IP of this *host*
    private InetAddress localIP;
    // The MAC of the *nif*
    private MacAddress localMac;
    // The MAC of the default gateway
    private MacAddress defaultGatewayMac;

    public NetworkParameter(PcapNetworkInterface nif, InetAddress localIP, MacAddress localMac) {
        this.nif = nif;
        this.localIP = localIP;
        this.localMac = localMac;
    }

    public PcapNetworkInterface getNif() {
        return nif;
    }

    public InetAddress getLocalIP() {
        return localIP;
    }

    public MacAddress getLocalMac() {
        return localMac;
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
                ", sourceIP=" + (localIP == null ? "null" : localIP.toString()) +
                ", sourceMac=" + (localMac == null ? "null" : localMac.toString()) +
                ", defaultGatewayMac=" + (defaultGatewayMac == null ? "null" : defaultGatewayMac.toString()) +
                '}';
    }
}
