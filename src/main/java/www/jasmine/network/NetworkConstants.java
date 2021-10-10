package www.jasmine.network;

// ToDo: Put in the config file and read from there
/**
 * Assumption about the basic parameter of the network interface
 */
public class NetworkConstants {
    // Max length of the datagram the network interface can handle
    final static int SNAPLEN = 65536; // bytes
    // Max time-out to receive datagram from the network interface
    final static int  READ_TIMEOUT = 200; // ms
    // Max waiting time to get an Ethernet packet from the network interface
    final static long WAIT_FOR_RESPONSE_TIMEOUT = 2000; // ms
}
