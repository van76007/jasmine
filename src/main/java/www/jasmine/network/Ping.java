package www.jasmine.network;

import org.pcap4j.core.*;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.Packet;
import www.jasmine.report.Report;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static www.jasmine.network.NetworkConstants.*;

public class Ping {
    NetworkParameter networkParameter;
    String host;

    public Ping(NetworkParameter networkParameter, String host) {
        this.networkParameter = networkParameter;
        this.host = host;
    }

    public Report pingByICMP() {
        return null;
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
