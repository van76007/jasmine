package www.jasmine.network;

import org.pcap4j.packet.*;
import www.jasmine.config.TracertConfig;

import java.net.InetAddress;

public class TraceRouteCommand extends PingCommand {
    TracertConfig config;

    public TraceRouteCommand(NetworkParameter parameter, InetAddress remoteInetAddress) {
        super(parameter, remoteInetAddress);
    }

    public TraceRouteCommand(NetworkParameter parameter, InetAddress remoteInetAddress, TracertConfig config) {
        this(parameter, remoteInetAddress);
        this.config = config;
        this.timeoutMessage = String.format("Request timeout for max_ttl %d", config.getMaxTtl());
        this.bpfExpression = "icmp and dst host " + parameter.getLocalIP().getHostAddress();
    }

    @Override
    protected ProcessPacketResult getProcessPacketResult() {
        return new ProcessPacketResult( 0, 1);
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
        return processPacketResult.getTtl() <= config.getMaxTtl();
    }

    @Override
    protected void processReceivedPacket(ReceivedPacket receivedPacket, ProcessPacketResult processPacketResult, short identifier) {
        Packet packet = receivedPacket.getPacket();
        String reportMessage;

        if (packet != null) {
            if (packet.contains(IcmpV4TimeExceededPacket.class)) {
                IcmpV4TimeExceededPacket timeExceededPacket = packet.get(IcmpV4TimeExceededPacket.class);
                IpV4Packet insideIpPacket = timeExceededPacket.get(IpV4Packet.class);
                InetAddress dstAddr = insideIpPacket.getHeader().getDstAddr();
                // Only consider the ICMP packet that contains another IP packet having the IP being traced route
                if (isTheSameIpAddress(dstAddr, remoteInetAddress)) {
                    IpV4Packet ipPacket = packet.get(IpV4Packet.class);
                    InetAddress hopAddress = ipPacket.getHeader().getSrcAddr();
                    reportMessage = String.format("%d %s (%s) %d ms", processPacketResult.getTtl(), hopAddress.getHostName(), hopAddress.getHostAddress(), receivedPacket.getDelay());
                    processPacketResult.setReportMessage(reportMessage);

                    System.out.println("TRACEROUTE from: " + hopAddress.getHostName() + " TTL: " + processPacketResult.getTtl()
                            + " to host: " + remoteInetAddress.toString());

                    // Try to probe the same host again, i.e. reuse the same TTL
                    int count = processPacketResult.getCount();
                    int newTtl = (count - count % config.getNumberOfProbes()) / config.getNumberOfProbes() + 1;
                    System.out.println(String.format("Host %s Count_TTL=(%d, %d)", remoteInetAddress.getHostName(), count, newTtl));
                    processPacketResult.setTtl(newTtl);
                    processPacketResult.increaseCount(1);
                } else {
                    System.out.println("Received from not the same IP: " + dstAddr.toString());
                }
            } else {
                System.out.println("Not expected Time exceed packet");
            }
        }
    }

    private boolean isTheSameIpAddress(InetAddress anIp, InetAddress anotherIp) {
        return anIp.getHostAddress().equals(anotherIp.getHostAddress());
    }
}
