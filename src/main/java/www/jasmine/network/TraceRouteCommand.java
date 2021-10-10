package www.jasmine.network;

import org.pcap4j.packet.*;
import www.jasmine.Command;
import www.jasmine.model.config.TracertConfig;
import www.jasmine.model.network.Counter;
import www.jasmine.model.network.NetworkParameter;
import www.jasmine.model.network.ReceivedPacket;
import www.jasmine.model.report.Report;
import www.jasmine.report.ReportBuilder;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Trace route by ICMP echo packet, starting with TTL = 1
 * If receiving the ICMP Time exceeded in transit packet from a hop, try to probe the same hop again. It means we retain
 * the same TTL. If number of probes is reached for a hop, try to reach to the next hop by increasing the TTL by 1.
 * Repeat until the TTL reach the max TTL (default value is 64)
 */
public class TraceRouteCommand extends PingCommand {
    TracertConfig config;

    public TraceRouteCommand(NetworkParameter parameter, String host, TracertConfig config) throws UnknownHostException {
        super(parameter, host);
        this.config = config;
        this.timeoutMessage = String.format("Trace route timeout for max_ttl %d", config.getMaxTtl());
        this.bpfExpression = "icmp and dst host " + parameter.getLocalIP().getHostAddress();
    }

    public Report trace() {
        String reportMessage = sendICMPPackets();
        String finalReportMessage = reportMessage == null ? timeoutMessage : reportMessage;
        return new Report(host, finalReportMessage, Command.TRACEROUTE);
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
            logger.severe(e.getMessage());
        }
    }

    @Override
    protected boolean shouldContinue(Counter counter) {
        return counter.getTtl() <= config.getMaxTtl()
                && counter.getSequence() <= config.getNumberOfProbes() * config.getMaxTtl();
    }

    @Override
    protected void processReceivedPacket(ReceivedPacket receivedPacket, Counter counter, ReportBuilder reportBuilder, short identifier) {
        Packet packet = receivedPacket.getPacket();
        String reportMessage;
        if (packet != null && packet.contains(IcmpV4TimeExceededPacket.class)) {
            IpV4Packet ipPacket = packet.get(IpV4Packet.class);
            InetAddress hopAddress = ipPacket.getHeader().getSrcAddr();
            reportMessage = String.format("%d %s (%s) %.2f ms",
                    counter.getTtl(),
                    hopAddress.getHostName(),
                    hopAddress.getHostAddress(),
                    receivedPacket.getDelayInMilliseconds());
            reportBuilder.appendReportMessage(reportMessage);
            setNextCounter(counter);
        }
    }

    /**
     * Probe each hop that return ICMP Time Exceeded message until max number of probes reached
     * @param counter Counter value to be updated
     *                If number of seeing the same hop reach max number of probe, increase TTL
     *                Else keep the same TTL
     */
    @Override
    protected void setNextCounter(Counter counter) {
        if (counter.getSeeTheSameHopCount() < config.getNumberOfProbes() - 1) {
            counter.increaseSeeTheSameHostCount(1);
        } else {
            counter.setSeeTheSameHopCount(0);
            counter.increaseTtl(1);
        }
        counter.increaseSequence(1);
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