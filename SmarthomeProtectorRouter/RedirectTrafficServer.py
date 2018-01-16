import nclib
import threading
import copy

class RedirectServer():
    def __init__(self, iot_ip, iot_port, redirect_port):
        self.iot_ip = iot_ip
        self.iot_port = iot_port
        self.redirect_port = redirect_port
        self.isRedirect = False
        self.packets = []
        t1 = threading.Thread(target=self.__handle_redict)
        t1.start()

    def __handle_redict(self):
        self.local_nc = nclib.Netcat(listen=('localhost', self.redirect_port))
        self.iot_nc = nclib.Netcat((self.iot_ip, self.iot_port))
        t2 = threading.Thread(target=self.__send_packet)
        t2.start()

        while True:
            tmp_str = self.local_nc.recv()
            self.packets.append(copy.copy(tmp_str))
            while 'HTTP' in tmp_str:
                self.iot_nc.send(tmp_str)
                tmp_str = self.local_nc.recv()
                self.packets.append(copy.copy(tmp_str))

    def __send_packet(self):
        while True:
            if self.isRedirect:
                for packet_str in self.packets:
                    self.iot_nc.send(packet_str)
                    resp_str = self.iot_nc.recv()
                    self.local_nc.send(resp_str)
                self.packets = []