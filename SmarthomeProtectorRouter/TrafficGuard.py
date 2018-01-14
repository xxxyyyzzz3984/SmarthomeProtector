import iptc

class TrafficGuard:
    def __init__(self, smartphoneIP, IoTIP):
        self.smartphone_ip = smartphoneIP
        self.iot_ip = IoTIP

        ## First block all incoming traffic to the iot
        self.filter_chain = iptc.Chain(iptc.Table(iptc.Table.FILTER), "INPUT")
        self.block_rule = iptc.Rule()
        self.block_rule.dst = self.iot_ip
        target = self.block_rule.create_target("DROP")
        self.block_rule.target = target
        self.filter_chain.insert_rule(self.block_rule)

# TrafficGuard = TrafficGuard("test", "164.106.130.6")
# filter_chain = iptc.Chain(iptc.Table(iptc.Table.FILTER), "INPUT")
# block_rule = iptc.Rule()
# block_rule.src = "206.212.0.79"
# target = block_rule.create_target("DROP")
# block_rule.target = target
# filter_chain.insert_rule(block_rule)