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

public class PingCommand extends AbstractNetworkCommand {
    PingConfig config;
    InetAddress remoteInetAddress;
    String timeoutMessage;

    public PingCommand(NetworkParameter parameter, InetAddress remoteInetAddress) {
        super(parameter);
        this.remoteInetAddress = remoteInetAddress;
    }

    public PingCommand(NetworkParameter parameter, InetAddress remoteInetAddress, PingConfig config) {
        this(parameter, remoteInetAddress);
        this.config = config;
        this.timeoutMessage = String.format("Request timeout for icmp_seq %d", config.getCount());
        this.bpfExpression = "icmp and ether dst " + Pcaps.toBpfString(parameter.getLocalMac()) + " and src host " + remoteInetAddress.getHostAddress();
    }

    public Report ping() {
        Report report = null;
        try {
            setupPacketHandlers();
            String reportMessage = loop();
            report = new Report(remoteInetAddress.getHostAddress(),
                    reportMessage == null ? timeoutMessage : reportMessage);
        } catch(PcapNativeException | NotOpenException e) {
            e.printStackTrace();
        } finally {
            closeExecutor(executor);
            closeHandler(receiveHandle);
        }
        return report;
    }

    protected ProcessPacketResult getProcessPacketResult() {
        return new ProcessPacketResult(false, 0, 100);
    }

    private String loop() {
        ProcessPacketResult processPacketResult = getProcessPacketResult();
        while(shouldSendPacket(processPacketResult)) {
            System.out.println("ProcessPacketResult: " + processPacketResult.toString());
            Packet packet = buildPacket(processPacketResult.getCount(), processPacketResult.getTtl(), parameter, remoteInetAddress);
            if (processPacketResult.getCount() > 0) {
                pause();
            }
            ReceivedPacket receivedPacket = sendAndReceivePacket(packet);
            processReceivedPacket(receivedPacket, processPacketResult);
        }
        System.out.println("LAST ProcessPacketResult: " + processPacketResult.toString());
        return processPacketResult.getReportMessage();
    }

    protected void pause() {
        try {
            Thread.sleep(config.getWait());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected boolean shouldSendPacket(ProcessPacketResult processPacketResult) {
        return !processPacketResult.isLastResult() && processPacketResult.getCount() < config.getCount() + 1;
    }

    protected void processReceivedPacket(ReceivedPacket receivedPacket, ProcessPacketResult processPacketResult) {
        String message = null;
        Packet packet = receivedPacket.getPacket();
        if (packet != null) {
            if (packet.contains(IcmpV4EchoReplyPacket.class)) {
                IpV4Packet p = packet.get(IpV4Packet.class);
                IcmpV4EchoReplyPacket pp = packet.get(IcmpV4EchoReplyPacket.class);

                InetAddress hopAddress = p.getHeader().getSrcAddr();
                /*
                System.out.println("Got IcmpV4EchoReplyPacket getHostName: " + hopAddress.getHostName()
                 + " getHostAddress: " + hopAddress.getHostAddress() + " icmp_seq: " + pp.getHeader().getSequenceNumberAsInt()
                 + " for this host: " + this.remoteInetAddress.toString());
                */
                message = String.format("%d bytes from %s: icmp_seq=%d ttl=%d time=%d ms",
                        pp.length(), remoteInetAddress.getHostAddress(), processPacketResult.getCount(),
                        p.getHeader().getTtl(), receivedPacket.getDelay());
            }
        }
        processPacketResult.setLastResult(false);
        processPacketResult.increaseCount(1);
        processPacketResult.increaseTtl(1);
        processPacketResult.setReportMessage(message);
    }

    @Override
    protected long sendPacket(Packet packet) {
        long start = System.nanoTime();
        try {
            sendHandle.sendPacket(packet);
        } catch (PcapNativeException | NotOpenException e) {
            e.printStackTrace();
        }
        return start;
    }

    @Override
    protected Packet buildPacket(int count, int ttl, NetworkParameter parameter, InetAddress dstIpAddress) {
        MacAddress srcMacAddress = parameter.getLocalMac();
        MacAddress dstMacAddress = parameter.getDefaultGatewayMac();
        InetAddress srcIpAddress = parameter.getLocalIP();
        IcmpV4EchoPacket.Builder echoBuilder = getIcmpEchoPacketBuilder((short) count);
        IcmpV4CommonPacket.Builder icmpV4CommonBuilder = getIcmpPacketBuilder(echoBuilder);
        IpV4Packet.Builder ipV4Builder = getIpPacketBuilder((short) count, ttl, (Inet4Address) srcIpAddress, (Inet4Address) dstIpAddress, icmpV4CommonBuilder);
        EthernetPacket.Builder etherBuilder = getEthernetPacketBuilder(srcMacAddress, dstMacAddress, ipV4Builder);
        return etherBuilder.build();
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

    private IpV4Packet.Builder getIpPacketBuilder(short count, int ttl, Inet4Address srcIpAddress, Inet4Address dstIpAddress, IcmpV4CommonPacket.Builder icmpV4CommonBuilder) {
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
