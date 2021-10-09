package www.jasmine.network;

import org.pcap4j.packet.Packet;

public class ReceivedPacket {
    Packet packet;
    long delay; // nanoseconds

    public ReceivedPacket(Packet packet, long delay) {
        this.packet = packet;
        this.delay = delay;
    }

    public Packet getPacket() {
        return packet;
    }

    public long getDelay() {
        return delay;
    }
}
