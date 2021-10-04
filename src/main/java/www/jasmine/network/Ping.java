package www.jasmine.network;

import org.pcap4j.core.*;
import org.pcap4j.packet.*;
import org.pcap4j.packet.namednumber.*;
import org.pcap4j.util.MacAddress;
import www.jasmine.config.PingConfig;
import www.jasmine.report.Report;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static www.jasmine.network.NetworkConstants.*;

public class Ping {
    NetworkParameter networkParameter;
    InetAddress hostInetAddress;
    PingConfig config;

    PcapHandle receiveHandle = null;
    PcapHandle sendHandle = null;
    ExecutorService executor = Executors.newSingleThreadExecutor();

    public Ping(NetworkParameter networkParameter, InetAddress hostInetAddress, PingConfig config) throws UnknownHostException {
        this.networkParameter = networkParameter;
        this.hostInetAddress = hostInetAddress;
        this.config = config;
    }

    public Report pingByICMP() {
        Report report = null;
        try {
            PcapNetworkInterface nif = networkParameter.getNif();
            receiveHandle = nif.openLive(SNAPLEN, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, READ_TIMEOUT);
            receiveHandle.setFilter("icmp and ether dst " + Pcaps.toBpfString(networkParameter.getLocalMac()) + " and src host " + hostInetAddress.getHostAddress(), BpfProgram.BpfCompileMode.OPTIMIZE);
            sendHandle = nif.openLive(SNAPLEN, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, READ_TIMEOUT);
            String reportMessage = loop(receiveHandle, sendHandle, executor);
            report = new Report(hostInetAddress.getHostAddress(), reportMessage == null ? String.format("Request timeout for icmp_seq %d", config.getCount()) : reportMessage);
        } catch(PcapNativeException | NotOpenException e) {
            e.printStackTrace();
        } finally {
            closeExecutor(executor);
            closeHandler(receiveHandle);
        }
        return report;
    }

    private String loop(PcapHandle receiveHandle, PcapHandle sendHandle, ExecutorService executor) throws PcapNativeException, NotOpenException {
        String reportMessage = null;
        final AtomicReference<Packet> pRef = new AtomicReference<>();
        PacketListener listener = packet -> {
            if (packet.contains(EthernetPacket.class)) {
                pRef.set(packet);
            }
        };
        int count = 0;
        while(count < this.config.getCount()) {
            Task receiveTask = new Task(receiveHandle, listener, 1);
            Future receiveFuture = executor.submit(receiveTask);

            long tStart = sendICMPPacket(sendHandle, count++,
                    networkParameter.getLocalMac(), networkParameter.getLocalIP(),
                    networkParameter.getDefaultGatewayMac(), hostInetAddress);
            try {
                receiveFuture.get(WAIT_FOR_RESPONSE_TIMEOUT, TimeUnit.MILLISECONDS);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            long tDelay = ( System.nanoTime() - tStart ) / 1000000;
            Packet packet = pRef.get();
            if (packet != null) {
                if (packet.contains(IcmpV4EchoReplyPacket.class)) {
                    IpV4Packet p = packet.get(IpV4Packet.class);
                    IcmpV4EchoReplyPacket pp = packet.get(IcmpV4EchoReplyPacket.class);
                    count++;
                    reportMessage = String.format("%d bytes from %s: icmp_seq=%d ttl=%d time=%d ms",
                            pp.length(), hostInetAddress.getHostAddress(), pp.getHeader().getSequenceNumber(),
                            p.getHeader().getTtl(), tDelay);
                }
            }
            count++;
        }
        return reportMessage;
    }

    private long sendICMPPacket(PcapHandle sendHandle, int count, MacAddress srcMacAddress, InetAddress srcIpAddress, MacAddress dstMacAddress, InetAddress dstIpAddress) throws PcapNativeException, NotOpenException {
        if (count > 1) {
            try {
                Thread.sleep(config.getWait());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        byte[] echoData = new byte[48];
        for (int i = 0; i < echoData.length; i++) {
            echoData[i] = (byte) i;
        }

        IcmpV4EchoPacket.Builder echoBuilder = new IcmpV4EchoPacket.Builder();
        echoBuilder
                .identifier((short) count)
                .sequenceNumber((short) count)
                .payloadBuilder(new UnknownPacket.Builder().rawData(echoData));

        IcmpV4CommonPacket.Builder icmpV4CommonBuilder = new IcmpV4CommonPacket.Builder();
        icmpV4CommonBuilder
                .type(IcmpV4Type.ECHO)
                .code(IcmpV4Code.NO_CODE)
                .payloadBuilder(echoBuilder)
                .correctChecksumAtBuild(true);

        IpV4Packet.Builder ipV4Builder = new IpV4Packet.Builder();
        ipV4Builder
                .version(IpVersion.IPV4)
                .tos(IpV4Rfc791Tos.newInstance((byte) 0))
                .ttl((byte) 100)
                .identification((short) count)
                .protocol(IpNumber.ICMPV4)
                .srcAddr((Inet4Address) srcIpAddress)
                .dstAddr((Inet4Address) dstIpAddress)
                .payloadBuilder(icmpV4CommonBuilder)
                .correctChecksumAtBuild(true)
                .correctLengthAtBuild(true);

        EthernetPacket.Builder etherBuilder = new EthernetPacket.Builder();
        etherBuilder
                .dstAddr(dstMacAddress)
                .srcAddr(srcMacAddress)
                .type(EtherType.IPV4)
                .paddingAtBuild(true);

        /*
        long tStart = System.nanoTime();
        for (final Packet ipV4Packet : IpV4Helper.fragment(ipV4Builder.build(), MTU)) {
            etherBuilder.payloadBuilder(
                    new AbstractPacket.AbstractBuilder() {
                        @Override
                        public Packet build() {
                            return ipV4Packet;
                        }
                    });

            Packet p = etherBuilder.build();
            sendHandle.sendPacket(p);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                break;
            }
        }
         */

        etherBuilder.payloadBuilder(
                new AbstractPacket.AbstractBuilder() {
                    @Override
                    public Packet build() {
                        return ipV4Builder.build();
                    }
                });
        Packet p = etherBuilder.build();
        long tStart = System.nanoTime();
        sendHandle.sendPacket(p);
        return tStart;
    }

    private void closeExecutor(ExecutorService executor) {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }

    private void closeHandler(PcapHandle handler) {
        if (handler != null) {
            try {
                handler.breakLoop();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            try {
                handler.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
