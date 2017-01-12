package net.floodlightcontroller.dnsfilter;



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import java.util.Map;




import org.projectfloodlight.openflow.protocol.OFFlowMod;
import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFPacketIn;
import org.projectfloodlight.openflow.protocol.OFPacketOut;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.protocol.OFVersion;
import org.projectfloodlight.openflow.protocol.action.OFAction;
import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.protocol.match.MatchField;




import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.EthType;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.IpProtocol;
import org.projectfloodlight.openflow.types.MacAddress;
import org.projectfloodlight.openflow.types.OFBufferId;
import org.projectfloodlight.openflow.types.OFPort;
import org.projectfloodlight.openflow.types.TransportPort;
import org.projectfloodlight.openflow.types.U64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.floodlightcontroller.classifier.Classifier;
import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;

import net.floodlightcontroller.core.internal.IOFSwitchService;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.core.util.AppCookie;
import net.floodlightcontroller.devicemanager.IDeviceService;


import net.floodlightcontroller.packet.DNS;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPv4;

import net.floodlightcontroller.packet.UDP;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.routing.IRoutingDecision;
import net.floodlightcontroller.routing.RoutingDecision;


import net.floodlightcontroller.util.OFMessageDamper;



public class DNSFilter implements IFloodlightModule, IOFMessageListener,IDNSFilterService {
	protected IFloodlightProviderService floodlightProvider;
	protected static Logger logger;
	protected Trie blacklist;
	protected Trie whitelist;
	protected IRestApiService restApi;
	protected ArrayList<DNSFilterRecords> recordsTop100;
	protected Set<DatapathId> SwFilterSet;
	protected Set<IPv4Address> HostIPFilterSet;
	protected Set<MacAddress> HostMACFilterSet;

	protected OFMessageDamper messageDamper;
	protected IPv4Address DNSRedirectIp;
	protected IOFSwitchService switchService;
	
