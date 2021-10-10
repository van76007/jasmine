package www.jasmine.network;

import org.pcap4j.packet.*;
import org.pcap4j.packet.namednumber.*;
import org.pcap4j.util.MacAddress;

import java.net.Inet4Address;

public class PacketBuilderFactory {
    public static IcmpV4EchoPacket.Builder getIcmpEchoPacketBuilder(short count, short identifier) {
        byte[] echoData = new byte[48];
        for (int i = 0; i < echoData.length; i++) {
            echoData[i] = (byte) i;
        }
        IcmpV4EchoPacket.Builder echoBuilder = new IcmpV4EchoPacket.Builder();
        echoBuilder
                .identifier(identifier)
                .sequenceNumber(count)
                .payloadBuilder(new UnknownPacket.Builder().rawData(echoData));
        return echoBuilder;
    }

    public static IcmpV4CommonPacket.Builder getIcmpPacketBuilder(IcmpV4EchoPacket.Builder echoBuilder) {
        IcmpV4CommonPacket.Builder icmpV4CommonBuilder = new IcmpV4CommonPacket.Builder();
        icmpV4CommonBuilder
                .type(IcmpV4Type.ECHO)
                .code(IcmpV4Code.NO_CODE)
                .payloadBuilder(echoBuilder)
                .correctChecksumAtBuild(true);
        return icmpV4CommonBuilder;
    }

    public static IpV4Packet.Builder getIpPacketBuilder(short count, int ttl, Inet4Address srcIpAddress, Inet4Address dstIpAddress, IcmpV4CommonPacket.Builder icmpV4CommonBuilder) {
        IpV4Packet.Builder ipV4Builder = new IpV4Packet.Builder();
        ipV4Builder
                .version(IpVersion.IPV4)
                .tos(IpV4Rfc791Tos.newInstance((byte) 0))
                .ttl((byte) ttl)
                .identification(count)
                .protocol(IpNumber.ICMPV4)
                .srcAddr(srcIpAddress)
                .dstAddr(dstIpAddress)
                .payloadBuilder(icmpV4CommonBuilder)
                .correctChecksumAtBuild(true)
                .correctLengthAtBuild(true);
        return ipV4Builder;
    }

    public static EthernetPacket.Builder getEthernetPacketBuilder(MacAddress srcMacAddress, MacAddress dstMacAddress, IpV4Packet.Builder ipV4Builder) {
        EthernetPacket.Builder etherBuilder = new EthernetPacket.Builder();
        etherBuilder
                .dstAddr(dstMacAddress)
                .srcAddr(srcMacAddress)
                .type(EtherType.IPV4)
                .payloadBuilder(ipV4Builder)
                .paddingAtBuild(true);
        return etherBuilder;
    }
}
