package www.jasmine.model.network;

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

    public float getDelayInMilliseconds() {
        return (float) delay / 1000000;
    }

    @Override
    public String toString() {
        return "ReceivedPacket{" +
                "packet=" + (packet == null ? "null" : packet.toString()) +
                ", delay=" + delay +
                '}';
    }
}
