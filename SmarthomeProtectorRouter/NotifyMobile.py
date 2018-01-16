import requests

class Notifier:
    def __init__(self, ip, port):
        self.MobileIP = ip
        self.MobilePort = port


    def setData(self, alertIP, alertMAC, targetIP, targetMAC, dgOP):
        self.data = {
            'alertIP': alertIP,
            'alertMAC': alertMAC,
            'targetIP': targetIP,
            'targetMAC': targetMAC,
            'dgOP': dgOP
        }

    def sendData(self):
        r = requests.post('http://' + self.MobileIP + ":" + str(self.MobilePort), data=self.data)
        return r.text


# test = Notifier('128.164.68.232', 8888)
# test.setData('128.164.68.232', 'test', '192.168.1.145', 'test', 'testOP')
# test.sendData()