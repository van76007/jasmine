package www.jasmine.network;

import org.pcap4j.core.*;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.Packet;

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

    protected ReceivedPacket sendAndReceivePacket(int count) {
        Task receiveTask = new Task(receiveHandle, listener, 1);
        Future receiveFuture = executor.submit(receiveTask);

        long start = sendPacket(count);
        try {
            receiveFuture.get(WAIT_FOR_RESPONSE_TIMEOUT, TimeUnit.MILLISECONDS);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        long delay = (System.nanoTime() - start) / 1000000;
        Packet packet = pRef.get();
        return new ReceivedPacket(packet, delay);
    }

    protected abstract long sendPacket(int count);

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
