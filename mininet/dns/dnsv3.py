import SocketServer
import struct
import socket
#import httplib
import urllib2
import json
import os
#from scapy.all import ARP,send,Ether

Parameter = {}
Parameter["switchid"] = "00:00:00:00:00:00:00:04"
Parameter["proxy_mac"] = "00:00:00:00:00:01"
Parameter["gw_mac"] = "00:50:56:e5:65:e5"
Parameter["RestAPI"] = "http://10.0.0.252:8080/wm/dnsfilter/proxy"
Parameter["idletimeout"] = "30"
Parameter["hardtimeout"] = "0"
Parameter["monitor_port"] ="all"

class SinProxy:
    global Parameter 
    def __init__(self, name ,src_ip, proxy_ip ):
        try:
            self.sendcommand(src_ip,socket.gethostbyname(name),proxy_ip)#resolve the real ip and send relation of real ip and proxy ip to controller 
        except socket.error, err_msg:
            print "%s:%s" %(name,err_msg)
    def sendcommand(self,src_ip,dst_ip,proxy_ip):
        url=Parameter["RestAPI"]
        #response = urllib2.urlopen(url)

        
        values={}
        values["switch"] = Parameter["switchid"]
        values["src_ip"] = src_ip
        values["dst_ip"] = dst_ip
        values["proxy_ip"] = proxy_ip
        values["src_mac"] = self.getclientmac(src_ip)
        values["dst_mac"] = Parameter["gw_mac"]
        values["proxy_mac"] = Parameter["proxy_mac"]
        values["idletimeout"] = Parameter["idletimeout"]
        values["hardtimeout"] = Parameter["hardtimeout"]
        values["monitor_port"] = Parameter["monitor_port"]
        
        jdata = json.dumps(values)
        print jdata;
       
        req = urllib2.Request(url, jdata)
        try:
            response = urllib2.urlopen(req)
        except Exception, e:
            print "error: %s", e
        else:
            print response.read()
        
    def getclientmac(self, ip):
        mac=""
        try:
            mac = os.popen('arp '+ip).read().split()[8]
        except Exception:
           pass
        finally:
            if(mac.count(":") != 5):
                mac = "ff:ff:ff:ff:ff:ff"
            return mac

# DNS Query
class SinDNSQuery:
    def __init__(self, data):
        i = 1
        self.name = ''
        while True:
            d = data[i]
            #print "test",i,ord(d)
            if ord(d) == 0:
                break;
            if ord(d) < 32:
                self.name = self.name + '.'
            else:
                #self.name = self.name + chr(d)
                self.name = self.name + d
            i = i + 1
            #print "domain:",self.name
        self.querybytes = data[0:i + 1]
        (self.type, self.classify) = struct.unpack('>HH', data[i + 1:i + 5])
        self.len = i + 5
    def getbytes(self):
        return self.querybytes + struct.pack('>HH', self.type, self.classify)

# DNS Answer RRS
# this class is also can be use as Authority RRS or Additional RRS
class SinDNSAnswer:
    def __init__(self, ip):
        self.name = 49164
        self.type = 1
        self.classify = 1
        self.timetolive = 190
        self.datalength = 4
        self.ip = ip
    def getbytes(self):
        res = struct.pack('>HHHLH', self.name, self.type, self.classify, self.timetolive, self.datalength)
        s = self.ip.split('.')
        res = res + struct.pack('BBBB', int(s[0]), int(s[1]), int(s[2]), int(s[3]))
        return res

# DNS frame
# must initialized by a DNS query frame
class SinDNSFrame:
    def __init__(self, data):
        (self.id, self.flags, self.quests, self.answers, self.author, self.addition) = struct.unpack('>HHHHHH', data[0:12])
        self.query = SinDNSQuery(data[12:])
    def getname(self):
        return self.query.name
    def setip(self, ip):
        self.answer = SinDNSAnswer(ip)
        self.answers = 1
        self.flags = 33152
    def getbytes(self):
        res = struct.pack('>HHHHHH', self.id, self.flags, self.quests, self.answers, self.author, self.addition)
        res = res + self.query.getbytes()
        if self.answers != 0:
            res = res + self.answer.getbytes()
        return res
# A UDPHandler to handle DNS query
class SinDNSUDPHandler(SocketServer.BaseRequestHandler):
    n=0
    def handle(self):
        data = self.request[0].strip()
        dns = SinDNSFrame(data)
        sock = self.request[1]
        namemap = SinDNSServer.namemap
        if(dns.query.type==1):
            # If this is query a A record, then response it

            name = dns.getname();
            
            if namemap.__contains__(name):
                # If have record, response it
                dns.setip(namemap[name])
                sock.sendto(dns.getbytes(), self.client_address)
            elif namemap.__contains__('*'):
                # Response default address
                #print "n=%d" %SinDNSUDPHandler.n
                ip = namemap['*']+str(SinDNSUDPHandler.n)
                dns.setip(ip)
                SinProxy(name, self.client_address[0], ip)
                SinDNSUDPHandler.n=(SinDNSUDPHandler.n+1)%10
                sock.sendto(dns.getbytes(), self.client_address)
            else:
                # ignore it
                sock.sendto(data, self.client_address)
        else:
            # If this is not query a A record, ignore it
            sock.sendto(data, self.client_address)

# DNS Server
# It only support A record query
# user it, U can create a simple DNS server
class SinDNSServer:
    def __init__(self, port=53):
        SinDNSServer.namemap = {}
        self.port = port
    def addname(self, name, ip):
        SinDNSServer.namemap[name] = ip
    def start(self):
        HOST, PORT = "10.0.0.253", self.port
        print "start DNS server: %s:%s" %(HOST,PORT)
        server = SocketServer.UDPServer((HOST, PORT), SinDNSUDPHandler)
        server.serve_forever()

        
# Now, test it
if __name__ == "__main__":
    sev = SinDNSServer()
#    sev.addname('www.aa.com', '202.202.1.3')    # add a A record
#    sev.addname('www.bb.com', '192.168.0.2')    # add a A record
    sev.addname('*', '10.0.0.20') # default address prefix
    sev.start() # start DNS server
    
# Now, U can use "nslookup" command to test it
# Such as "nslookup www.aa.com"
