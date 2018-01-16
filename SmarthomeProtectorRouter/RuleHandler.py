import os

class RuleHandler:
    def __init__(self, IoTIP, Redirect_Localport):
        self.iot_ip = IoTIP
        self.redirect_port = Redirect_Localport

    def add_rule(self):
        os.system("iptables -t nat -A PREROUTING -p tcp"
                  " -d %s -j DNAT --to 127.0.0.1:%d" % (self.iot_ip, self.redirect_port))

    def delete_rule(self):
        os.system("iptables -t nat -D PREROUTING 1")

# TrafficGuard("10.42.0.247", 4444)

# TrafficGuard = TrafficGuard("test", "10.42.0.247")
# filter_chain = iptc.Chain(iptc.Table(iptc.Table.FILTER), "INPUT")
# block_rule = iptc.Rule()
# block_rule.src = "206.212.0.79"
# target = block_rule.create_target("DROP")
# block_rule.target = target
# filter_chain.insert_rule(block_rule)