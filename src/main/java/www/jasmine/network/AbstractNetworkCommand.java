package www.jasmine.network;

import org.pcap4j.core.*;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.Packet;
import www.jasmine.SingletonLogger;
import www.jasmine.model.network.NetworkParameter;
import www.jasmine.model.network.ReceivedPacket;

import java.net.InetAddress;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import static www.jasmine.network.NetworkConstants.*;

/**
 * To generalize the operation of any network command. It composes of sending and receiving packet
 */
public abstract class AbstractNetworkCommand {
    Logger logger = SingletonLogger.SingletonLogger().logger;
    NetworkParameter parameter;
    PcapHandle sendHandle;
    String bpfExpression;
    ExecutorService executor = Executors.newSingleThreadExecutor();

    public AbstractNetworkCommand(NetworkParameter parameter) {
        this.parameter = parameter;
    }

    protected abstract long sendPacket(Packet packet);

    protected abstract Packet buildPacket(int count, int ttl, short identifier, NetworkParameter parameter, InetAddress dstIpAddress);

    protected void setupSendPacketHandler() throws PcapNativeException {
        sendHandle = parameter.getNif().openLive(SNAPLEN, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, READ_TIMEOUT);
    }

    /**
     * Send a packet and waiting for the reply of the packet
     * @param packet Packet to be sent
     * @param identifier An ID of the sent packet that help to associate the receiving packet with the sent one
     * @return The reply packet
     * @throws PcapNativeException
     * @throws NotOpenException
     */
    protected ReceivedPacket sendAndReceivePacket(Packet packet, final short identifier) throws PcapNativeException, NotOpenException {
        PcapHandle receiveHandle = parameter.getNif().openLive(SNAPLEN, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, READ_TIMEOUT);
        receiveHandle.setFilter(bpfExpression, BpfProgram.BpfCompileMode.OPTIMIZE);
        final AtomicReference<Packet> pRef = new AtomicReference<>();
        PacketListener listener = p -> {
            if (p.contains(EthernetPacket.class) && isExpectedReply(p, identifier)) {
                pRef.set(p);
            }
        };
        ReceivingPacketTask receivingPacketTask = new ReceivingPacketTask(receiveHandle, listener, 1);
        Future receiveFuture = executor.submit(receivingPacketTask);
        long start = sendPacket(packet);
        try {
            receiveFuture.get(WAIT_FOR_RESPONSE_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            logger.warning(e.getMessage() != null ? e.getMessage() : "Receive packet timed out");
        }
        long delay = System.nanoTime() - start;
        closeHandler(receiveHandle);
        return new ReceivedPacket(pRef.get(), delay);
    }

    /**
     * As we open the network interface in the PROMISCUOUS mode, we filter only the reply packet which is corresponding
     * to the send packet
     * @param packet Packet received from the Internet
     * @param identifier ID of the sent packet that can help to correlate the reply to the sent packet
     * @return true if there is a match
     */
    protected boolean isExpectedReply(Packet packet, short identifier) {
        return true;
    }

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
                logger.severe(e.getMessage());
            }
            try {
                handler.close();
            }
            catch (Exception e) {
                logger.severe(e.getMessage());
            }
        }
    }
}
