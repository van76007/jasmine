package www.jasmine.network;

import org.pcap4j.core.PcapAddress;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;
import org.pcap4j.util.MacAddress;
import www.jasmine.SingletonLogger;
import www.jasmine.model.network.NetworkParameter;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.logging.Logger;

public class NetworkParameterBuilder {
    Logger logger = SingletonLogger.SingletonLogger().logger;

    public NetworkParameter buildNetworkParameter() {
        NetworkParameter parameter = null;
        try {
            // 1. Find local network interface can communicate to the Internet via default gateway.
            //    Also find the IP of this host that the default gateway send back the IP packet
            //    Input is *one of the IP of this host*. For example if using VPN, InetAddress.getLocalHost().getHostAddress()
            //    will return the IP "192.168.1.34" which is looked up by the host name
            //    We want to find the other useful IP "192.168.1.33"
            parameter = getLocalNetworkInterfaceParameter(InetAddress.getByName(InetAddress.getLocalHost().getHostAddress()));
            if (parameter != null) {
                // 2. Find default gateway MAC
                DefaultGatewayMacBuilder builder = new DefaultGatewayMacBuilder(parameter);
                MacAddress defaultGatewayMac = builder.buildDefaultGatewayMac();
                parameter.setDefaultGatewayMac(defaultGatewayMac);
            }
        } catch (UnknownHostException e) {
            logger.severe(e.getMessage());
        }
        return parameter;
    }

    private NetworkParameter getLocalNetworkInterfaceParameter(InetAddress hostIP) {
        logger.info("this machine IP: " + hostIP.toString());
        byte[] inputIpInBytes = hostIP.getAddress();
        try {
            for (PcapNetworkInterface currentInterface : Pcaps.findAllDevs()) {
                List<PcapAddress> addresses = currentInterface.getAddresses();
                for (PcapAddress ipAddress : addresses) {
                    if (!isSameTypeAddress(hostIP, ipAddress.getAddress())) {
                        continue;
                    }
                    InetAddress maskAddr = ipAddress.getNetmask();
                    if (maskAddr != null && !isUnderSameSubNet(hostIP, ipAddress.getAddress(), maskAddr)) {
                        continue;
                    }
                    byte[] ipInBytes = ipAddress.getAddress().getAddress();
                    if(similarBytes(inputIpInBytes, ipInBytes) > Integer.MIN_VALUE) {
                        InetAddress localIP = ipAddress.getAddress();
                        MacAddress localMac = MacAddress.getByAddress(currentInterface.getLinkLayerAddresses().get(0).getAddress());
                        return new NetworkParameter(currentInterface, localIP, localMac);
                    }
                }
            }
        } catch (PcapNativeException | IndexOutOfBoundsException e) {
            logger.severe(e.getMessage());
        }
        return null;
    }

    private boolean isSameTypeAddress(InetAddress address1, InetAddress address2) {
        return (address1 instanceof Inet6Address && address2 instanceof Inet6Address)
                || (address1 instanceof Inet4Address && address2 instanceof Inet4Address)
                || (address1.toString().equals(address2.toString()));
    }

    private boolean isUnderSameSubNet(InetAddress testAddr, InetAddress currentAddr, InetAddress maskAddr) {
        byte [] test = testAddr.getAddress();
        byte [] current = currentAddr.getAddress();
        byte [] mask = maskAddr.getAddress();
        boolean equal = true;
        for (int i =0; i < test.length; i++) {
            if ((test[i] & mask[i]) != (current[i] & mask[i])) {
                equal = false;
                break;
            }
        }
        return equal;
    }

    private int similarBytes(byte[] b1, byte[] b2) {
        int n = b1.length;
        int i = 0;
        while(i < n && b1[i] == b2[i]) {
            i++;
        }
        return i;
    }
}
