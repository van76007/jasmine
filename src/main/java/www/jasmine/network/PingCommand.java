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
import java.util.Random;

public class PingCommand extends AbstractNetworkCommand {
    final static int TTL = 100;
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
            setupSendPacketHandler();
            String reportMessage = loop();
            report = new Report(remoteInetAddress.getHostAddress(),
                    reportMessage == null ? timeoutMessage : reportMessage);
        } catch(PcapNativeException | NotOpenException e) {
            e.printStackTrace();
        } finally {
            closeExecutor(executor);
            closeHandler(sendHandle);
        }
        return report;
    }

    private String loop() throws PcapNativeException, NotOpenException {
        Random random = new Random();
        short identifier = (short) random.nextInt(1 << 15);
        System.out.println("COMMAND: " + this.getClass().getName() + " identifier: " + identifier);

        ReportBuilder reportBuilder = new ReportBuilder();
        Counter counter = initializeCounter();
        while(shouldContinue(counter)) {
            System.out.println(this.getClass().getName() + " ProcessPacketResult: " + counter.toString());
            setNextTTL(counter);
            Packet packet = buildPacket(counter.getSequence(), counter.getTtl(), identifier, parameter, remoteInetAddress);
            if (counter.getSequence() > 0) {
                pause();
            }
            ReceivedPacket receivedPacket = sendAndReceivePacket(packet, identifier);
            processReceivedPacket(receivedPacket, counter, reportBuilder, identifier);
        }
        System.out.println(this.getClass().getName() + " LAST ProcessPacketResult: " + reportBuilder.toString());
        return reportBuilder.getReportMessage();
    }

    protected Counter initializeCounter() {
        return new Counter(0, TTL);
    }

    protected boolean shouldContinue(Counter counter) {
        return counter.getSequence() < config.getCount();
    }

    protected void setNextTTL(Counter counter) {
        counter.setTtl(TTL);
    }

    protected void pause() {
        try {
            Thread.sleep(config.getWait());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void processReceivedPacket(ReceivedPacket receivedPacket, Counter counter, ReportBuilder reportBuilder, short identifier) {
        String message;
        Packet packet = receivedPacket.getPacket();
        if (packet != null) {
            if (packet.contains(IcmpV4EchoReplyPacket.class)) {
                IpV4Packet ipPacket = packet.get(IpV4Packet.class);
                IcmpV4EchoReplyPacket echo = packet.get(IcmpV4EchoReplyPacket.class);

                InetAddress hopAddress = ipPacket.getHeader().getSrcAddr();
                System.out.println("PING. Got IcmpV4EchoReplyPacket from: " + hopAddress.getHostName() + " my identifier: " + identifier
                        + " their identifier: " + echo.getHeader().getIdentifier() + " icmp_seq: " + echo.getHeader().getSequenceNumberAsInt()
                        + " for this host: " + remoteInetAddress.toString());

                message = String.format("%d bytes from %s: icmp_seq=%d ttl=%d time=%.2f ms",
                        echo.length(), remoteInetAddress.getHostAddress(), counter.getSequence(),
                        ipPacket.getHeader().getTtl(), receivedPacket.getDelayInMilliseconds());
                reportBuilder.appendReportMessage(message);
            }
        }

        counter.increaseSequence(1);
        counter.increaseTtl(1);
    }

    /**
     * Filter the received packet from the network interface
     * @param packet The received packet from the network interface
     * @return true if and only if:
     *                            1. It is ICMP reply package
     *                            2. The ICMP packet identifier is the same as the one of the ICMP echo packet
     */
    @Override
    protected boolean isExpectedReply(Packet packet, short identifier) {
        if (packet != null && packet.contains(IcmpV4EchoReplyPacket.class)) {
            IcmpV4EchoReplyPacket echo = packet.get(IcmpV4EchoReplyPacket.class);
            return echo.getHeader().getIdentifier() == identifier;
        }
        return false;
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
    protected Packet buildPacket(int count, int ttl, short identifier, NetworkParameter parameter, InetAddress dstIpAddress) {
        MacAddress srcMacAddress = parameter.getLocalMac();
        MacAddress dstMacAddress = parameter.getDefaultGatewayMac();
        InetAddress srcIpAddress = parameter.getLocalIP();
        IcmpV4EchoPacket.Builder echoBuilder = getIcmpEchoPacketBuilder((short) count, identifier);
        IcmpV4CommonPacket.Builder icmpV4CommonBuilder = getIcmpPacketBuilder(echoBuilder);
        IpV4Packet.Builder ipV4Builder = getIpPacketBuilder((short) count, ttl, (Inet4Address) srcIpAddress, (Inet4Address) dstIpAddress, icmpV4CommonBuilder);
        EthernetPacket.Builder etherBuilder = getEthernetPacketBuilder(srcMacAddress, dstMacAddress, ipV4Builder);
        return etherBuilder.build();
    }

    private IcmpV4EchoPacket.Builder getIcmpEchoPacketBuilder(short count, short identifier) {
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
