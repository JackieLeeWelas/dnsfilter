package net.floodlightcontroller.dnsfilter;

import java.util.ArrayList;
import java.util.Set;

import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.MacAddress;
import org.projectfloodlight.openflow.types.OFPort;

import net.floodlightcontroller.core.module.IFloodlightService;
//DNSFilter服务接口
public interface IDNSFilterService extends IFloodlightService {
	public String test();
	public ArrayList<DNSFilterRecords> getrecords();
	public void addSwFilterSet(DatapathId swid);
	public void removeSwFilterSet(DatapathId swid);
	public void clearSwFilterSet();
	public Set<DatapathId> getSwFilterSet();
	public boolean insertBlackDomain(String name);	
	public boolean removeBlackDomain(String name);
	public boolean hasBlackDomain(String name);
	public boolean insertWhiteDomain(String name);	
	public boolean removeWhiteDomain(String name);
	public boolean hasWhiteDomain(String name);
	public void setDnsRedirect(IPv4Address ip);
	public IPv4Address getDnsRedirect();
	public void setProxy(DatapathId switchid, IPv4Address src_ip, IPv4Address dst_ip, IPv4Address proxy_ip,
			MacAddress src_mac, MacAddress dst_mac, MacAddress proxy_mac,int hardtimeout,int idletimeout,OFPort monitor_port);
	public void addHostIPFilterSet(IPv4Address host);
	public void removeHostIPFilterSet(IPv4Address host);
	public Set<IPv4Address> getHostIPFilterSet();
	public void addHostMACFilterSet(MacAddress host);
	public void removeHostMACFilterSet(MacAddress host);
	public Set<MacAddress> getHostMACFilterSet();
}
