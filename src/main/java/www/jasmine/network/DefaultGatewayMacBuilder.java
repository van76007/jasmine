package www.jasmine.network;

import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.Packet;
import org.pcap4j.util.MacAddress;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import www.jasmine.model.network.NetworkParameter;
import www.jasmine.model.network.ReceivedPacket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;

/**
 * To automatically detect the MAC of the default gateway.
 * The trick is to send an HTTP request and waiting for the reply. The Ethernet packet of the reply is sent from the
 * gateway. Thus, the MAC of the default gateway can be extracted from it
 */
public class DefaultGatewayMacBuilder extends AbstractNetworkCommand {
    public DefaultGatewayMacBuilder(NetworkParameter parameter) {
        super(parameter);
        this.bpfExpression = "tcp and dst host " + parameter.getLocalIP().getHostAddress();
    }

    public MacAddress buildDefaultGatewayMac() {
        MacAddress defaultGatewayMac = null;
        try {
            setupSendPacketHandler();
            ReceivedPacket receivedPacket = sendAndReceivePacket(null, (short)0);
            Packet packet = receivedPacket.getPacket();
            if (packet != null) {
                EthernetPacket p = packet.get(EthernetPacket.class);
                defaultGatewayMac = p.getHeader().getSrcAddr();
            }
        } catch(PcapNativeException | NotOpenException e) {
            logger.severe(e.getMessage());
        } finally {
            closeExecutor(executor);
            closeHandler(sendHandle);
        }
        return defaultGatewayMac;
    }

    @Override
    protected long sendPacket(Packet packet) {
        long start = System.nanoTime();
        try {
            sendHTTPRequest();
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
        return start;
    }

    @Override
    protected Packet buildPacket(int count, int ttl, short identifier, NetworkParameter parameter, InetAddress dstIpAddress) {
        throw new NotImplementedException();
    }

    private void sendHTTPRequest() throws IOException {
        new URL("http://anysite.com" ).openStream().close();
    }
}