	protected Classifier classifier;
	@Override
	public String getName() {
		return "dnsfilter";
	}

	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}
	//DNSFilter模块加载顺序，必须先与forwarding模块
	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		
		return (type.equals(OFType.PACKET_IN) && name.equals("forwarding"));
	}
	
	//处理Packet_in包
	@Override
	public net.floodlightcontroller.core.IListener.Command receive(
			IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {		
		//检查该Switch是否开启DNS过滤。处理开启DNS过滤的switch发来的请求
		//检查该源Host是否需要检查。处理开启DNS过滤的Host发来的请求
    	if(!this.SwFilterSet.contains(sw.getId())&&!this.isFilterHostIP(cntx) && !this.isFilterHostMAC(cntx))
    		return Command.CONTINUE;			
        switch (msg.getType()) {
        case PACKET_IN:
        	IRoutingDecision decision = null;
            if (cntx != null) { 
            	decision = IRoutingDecision.rtStore.get(cntx, IRoutingDecision.CONTEXT_DECISION);
                return this.processPacketInMessage(sw, (OFPacketIn) msg, decision, cntx);
            }
            break;
        default:
            break;
        }
        return Command.CONTINUE;		
	}
	// 检查Packet_in的携带的数据包的源IP地址是否需要检查
	protected boolean isFilterHostIP(FloodlightContext cntx){
			
		if(cntx!=null){
			Ethernet eth=IFloodlightProviderService.bcStore.get(cntx, IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
			if(eth.getPayload() instanceof IPv4){
				IPv4 ip_pkt = (IPv4) eth.getPayload();
	            IPv4Address src = ip_pkt.getSourceAddress();	            
	           
	            if(this.HostIPFilterSet.contains(src))
	            	return true;
	           
			}
		}
		return false;
		
	}
	// 检查Packet_in的携带的数据包的源地址是否需要检查
		protected boolean isFilterHostMAC(FloodlightContext cntx){
				
			if(cntx!=null){
				Ethernet eth=IFloodlightProviderService.bcStore.get(cntx, IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
				MacAddress mac = eth.getSourceMACAddress();
		        if(this.HostMACFilterSet.contains(mac)){
		            return true;
		           
				}
			}
			return false;
			
		}
	
	public void processMaliciousDomain(IOFSwitch sw,OFPacketIn pi,FloodlightContext cntx,
			IRoutingDecision decision,IPv4Address src,IPv4Address dst,String domainName,
			TransportPort src_p,TransportPort dst_p){
		this.record(sw,src,dst,domainName);
		OFPort inPort = (pi.getVersion().compareTo(OFVersion.OF_12) < 0 ? pi.getInPort() : pi.getMatch().get(MatchField.IN_PORT));
		if(this.DNSRedirectIp==IPv4Address.of("0.0.0.0")){            				
			decision = new RoutingDecision(sw.getId(), inPort,
					IDeviceService.fcStore.get(cntx, IDeviceService.CONTEXT_SRC_DEVICE),
					IRoutingDecision.RoutingAction.DROP);
		}else{            				
			this.redirect(sw, pi, src,dst,src_p,dst_p);               			
			decision = new RoutingDecision(sw.getId(), inPort,
					IDeviceService.fcStore.get(cntx, IDeviceService.CONTEXT_SRC_DEVICE),
					IRoutingDecision.RoutingAction.NONE);            		
		}
		
		decision.addToContext(cntx);
	}
	public Command processPacketInMessage(IOFSwitch sw, OFPacketIn pi,IRoutingDecision decision,
			 FloodlightContext cntx)
	{
		Ethernet eth=IFloodlightProviderService.bcStore.get(cntx, IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
		//OFPort inPort = (pi.getVersion().compareTo(OFVersion.OF_12) < 0 ? pi.getInPort() : pi.getMatch().get(MatchField.IN_PORT));
		if(eth.getPayload() instanceof IPv4)
		{
			IPv4 ip_pkt = (IPv4) eth.getPayload();
            IPv4Address src = ip_pkt.getSourceAddress();
            IPv4Address dst = ip_pkt.getDestinationAddress();
			 if(ip_pkt.getPayload() instanceof UDP){
				UDP udp_pkt=(UDP)ip_pkt.getPayload();
	            TransportPort src_p=udp_pkt.getSourcePort();
	            TransportPort dst_p=udp_pkt.getDestinationPort();
	            
	            //检查该数据包是否是DNS请求包
	            if(dst_p.equals(TransportPort.of(53)))
	            {
	      
	            	DNS dns_pkt=(DNS)udp_pkt.getPayload();
	            	String query_name = dns_pkt.toString();
	            	if(query_name.contains("in-addr.arpa"))
	            		return Command.CONTINUE;
	            	String src2dst=" from "+src.toString()+":"+src_p.toString()+
	            			" to "+dst.toString()+":"+dst_p.toString()+
	            			" seen on switch: "+sw.getId();           			            	
            		logger.info("DNS packet  {},queryname:{} ",src2dst,query_name);
            		logger.info("blacklist has:{}",blacklist.has(query_name));
            		logger.info("whitelist has:{}",whitelist.has(query_name));
            		
            		// 不检查去往DNS 重定向服务的数据包
            		if(dst.equals(this.DNSRedirectIp)||src.equals(this.DNSRedirectIp))
            			return Command.CONTINUE;
            		
            		//检查该DNS请求的域名是否在黑名单中，
            		if(blacklist.has(query_name)){
            			//处理恶意域名
            			processMaliciousDomain(sw,pi,cntx,decision,src,dst,dns_pkt.toString(),src_p,dst_p);
            		}
            		else if(whitelist.has(query_name)){//域名在白名单中
            			return Command.CONTINUE;
            		}else{   //不在黑白名单的域名，根据分类器分类结果处理
            			double result = classifier.predict(query_name);
            			if( result > 0) { //malicious domain name
            				processMaliciousDomain(sw,pi,cntx,decision,src,dst,dns_pkt.toString(),src_p,dst_p);
            			}else{
            				return Command.CONTINUE;
            			}
            		}
	            } 
			 }
		}			
		return Command.CONTINUE;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		Collection<Class<? extends IFloodlightService>>l=
				new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IDNSFilterService.class);
		return l;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		Map<Class<? extends IFloodlightService>, IFloodlightService>m=
				new HashMap<Class<? extends IFloodlightService>, IFloodlightService>();
		m.put(IDNSFilterService.class, this);
		return m;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		Collection<Class<? extends IFloodlightService>>l=
				new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IFloodlightProviderService.class);
		l.add(IRestApiService.class);
		return l;
	}
	
	//从黑白域名名单文件中读取域名，建立字典树
	protected void readlist()
	{
		File file = new File("./data/blacklist.txt");  
		File file2 = new File("./data/whitelist.txt"); 
 		BufferedReader reader = null;  
		try {  			
			reader = new BufferedReader(new FileReader(file)); 			
			String tempString = null;  
			//一次读入一行，直到读入null为文件结束  
			while ((tempString = reader.readLine()) != null){  
				blacklist.insert(tempString);
			}  
			reader.close();
			reader = new BufferedReader(new FileReader(file2)); 
			while ((tempString = reader.readLine()) != null){  
				whitelist.insert(tempString);
			} 
			reader.close();  
			
		} catch (IOException e) {  
			e.printStackTrace();  
		} finally {  
			if (reader != null){  
				try {  
					reader.close();  
				} catch (IOException e1) {  
				}  
			}  
			}
	}
	//记录解析恶意域名的DNS数据包
	protected void record(IOFSwitch sw,IPv4Address src,IPv4Address dst,String query_name){
		File file =new File("./recordlist.txt");
		BufferedWriter writer =null;
		try {
			DNSFilterRecords tmprecord=new DNSFilterRecords();
					
			writer = new BufferedWriter(new FileWriter(file,true));  
			Date now = new Date();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");		

			String out=df.format(now)+" "+src.toString()+" "+dst.toString()+" "+sw.getId()+" "+query_name+"\n";
			
			tmprecord.host=src.toString();
			tmprecord.dnsserver=dst.toString();
			tmprecord.time=df.format(now);
			tmprecord.swid=sw.getId().toString();
			tmprecord.queryname=query_name;
			//记录最近的100条
			if(recordsTop100.size()>=100)
			{
				recordsTop100.remove(0);
			}
			recordsTop100.add(tmprecord);
			//写入文件。	
			writer.write(out);
			writer.close();
		} catch (IOException e) {  
			e.printStackTrace();  
		} finally {  
			if (writer != null){  
				try {  
					writer.close();  
				} catch (IOException e1) {  
				}  
			}  
			}
	}
	//模块初始化
	@Override
	public void init(FloodlightModuleContext context)
			throws FloodlightModuleException {
		floodlightProvider=context.getServiceImpl(IFloodlightProviderService.class);
		logger=LoggerFactory.getLogger(DNSFilter.class);
		blacklist = new Trie();
		whitelist = new Trie();
		this.readlist();
		restApi=context.getServiceImpl(IRestApiService.class);
		recordsTop100=new ArrayList<DNSFilterRecords>();
		SwFilterSet= new HashSet<DatapathId>();
		HostIPFilterSet=new HashSet<IPv4Address>();
		HostMACFilterSet=new HashSet<MacAddress>();
		messageDamper = new OFMessageDamper(1000,EnumSet.of(OFType.FLOW_MOD),250);
		DNSRedirectIp= IPv4Address.of("0.0.0.0");
		switchService = context.getServiceImpl(IOFSwitchService.class);
		classifier = new Classifier();
		
	}

	@Override
	public void startUp(FloodlightModuleContext context)
			throws FloodlightModuleException {
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
		restApi.addRestletRoutable(new DNSFilterWebRoutable());
		
	}

	@Override
	public String test() {
	
		return "test";
	}
	//获取被DNSfilter模块过滤的数据包
	@Override
	public ArrayList<DNSFilterRecords> getrecords() {
	
		return this.recordsTop100;
	}
	//Switch开启DNS过滤功能
	@Override
	public void addSwFilterSet(DatapathId swid) {
		
		this.SwFilterSet.add(swid);

	}
	//Switch关闭DNS过滤功能
	@Override
	public void removeSwFilterSet(DatapathId swid) {
		
		this.SwFilterSet.remove(swid);
		
		
	}
	//关闭所有Switch的DNS过滤功能
	@Override
	public void clearSwFilterSet() {
		this.SwFilterSet.clear();
		
	}
	//获取开启DNS过滤功能的switch的集合
	@Override
	public Set<DatapathId> getSwFilterSet() {
		
		return this.SwFilterSet;
	}
	//增加域名黑名单
	@Override
	public boolean insertBlackDomain(String name) {
		
		if(this.cheackDomainName(name))
		{
			this.blacklist.insert(name);
			return true;
		}
		else
			return false;
	}
	//删除黑名单
	@Override
	public boolean removeBlackDomain(String name) {
		
		if(this.cheackDomainName(name))
			return this.blacklist.remove(name);
		else
			return false;
	}
	//判断该域名是否为恶意域名
	@Override
	public boolean hasBlackDomain(String name) {
		
		if(this.cheackDomainName(name))
			return this.blacklist.has(name);
		else
			return false;
	}
	
	//增加域名白名单
		@Override
		public boolean insertWhiteDomain(String name) {
			
			if(this.cheackDomainName(name))
			{
				this.whitelist.insert(name);
				return true;
			}
			else
				return false;
		}
		//删除白名单
		@Override
		public boolean removeWhiteDomain(String name) {
			
			if(this.cheackDomainName(name))
				return this.whitelist.remove(name);
			else
				return false;
		}
		//判断该域名是否为良性域名
		@Override
		public boolean hasWhiteDomain(String name) {
			
			if(this.cheackDomainName(name))
				return this.whitelist.has(name);
			else
				return false;
		}
	//检查域名的正确性
	public boolean cheackDomainName(String name){
		
    	if (name == null || name.length() == 0 ) {  
            return false;  
        } 	
		char[] letters=name.toLowerCase().toCharArray();
		for(int i=0;i<name.length();i++)
		{
			if(!(Character.isDigit(letters[i])||Character.isLowerCase(letters[i])
					||letters[i]=='.'||letters[i]=='-'))
			{
				return false;
			}
				
		}
		return true;
	}
	
	protected void redirect(IOFSwitch sw, OFPacketIn pi, IPv4Address src, IPv4Address dst,
			TransportPort src_p,TransportPort dst_p){
		
		List<OFAction> actionsRequest = new ArrayList<OFAction>();
		List<OFAction> actionsReply = new ArrayList<OFAction>();
		
		actionsRequest.add(sw.getOFFactory().actions().setNwDst(this.DNSRedirectIp));	
		actionsRequest.add(sw.getOFFactory().actions().output(OFPort.FLOOD, Integer.MAX_VALUE));
		//重定向DNS请求报文，将该报文的目的修改为指定DNS重定向服务器。
	
		this.AddFlow2DNS(sw, src, dst, src_p, dst_p, actionsRequest);
		
		actionsReply.add(sw.getOFFactory().actions().setNwSrc(dst));	
		actionsReply.add(sw.getOFFactory().actions().output(OFPort.FLOOD, Integer.MAX_VALUE));
		//修改DNS响应包的源地址。
		this.AddFlow2DNS(sw, this.DNSRedirectIp, src, dst_p, src_p, actionsReply);
		
		//将当前报文发送回交换机
		List<OFAction> actions = new ArrayList<OFAction>();
		actions.add(sw.getOFFactory().actions().output(OFPort.TABLE, Integer.MAX_VALUE));
		this.SendPacketBackSwitch(sw, pi, actions);
		//this.SendPacketBackSwitch(sw, pi, actionsRequest);
	}
	//用于增加流表项 用于匹配DNS报文，并修改源目IP
	protected void AddFlow2DNS(IOFSwitch sw,IPv4Address src, IPv4Address dst,
			TransportPort src_p,TransportPort dst_p,List<OFAction> actions){
		
		Match.Builder mb = sw.getOFFactory().buildMatch();	
	
		OFFlowMod.Builder fmb = sw.getOFFactory().buildFlowAdd();	
		U64 cookie = AppCookie.makeCookie(2, 0);
		
		mb.setExact(MatchField.IPV4_SRC,src)
		.setExact(MatchField.IPV4_DST, dst)
		.setExact(MatchField.ETH_TYPE, EthType.IPv4);
		
		mb.setExact(MatchField.UDP_SRC,src_p)
		.setExact(MatchField.UDP_DST, dst_p)
		.setExact(MatchField.IP_PROTO, IpProtocol.UDP);
								
		fmb.setCookie(cookie)
		.setHardTimeout(0)
		.setIdleTimeout(5)
		.setBufferId(OFBufferId.NO_BUFFER)
		.setMatch(mb.build())
		.setActions(actions)
		.setPriority(1000);
		
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("write DNS redirect flow-mod sw={} match={} flow-mod={}",
						new Object[] { sw, mb.build(), fmb.build() });
			}
			boolean dampened = messageDamper.write(sw, fmb.build());

			logger.debug("OFMessage dampened: {}", dampened);
		} catch (IOException e) {
			logger.error("Failure writing DNS redirect flow mod", e);
		}
		
	}
	//将Packet_in的负载报文发送回交换机。
	void SendPacketBackSwitch(IOFSwitch sw,OFPacketIn pi,List<OFAction> actions){
		
		OFPacketOut.Builder pob = sw.getOFFactory().buildPacketOut();
		OFPort inPort = (pi.getVersion().compareTo(OFVersion.OF_12) < 0 ? pi.getInPort() : pi.getMatch().get(MatchField.IN_PORT));
		pob.setActions(actions);
		pob.setBufferId(OFBufferId.NO_BUFFER);
		pob.setInPort(inPort);
		pob.setData(pi.getData());

		
		try {
			messageDamper.write(sw, pob.build());
			
		} catch (IOException e) {
			logger.error("Failure sending the packet back to switch", e);
		}
		
	}
	
	//设置DNS重定向服务器的IP
	@Override
	public void setDnsRedirect(IPv4Address ip) {
		this.DNSRedirectIp=ip;
	}
	//获取DNS重定向服务器的IP
	@Override
	public IPv4Address getDnsRedirect() {
		return this.DNSRedirectIp;
	}
	//设置流表用于代理，修改流的源目IP、MAC
	@Override
	public void setProxy(DatapathId switchid, IPv4Address src_ip,
			IPv4Address dst_ip, IPv4Address proxy_ip, MacAddress src_mac,
			MacAddress dst_mac, MacAddress proxy_mac,int hardtimeout,int idletimeout,OFPort monitor_port) {
		

		IOFSwitch sw = switchService.getSwitch(switchid);
		

		this.addProxyFlow(sw, this.setProxyMatch(sw, src_ip, proxy_ip), 
				this.setProxyAction(sw, proxy_ip, dst_ip, proxy_mac, dst_mac, monitor_port),hardtimeout, idletimeout);	
		
		this.addProxyFlow(sw, this.setProxyMatch(sw, dst_ip, proxy_ip), 
				this.setProxyAction(sw, proxy_ip, src_ip, proxy_mac, src_mac, monitor_port),hardtimeout, idletimeout);	
		
		
	}
	
	protected Match setProxyMatch(IOFSwitch sw, IPv4Address src, IPv4Address dst){
		
		Match.Builder mb = sw.getOFFactory().buildMatch();	
			
		mb.setExact(MatchField.IPV4_SRC,src)
		.setExact(MatchField.IPV4_DST, dst)
		.setExact(MatchField.ETH_TYPE, EthType.IPv4);
		
		return mb.build();
		
	}
	
	protected List<OFAction> setProxyAction(IOFSwitch sw, IPv4Address src_ip,
			IPv4Address dst_ip, MacAddress src_mac, MacAddress dst_mac,OFPort monitor_port){

		List<OFAction> actions = new ArrayList<OFAction>();
		
		actions.add(sw.getOFFactory().actions().setNwSrc(src_ip));
		actions.add(sw.getOFFactory().actions().setNwDst(dst_ip));
		actions.add(sw.getOFFactory().actions().setDlSrc(src_mac));
		actions.add(sw.getOFFactory().actions().setDlDst(dst_mac));
		actions.add(sw.getOFFactory().actions().output(monitor_port, Integer.MAX_VALUE));
		actions.add(sw.getOFFactory().actions().output(OFPort.IN_PORT, Integer.MAX_VALUE));
		return actions;
	}
	
	protected void addProxyFlow(IOFSwitch sw, Match match, List<OFAction> actions,
			int hardtimeout,int idletimeout){
		
		OFFlowMod.Builder fmb = sw.getOFFactory().buildFlowAdd();	
		U64 cookie = AppCookie.makeCookie(2, 0);
											
		fmb.setCookie(cookie)
		.setHardTimeout(hardtimeout)
		.setIdleTimeout(idletimeout)
		.setBufferId(OFBufferId.NO_BUFFER)
		.setMatch(match)
		.setActions(actions)
		.setPriority(1000);
		
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("write proxy  flow-mod sw={} match={} flow-mod={}",
						new Object[] { sw, match, fmb.build() });
			}
			boolean dampened = messageDamper.write(sw, fmb.build());

			logger.debug("OFMessage dampened: {}", dampened);
		} catch (IOException e) {
			logger.error("Failure writing Proxy flow mod", e);
		}
		
	}

	@Override
	public void addHostIPFilterSet(IPv4Address host) {
		
		this.HostIPFilterSet.add(host);
		
	}

	@Override
	public void removeHostIPFilterSet(IPv4Address host) {
		
		this.HostIPFilterSet.remove(host);
		
	}


	@Override
	public Set<IPv4Address> getHostIPFilterSet() {
		
		return this.HostIPFilterSet;
	}

	@Override
	public void addHostMACFilterSet(MacAddress host) {
		// TODO Auto-generated method stub
		this.HostMACFilterSet.add(host);
	}

	@Override
	public void removeHostMACFilterSet(MacAddress host) {
		// TODO Auto-generated method stub
		this.HostMACFilterSet.remove(host);
	}

	@Override
	public Set<MacAddress> getHostMACFilterSet() {
		// TODO Auto-generated method stub
		return this.HostMACFilterSet;
	}
	
				
	
}
