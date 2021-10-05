package www.jasmine.network;

import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.*;
import www.jasmine.config.TracertConfig;

import java.net.InetAddress;

public class Tracert extends Ping {
    TracertConfig config;

    public Tracert(NetworkParameter parameter, InetAddress remoteInetAddress) {
        super(parameter, remoteInetAddress);
    }

    public Tracert(NetworkParameter parameter, InetAddress remoteInetAddress, TracertConfig config) {
        this(parameter, remoteInetAddress);
        this.config = config;
        this.timeoutMessage = String.format("Request timeout for max_ttl %d", config.getMaxTtl());
        this.bpfExpression = "icmp and dst host " + parameter.getLocalIP().getHostAddress();
        System.out.println("CERT bpfExpression: " + bpfExpression);
    }

    @Override
    protected ProcessPacketResult getProcessPacketResult() {
        return new ProcessPacketResult(false, 0, 1);
    }

    @Override
    protected void pause() {
        try {
            Thread.sleep(config.getPause());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected boolean shouldSendPacket(ProcessPacketResult processPacketResult) {
        System.out.println("processPacketResult:" + processPacketResult.toString());
        return !processPacketResult.isLastResult() && processPacketResult.getTtl() < config.getMaxTtl();
    }

    @Override
    protected void processReceivedPacket(ReceivedPacket receivedPacket, ProcessPacketResult processPacketResult) {
        Packet packet = receivedPacket.getPacket();
        String reportMessage = null;
        if (packet != null) {
            if (packet.contains(IcmpV4TimeExceededPacket.class)) {
                processPacketResult.setLastResult(false);
                int count = processPacketResult.getCount();
                processPacketResult.setTtl((count - count % config.getNumberOfProbes()) / config.getNumberOfProbes() + 1);

                IpV4Packet p = packet.get(IpV4Packet.class);
                InetAddress hopAddress = p.getHeader().getSrcAddr();
                System.out.println("Got IcmpV4TimeExceededPacket hopAddress.getHostName(): " + hopAddress.getHostName());
                System.out.println("Got IcmpV4TimeExceededPacket hopAddress.getHostAddress(): " + hopAddress.getHostAddress());
                reportMessage = String.format("%d %s (%s) %d ms", processPacketResult.getTtl() + 1, hopAddress.getHostName(), hopAddress.getHostAddress(), receivedPacket.getDelay());
            }

            if (packet.contains(IcmpV4DestinationUnreachablePacket.class) || packet.contains(IcmpV4EchoReplyPacket.class)) {
                processPacketResult.setLastResult(true);
                int ttl = processPacketResult.getTtl();
                IpV4Packet p = packet.get(IpV4Packet.class);
                InetAddress hopAddress = p.getHeader().getSrcAddr();

                if (packet.contains(IcmpV4DestinationUnreachablePacket.class)) {
                    System.out.println("Got IcmpV4DestinationUnreachablePacket: " + hopAddress.getHostName());
                    System.out.println("Got IcmpV4DestinationUnreachablePacket: " + hopAddress.getHostAddress());

                }
                if (packet.contains(IcmpV4EchoReplyPacket.class)) {
                    System.out.println("Got IcmpV4EchoReplyPacket: " + hopAddress.getHostName());
                    System.out.println("Got IcmpV4EchoReplyPacket: " + hopAddress.getHostAddress());
                }
                reportMessage = String.format("%d %s (%s) %d ms", ttl + 1, hopAddress.getHostName(), hopAddress.getHostAddress(), receivedPacket.getDelay());
            }
        } else {
            int count = processPacketResult.getCount();
            processPacketResult.setTtl((count - count % config.getNumberOfProbes()) / config.getNumberOfProbes());
        }

        processPacketResult.increaseCount(1);
        processPacketResult.setReportMessage(reportMessage);
    }
}
