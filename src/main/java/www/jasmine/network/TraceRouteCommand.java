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
    protected Counter initializeCounter() {
        return new Counter( 1, 1);
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
    protected boolean shouldContinue(Counter counter) {
        System.out.println(this.getClass().getName() + " counter:" + counter.toString());
        return counter.getTtl() <= config.getMaxTtl()
                && counter.getSequence() <= config.getNumberOfProbes() * config.getMaxTtl();
    }

    @Override
    protected void setNextTTL(Counter counter) {
        int sequence = counter.getSequence();
        int quotient = (sequence - 1) / config.getNumberOfProbes();
        int newTtl = quotient + 1;
        counter.setTtl(newTtl);
        System.out.println(String.format("sequence=%d newTtl=%d", sequence, newTtl));
    }

    @Override
    protected void processReceivedPacket(ReceivedPacket receivedPacket, Counter counter, ReportBuilder reportBuilder, short identifier) {
        Packet packet = receivedPacket.getPacket();
        String reportMessage;
        if (packet != null && packet.contains(IcmpV4TimeExceededPacket.class)) {
            IpV4Packet ipPacket = packet.get(IpV4Packet.class);
            InetAddress hopAddress = ipPacket.getHeader().getSrcAddr();
            reportMessage = String.format("%d %s (%s) %d ns %.2f ms",
                    counter.getTtl(),
                    hopAddress.getHostName(),
                    hopAddress.getHostAddress(),
                    receivedPacket.getDelay(),
                    receivedPacket.getDelayInMilliseconds());
            reportBuilder.appendReportMessage(reportMessage);

            System.out.println("TRACEROUTE to " + remoteInetAddress.toString() + " hop: " + hopAddress.getHostName() + " TTL: " + counter.getTtl());
            if(hopAddress.getHostName().equals("192.168.1.1") && counter.getTtl() > 1) {
                System.out.println("ROUGE PACKAGE: " + identifier + " remote IP: " + remoteInetAddress.toString());
                System.out.println(packet.toString());
            }

            counter.increaseSequence(1);
        }
    }

    /**
     * Filter the received packet from the network interface
     * @param packet The received packet from the network interface
     * @return true if and only if:
     *                            1. It is ICMP time exceeded package
     *                            2. The ICMP packet contain another IP frame
     *                            3. That IP frame having the same destination IP as the IP we are tracing route
     */
    @Override
    protected boolean isExpectedReply(Packet packet, short identifier) {
        if (packet != null && packet.contains(IcmpV4TimeExceededPacket.class)) {
            IcmpV4TimeExceededPacket timeExceededPacket = packet.get(IcmpV4TimeExceededPacket.class);
            IpV4Packet insideIpPacket = timeExceededPacket.get(IpV4Packet.class);
            InetAddress dstAddr = insideIpPacket.getHeader().getDstAddr();
            return isTheSameIpAddress(dstAddr, remoteInetAddress) && isTheSameIdentifier(timeExceededPacket, identifier);
        }
        return false;
    }

    private boolean isTheSameIdentifier(IcmpV4TimeExceededPacket timeExceededPacket, short identifier) {
        IcmpV4EchoPacket insideIcmpV4packet = timeExceededPacket.get(IcmpV4EchoPacket.class);
        return insideIcmpV4packet.getHeader().getIdentifier() == identifier;
    }

    private boolean isTheSameIpAddress(InetAddress anIp, InetAddress anotherIp) {
        return anIp.getHostAddress().equals(anotherIp.getHostAddress());
    }
}