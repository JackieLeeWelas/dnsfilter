# coding=gbk
from __future__ import division
import time
import socket
import random
import math
import os

def ReadHost(filename):
  hosts=[]
  blacklist = open(filename)
  for host in blacklist:
      hosts.append(host)
  file.close(blacklist)
  return hosts

def Ping(filename):
  hosts=ReadHost(filename)
  total=10
  sucess=0
  for i in range(total):
    host=hosts[i]
    print "--------------------Start Ping--------------------------"
    print "host %s to ping: "%(i+1),host
    info=os.popen( 'ping -c 1 %s'%host.strip('\r\n') ).read()
    print(info.strip('\n'))
    if "icmp_seq" in info:
      sucess += 1
      print("********************Ping Success**************************\n")
    else: print("********************Ping Failed**************************\n")
  print("******************success rate : %s / %s = %s%%************************"%(sucess,total,sucess/total*100))

def Ping2(filename):
  hosts=ReadHost(filename)
  total=10
  sucess=0
  for i in range(total):
    host=hosts[i]
    print "--------------------Start Ping--------------------------"
    print "host %s to ping: "%(i+1),host
    info=os.popen( 'h%s ping -c 1 %s'%((i%4+1),host.strip('\r\n') ).read()
    print(info.strip('\n'))
    if "icmp_seq" in info:
      sucess += 1
      print("********************Ping Success**************************\n")
    else: print("********************Ping Failed**************************\n")
  print("******************success rate : %s / %s = %s%%************************"%(sucess,total,sucess/total*100))

if __name__=='__main__':
  start=time.time()
  Ping('../floodlight-1.2/blacklist.txt')
  end = time.time()
  print 'starting at: ',start
  print 'ending at: ',end
  print 'time last: %s - %s = %s s'%(end,start,end-start)



