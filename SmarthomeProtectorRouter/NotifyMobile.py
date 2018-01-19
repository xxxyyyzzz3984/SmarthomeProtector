import requests

class Notifier:
    def __init__(self, ip, port):
        self.MobileIP = ip
        self.MobilePort = port


    def setData(self, alertIP, alertMAC, targetIP, targetMAC, dgOP, pressible):
        self.data = {
            'alertIP': alertIP,
            'alertMAC': alertMAC,
            'targetIP': targetIP,
            'targetMAC': targetMAC,
            'dgOP': dgOP,
            'pressible': pressible
        }

    def sendData(self):
        try:
            r = requests.post('http://' + self.MobileIP + ":" + str(self.MobilePort), data=self.data)
            return r.text
        except:
            return 'Deny'



# test = Notifier('128.164.68.232', 8888)
# test.setData('128.164.68.232', 'test', '192.168.1.145', 'test', 'testOP')
# test.sendData()