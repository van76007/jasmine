Objectives
==========
To run ping and trace route command.
1. Ping command

   Ping by TCP/IP:
   * Use Java API `InetAddress.isReachable()`
   
   Ping by ICMP:
   * Use network library [pcap4j](https://www.pcap4j.org/) to send packet
   
2. Trace route command
   * Although trace route can be run by sending UDP packet, this implementation uses ICMP.
   In this way, the implementation of Trace route is an extension of Ping by ICMP  

The library *pcap4j* requires the pcap network library to be available to the application. An alternative solution is to
excute an arbitrary OS command (ping/traceroute/tracert) by `Runtime.getRuntime().exec("OS command")`. However, it is
more interesting to craft the packet ourself and implement the protocol, even in its basic format.

Compile and Run
===============

1. Compile

   `mvn clean package`

2. Run

   `java -jar uber-jasmine-1.0-SNAPSHOT.jar config.properties`

3. Output

The application will log in *jasmine.log* file. We avoid to use any logging library, only use the basic logging provided
by Java core. The report can be found in either a fictious uploading website or a local file 