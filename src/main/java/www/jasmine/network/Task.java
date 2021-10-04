package www.jasmine.network;

import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;

public class Task implements Runnable {
    private int packetCount;
    private PcapHandle handle;
    private PacketListener listener;

    public Task(PcapHandle handle, PacketListener listener, int packetCount) {
        this.handle = handle;
        this.listener = listener;
        this.packetCount = packetCount;
    }

    @Override
    public void run() {
        try {
            // packetCount = -1 : run forever
            // packetCount = 1 : run and stop as soon as receive 1 packet
            handle.loop(packetCount, listener);
        } catch (PcapNativeException | InterruptedException | NotOpenException  e) {
            e.printStackTrace();
        }
    }
}
