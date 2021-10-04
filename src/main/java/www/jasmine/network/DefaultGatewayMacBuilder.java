package www.jasmine.network;

import org.pcap4j.core.*;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.Packet;
import org.pcap4j.util.MacAddress;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static www.jasmine.network.NetworkConstants.*;

// ToDo: Abstract the flow send/receive/post-processing packet in the code
public class DefaultGatewayMacBuilder {
    public MacAddress buildDefaultGatewayMac(NetworkParameter parameter) {
        MacAddress defaultGatewayMac = null;
        PcapHandle receiveHandle = null;
        ExecutorService executor = null;
        try {
            PcapNetworkInterface nif = parameter.getNif();
            receiveHandle = nif.openLive(SNAPLEN, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, READ_TIMEOUT);
            receiveHandle.setFilter("tcp and dst host " + parameter.getSourceIP().getHostAddress(), BpfProgram.BpfCompileMode.OPTIMIZE);

            final AtomicReference<Packet> pRef = new AtomicReference<>();
            PacketListener listener = packet -> {
                if (packet.contains(EthernetPacket.class)) {
                    pRef.set(packet);
                }
            };

            Task receiveTask = new Task(receiveHandle, listener, 1);
            executor = Executors.newSingleThreadExecutor();
            Future receiveFuture = executor.submit(receiveTask);

            // Send an HTTP request to receive some reply packet in the listener
            new URL("http://anysite.com" ).openStream().close();
            try {
                receiveFuture.get(WAIT_FOR_RESPONSE_TIMEOUT, TimeUnit.MILLISECONDS);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            Packet packet = pRef.get();
            if (packet != null) {
                EthernetPacket p = packet.get(EthernetPacket.class);
                defaultGatewayMac = p.getHeader().getSrcAddr();
            }
        } catch(PcapNativeException | NotOpenException | IOException e) {
            e.printStackTrace();
        } finally {
            closeExecutor(executor);
            closeHandler(receiveHandle);
        }
        return defaultGatewayMac;
    }

    private void closeExecutor(ExecutorService executor) {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }

    private void closeHandler(PcapHandle handler) {
        if (handler != null) {
            try {
                handler.breakLoop();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            try {
                handler.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
