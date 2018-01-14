import dpkt
import datetime
import socket
import ConfigParser
import pcap
from dpkt.compat import compat_ord
from NotifyMobile import Notifier

from TrafficGuard import TrafficGuard


def mac_addr(address):
    """Convert a MAC address to a readable/printable string

       Args:
           address (str): a MAC address in hex form (e.g. '\x01\x02\x03\x04\x05\x06')
       Returns:
           str: Printable/readable MAC address
    """
    return ':'.join('%02x' % compat_ord(b) for b in address)

def inet_to_str(inet):
    """Convert inet object to a string

        Args:
            inet (inet struct): inet network address
        Returns:
            str: Printable/readable IP address
    """
    # First try ipv4 and then ipv6
    try:
        return socket.inet_ntop(socket.AF_INET, inet)
    except ValueError:
        return socket.inet_ntop(socket.AF_INET6, inet)


def print_packets(pcap):
    """Print out information about each packet in a pcap

       Args:
           pcap: dpkt pcap reader object (dpkt.pcap.Reader)
    """
    # For each packet in the pcap process the contents
    for timestamp, buf in pcap:

        # Print out the timestamp in UTC
        print('Timestamp: ', str(datetime.datetime.utcfromtimestamp(timestamp)))

        # Unpack the Ethernet frame (mac src/dst, ethertype)
        eth = dpkt.ethernet.Ethernet(buf)
        print('Ethernet Frame: ', mac_addr(eth.src), mac_addr(eth.dst), eth.type)

        # Make sure the Ethernet data contains an IP packet
        if not isinstance(eth.data, dpkt.ip.IP):
            print('Non IP Packet type not supported %s\n' % eth.data.__class__.__name__)
            continue

        # Now unpack the data within the Ethernet frame (the IP packet)
        # Pulling out src, dst, length, fragment info, TTL, and Protocol
        ip = eth.data

        # Pull out fragment information (flags and offset all packed into off field, so use bitmasks)
        do_not_fragment = bool(ip.off & dpkt.ip.IP_DF)
        more_fragments = bool(ip.off & dpkt.ip.IP_MF)
        fragment_offset = ip.off & dpkt.ip.IP_OFFMASK

        # Print out the info
        print('IP: %s -> %s   (len=%d ttl=%d DF=%d MF=%d offset=%d)\n' % \
              (inet_to_str(ip.src), inet_to_str(ip.dst), ip.len, ip.ttl, do_not_fragment, more_fragments, fragment_offset))


class TrafficSniffer():
    def __init__(self, iot_config_filepath):
        self.TargetIoTConfig = ConfigParser.ConfigParser()
        self.TargetIoTConfig.read(iot_config_filepath)
        self.SmartphoneConfig = ConfigParser.ConfigParser()
        self.SmartphoneConfig.read('./smartiot_config/smartphone.config')
        self.TrafficType = self.__ConfigSectionMap(self.TargetIoTConfig, "STAT")["traffictype"]
        self.OpKeyWord = self.__ConfigSectionMap(self.TargetIoTConfig, "STAT")["opkeyword"]
        self.TargetIoTIP = self.__ConfigSectionMap(self.TargetIoTConfig, "STAT")["ipaddr"]
        self.TargetIoTMac = self.__ConfigSectionMap(self.TargetIoTConfig, "STAT")["mac"]
        self.OpDesctip = self.__ConfigSectionMap(self.TargetIoTConfig, "STAT")["desciption"]
        self.SmartphoneIP = self.__ConfigSectionMap(self.SmartphoneConfig, "STAT")["ipaddr"]
        self.SmartphoneNotifyPort = self.__ConfigSectionMap(self.SmartphoneConfig, "STAT")["notifyport"]
        self.AlertMAC = ""
        self.AlertIP = ""

        ## set up mobile notifier
        self.MobileNotifier = Notifier(self.SmartphoneIP, self.SmartphoneNotifyPort)

        ## set up traffic guard
        # TrafficGuardRunner = TrafficGuard(self.SmartphoneIP self.TargetIoTIP)

        ## live pcap instance
        self.LivePcap = pcap.pcap()

    def handleTraffic(self):
        for timestamp, buf in self.LivePcap:
            eth = dpkt.ethernet.Ethernet(buf)
            ip = eth.data

            # Make sure the Ethernet data contains an IP packet
            if not isinstance(eth.data, dpkt.ip.IP):
                continue

            self.AlertMAC = mac_addr(eth.src)
            self.AlertIP = inet_to_str(ip.src)
            captured_dst_ip = inet_to_str(ip.dst)

            try:
                if captured_dst_ip == self.TargetIoTIP:
                    ## a traffic matches a sensitive one, send notify
                    if self.OpKeyWord in ip.data.data:
                        self.MobileNotifier.\
                            setData(self.AlertIP, self.AlertMAC, self.TargetIoTIP, self.TargetIoTMac, self.OpDesctip)
                        self.MobileNotifier.sendData()

            except TypeError:
                continue

    def __ConfigSectionMap(self, Config, section):
        dict1 = {}
        options = Config.options(section)
        for option in options:
            try:
                dict1[option] = Config.get(section, option)
                if dict1[option] == -1:
                    print "skip: %s" % option
            except:
                print "exception on %s!" % option
                dict1[option] = None
        return dict1


TrafficSnifferRunner = TrafficSniffer("./smartiot_config/wemo_smartswitch1.config")
TrafficSnifferRunner.handleTraffic()