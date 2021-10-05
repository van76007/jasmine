package www.jasmine.network;

import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.*;
import org.pcap4j.packet.namednumber.*;
import org.pcap4j.util.MacAddress;
import www.jasmine.config.PingConfig;
import www.jasmine.report.Report;

import java.net.Inet4Address;
import java.net.InetAddress;

public class Ping extends AbstractNetworkTask {
    PingConfig config;
    InetAddress remoteInetAddress;

    public Ping(NetworkParameter parameter, InetAddress remoteInetAddress, PingConfig config) {
        super(parameter);
        this.config = config;
        this.remoteInetAddress = remoteInetAddress;
        this.bpfExpression = "icmp and ether dst " + Pcaps.toBpfString(parameter.getLocalMac()) + " and src host " + remoteInetAddress.getHostAddress();
    }

    public Report pingByICMP() {
        Report report = null;
        try {
            setupPacketHandlers();
            String reportMessage = loop();
            report = new Report(remoteInetAddress.getHostAddress(),
                    reportMessage == null ? String.format("Request timeout for icmp_seq %d", config.getCount()) : reportMessage);
        } catch(PcapNativeException | NotOpenException e) {
            e.printStackTrace();
        } finally {
            closeExecutor(executor);
            closeHandler(receiveHandle);
        }
        return report;
    }

    private String loop() {
        String reportMessage = null;
        int count = 0;
        while(count < config.getCount()) {
            ReceivedPacket receivedPacket = sendAndReceivePacket(count);
            Packet packet = receivedPacket.getPacket();
            if (packet != null) {
                if (packet.contains(IcmpV4EchoReplyPacket.class)) {
                    IpV4Packet p = packet.get(IpV4Packet.class);
                    IcmpV4EchoReplyPacket pp = packet.get(IcmpV4EchoReplyPacket.class);
                    reportMessage = String.format("%d bytes from %s: icmp_seq=%d ttl=%d time=%d ms",
                            pp.length(), remoteInetAddress.getHostAddress(), pp.getHeader().getSequenceNumber(),
                            p.getHeader().getTtl(), receivedPacket.getDelay());
                }
            }
            count++;
        }
        return reportMessage;
    }

    @Override
    protected long sendPacket(int count) {
        long start = System.nanoTime();
        try {
            start = sendICMPPacket(count,
                    parameter.getLocalMac(), parameter.getLocalIP(),
                    parameter.getDefaultGatewayMac(), remoteInetAddress);
        } catch (PcapNativeException | NotOpenException e) {
            e.printStackTrace();
        }
        return start;
    }

    private long sendICMPPacket(int count, MacAddress srcMacAddress, InetAddress srcIpAddress, MacAddress dstMacAddress, InetAddress dstIpAddress) throws PcapNativeException, NotOpenException {
        if (count > 1) {
            try {
                Thread.sleep(config.getWait());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        IcmpV4EchoPacket.Builder echoBuilder = getIcmpEchoPacketBuilder((short) count);
        IcmpV4CommonPacket.Builder icmpV4CommonBuilder = getIcmpPacketBuilder(echoBuilder);
        IpV4Packet.Builder ipV4Builder = getIpPacketBuilder((short) count, (Inet4Address) srcIpAddress, (Inet4Address) dstIpAddress, icmpV4CommonBuilder);
        EthernetPacket.Builder etherBuilder = getEthernetPacketBuilder(srcMacAddress, dstMacAddress, ipV4Builder);

        Packet p = etherBuilder.build();
        long tStart = System.nanoTime();
        sendHandle.sendPacket(p);
        return tStart;
    }

    private IcmpV4EchoPacket.Builder getIcmpEchoPacketBuilder(short count) {
        byte[] echoData = new byte[48];
        for (int i = 0; i < echoData.length; i++) {
            echoData[i] = (byte) i;
        }

        IcmpV4EchoPacket.Builder echoBuilder = new IcmpV4EchoPacket.Builder();
        echoBuilder
                .identifier(count)
                .sequenceNumber(count)
                .payloadBuilder(new UnknownPacket.Builder().rawData(echoData));
        return echoBuilder;
    }

    private IcmpV4CommonPacket.Builder getIcmpPacketBuilder(IcmpV4EchoPacket.Builder echoBuilder) {
        IcmpV4CommonPacket.Builder icmpV4CommonBuilder = new IcmpV4CommonPacket.Builder();
        icmpV4CommonBuilder
                .type(IcmpV4Type.ECHO)
                .code(IcmpV4Code.NO_CODE)
                .payloadBuilder(echoBuilder)
                .correctChecksumAtBuild(true);
        return icmpV4CommonBuilder;
    }
    // ToDo: Tracert should pass TTL parameter
    private IpV4Packet.Builder getIpPacketBuilder(short count, Inet4Address srcIpAddress, Inet4Address dstIpAddress, IcmpV4CommonPacket.Builder icmpV4CommonBuilder) {
        IpV4Packet.Builder ipV4Builder = new IpV4Packet.Builder();
        ipV4Builder
                .version(IpVersion.IPV4)
                .tos(IpV4Rfc791Tos.newInstance((byte) 0))
                .ttl((byte) 100)
                .identification(count)
                .protocol(IpNumber.ICMPV4)
                .srcAddr(srcIpAddress)
                .dstAddr(dstIpAddress)
                .payloadBuilder(icmpV4CommonBuilder)
                .correctChecksumAtBuild(true)
                .correctLengthAtBuild(true);
        return ipV4Builder;
    }

    private EthernetPacket.Builder getEthernetPacketBuilder(MacAddress srcMacAddress, MacAddress dstMacAddress, IpV4Packet.Builder ipV4Builder) {
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
