package www.jasmine.network;

import org.pcap4j.core.*;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.Packet;

import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static www.jasmine.network.NetworkConstants.*;

public abstract class AbstractNetworkTask {
    NetworkParameter parameter;
    PcapHandle receiveHandle = null;
    PcapHandle sendHandle = null;
    String bpfExpression;
    PacketListener listener;
    ExecutorService executor = Executors.newSingleThreadExecutor();
    final AtomicReference<Packet> pRef = new AtomicReference<>();

    public AbstractNetworkTask(NetworkParameter parameter) {
        this.parameter = parameter;
    }

    protected ReceivedPacket sendAndReceivePacket(Packet packet) {
        Task receiveTask = new Task(receiveHandle, listener, 1);
        Future receiveFuture = executor.submit(receiveTask);

        // Packet packet = buildPacket(count, ttl, parameter, InetAddress dstIpAddress);
        long start = sendPacket(packet);
        try {
            receiveFuture.get(WAIT_FOR_RESPONSE_TIMEOUT, TimeUnit.MILLISECONDS);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        long delay = (System.nanoTime() - start) / 1000000;
        return new ReceivedPacket(pRef.get(), delay);
    }

    protected abstract long sendPacket(Packet packet);

    protected abstract Packet buildPacket(int count, int ttl, NetworkParameter parameter, InetAddress dstIpAddress);

    protected void closeExecutor(ExecutorService executor) {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }

    protected void closeHandler(PcapHandle handler) {
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

    protected void setupPacketHandlers() throws PcapNativeException, NotOpenException {
        sendHandle = parameter.getNif().openLive(SNAPLEN, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, READ_TIMEOUT);
        receiveHandle = parameter.getNif().openLive(SNAPLEN, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, READ_TIMEOUT);
        receiveHandle.setFilter(bpfExpression, BpfProgram.BpfCompileMode.OPTIMIZE);
        listener = packet -> {
            if (packet.contains(EthernetPacket.class)) {
                pRef.set(packet);
            }
        };
    }
}
