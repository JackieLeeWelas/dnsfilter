package net.floodlightcontroller.headerextract;

import java.util.*;
import java.util.Map.Entry;


import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.types.EthType;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.IpProtocol;
import org.projectfloodlight.openflow.types.MacAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.dnsfilter.DNSFilter;


import net.floodlightcontroller.packet.ARP;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.ICMP;
import net.floodlightcontroller.packet.IPv4;

public class HeaderExtract implements IOFMessageListener, IFloodlightModule {

//	public final int DEFAULT_CACHE_SIZE = 10;
	protected static Logger logger;
	protected IFloodlightProviderService floodlightProvider;
	private List<HashMap<String,String>> headlist;
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return Object.class.getSimpleName();
	}

	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		// TODO Auto-generated method stub
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IFloodlightProviderService.class);
		return l;
	}

	@Override
	public void init(FloodlightModuleContext context)
			throws FloodlightModuleException {
		// TODO Auto-generated method stub
		floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
		headlist = new ArrayList<HashMap<String,String>>();
		logger=LoggerFactory.getLogger(DNSFilter.class);

	}

	@Override
	public void startUp(FloodlightModuleContext context)
			throws FloodlightModuleException {
		// TODO Auto-generated method stub
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);

	}

	@Override
	public net.floodlightcontroller.core.IListener.Command receive(
			IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
		// TODO Auto-generated method stub
		
//		OFPacketIn pin = (OFPacketIn) msg;
		HashMap<String,String> hm = new HashMap<String,String>();
		switch(msg.getType()){
		case PACKET_IN:
			Ethernet eth = (Ethernet) IFloodlightProviderService.bcStore.get(cntx, IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
			MacAddress mac_src = eth.getSourceMACAddress();
			MacAddress mac_dst = eth.getDestinationMACAddress();
			hm.put("mac_dst", mac_dst.toString());
			hm.put("mac_src",mac_src.toString());
			
			if( eth.getEtherType() == EthType.IPv4){  //or eth.getPayload() instanceof IPv4
				IPv4 ip_pkt = (IPv4)eth.getPayload();
				IPv4Address ip_src = ip_pkt.getSourceAddress();
				IPv4Address ip_dst = ip_pkt.getDestinationAddress();
				hm.put("ip_dst", ip_dst.toString());
				hm.put("ip_src", ip_src.toString());
				
				if(ip_pkt.getProtocol() == IpProtocol.ICMP){ // or ip_pkt.getPayload() instanceof ICMP
					ICMP icmp_pkt = (ICMP)ip_pkt.getPayload();
					String icmp_str = icmp_pkt.toString();
					hm.put("icmp_data", icmp_str.toString());
					
				}
				else if(eth.getEtherType() == EthType.ARP){
					ARP arp_pkt = (ARP)eth.getPayload();
					MacAddress mac_sender = arp_pkt.getSenderHardwareAddress();
					MacAddress mac_target = arp_pkt.getTargetHardwareAddress();
					IPv4Address ip_sender = arp_pkt.getSenderProtocolAddress();
					IPv4Address ip_target = arp_pkt.getTargetProtocolAddress();
					
					hm.put("mac_sender", mac_sender.toString());
					hm.put("mac_target", mac_target.toString());
					hm.put("ip_sender", ip_sender.toString());
					hm.put("ip_target", ip_target.toString());
				}
				headlist.add(hm);
				
			}
			logger.info("packet_in message info: ");
	
			Set<Entry<String, String>> set = hm.entrySet();  
			for(Entry<String, String> e:set){
				logger.info(e.getKey().toString()+": "+e.getValue().toString());
			}
			break;
		default:
			break;
		}
		
		
//		Match match = new Match();
//		 Match.class.loadFromPacket(pin.getData(), pin.getInPort());
//		 System.out.println("-Get the Desitnation IP Address-");
//		 System.out.println(IPv4.fromIPv4Address(match.getNetworkDestination()));
		
	return Command.CONTINUE;
	}

}
