package www.jasmine.network;

import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import www.jasmine.SingletonLogger;

import java.util.logging.Logger;

public class ReceivingPacketTask implements Runnable {
    Logger logger = SingletonLogger.SingletonLogger().logger;
    private int packetCount;
    private PcapHandle handle;
    private PacketListener listener;

    public ReceivingPacketTask(PcapHandle handle, PacketListener listener, int packetCount) {
        this.handle = handle;
        this.listener = listener;
        this.packetCount = packetCount;
    }

    @Override
    public void run() {
        try {
            handle.loop(packetCount, listener);
        } catch (PcapNativeException | InterruptedException | NotOpenException  e) {
            logger.warning(e.getMessage() != null ? e.getMessage() : "interrupt receiving packet");
        }
    }
}
