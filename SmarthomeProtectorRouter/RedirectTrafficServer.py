import os

import nclib
import threading
import copy

import subprocess


def GetOutput(command):
    lines = os.popen(command).readlines()
    result = ""
    if len(lines) > 0:
        for line in lines:
            result += line
    return result



class RedirectServer():
    def __init__(self, iot_ip, iot_port, redirect_port):
        self.iot_ip = iot_ip
        self.iot_port = iot_port
        self.redirect_port = redirect_port
        self.isRedirect = False
        self.packets = ''


    def start_redirect(self):
        t1 = threading.Thread(target=self.__handle_redict)
        t1.start()
        self.iot_nc = nclib.Netcat((self.iot_ip, self.iot_port))
        t2 = threading.Thread(target=self.__send_packet)
        t2.start()

    def __handle_redict(self):
        self.packets = GetOutput('nc -l -p 4444')

        # print lastline
        # os.system('nc -l -p 4444')
        # print 'here here'
        # self.local_nc = nclib.Netcat(listen=('127.0.0.1', 4444))
        # print 'here'
        # self.iot_nc = nclib.Netcat((self.iot_ip, self.iot_port))
        # t2 = threading.Thread(target=self.__send_packet)
        # t2.start()

        # while True:
        #
        #     tmp_str = self.local_nc.recv()
        #     print tmp_str
        #     self.packets.append(copy.copy(tmp_str))
        #     while 'HTTP' in tmp_str:
        #         # self.iot_nc.send(tmp_str)
        #         tmp_str = self.local_nc.recv()
        #         print tmp_str
        #         self.packets.append(copy.copy(tmp_str))

    def setDecision(self, decision):
        self.isRedirect = decision

    def __send_packet(self):
        print 'send packet'
        while True:
            if self.packets != '' and self.isRedirect:
                self.iot_nc.send(self.packets)
                self.packets = ''
                self.isRedirect = False