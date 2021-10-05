package www.jasmine.network;

import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.Packet;
import org.pcap4j.util.MacAddress;

import java.io.IOException;
import java.net.URL;

public class DefaultGatewayMacBuilder extends AbstractNetworkTask {

    public DefaultGatewayMacBuilder(NetworkParameter parameter) {
        super(parameter);
        this.bpfExpression = "tcp and dst host " + parameter.getLocalIP().getHostAddress();
    }

    public MacAddress buildDefaultGatewayMac() {
        MacAddress defaultGatewayMac = null;
        try {
            setupPacketHandlers();
            ReceivedPacket receivedPacket = sendAndReceivePacket(1);
            Packet packet = receivedPacket.getPacket();
            if (packet != null) {
                EthernetPacket p = packet.get(EthernetPacket.class);
                defaultGatewayMac = p.getHeader().getSrcAddr();
            }
        } catch(PcapNativeException | NotOpenException e) {
            e.printStackTrace();
        } finally {
            closeExecutor(executor);
            closeHandler(receiveHandle);
        }
        return defaultGatewayMac;
    }

    @Override
    protected long sendPacket(int count) {
        long start = System.nanoTime();
        try {
            sendHTTPRequest();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return start;
    }

    private void sendHTTPRequest() throws IOException {
        new URL("http://anysite.com" ).openStream().close();
    }
}
