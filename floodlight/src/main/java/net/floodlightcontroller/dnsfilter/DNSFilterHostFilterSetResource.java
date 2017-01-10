package net.floodlightcontroller.dnsfilter;

import java.util.Set;


import org.projectfloodlight.openflow.types.IPv4Address;
import org.restlet.resource.Get;

public class DNSFilterHostFilterSetResource extends DNSFilterResourceBase{
	@Get("json")
	public Object handleRequest() {
		IDNSFilterService dnsfilter=this.getDNSFilterService(); 
		
		StringBuilder json = new StringBuilder();  
		Set<IPv4Address> set=dnsfilter.getHostIPFilterSet();
		json.append("[");
		if(set!=null&&set.size()>0){
			for(IPv4Address ip :set){
				json.append("{");
				json.append('\"');
				json.append("host_ip");
				json.append('\"');
				json.append(":");
				json.append('\"');
				json.append(ip.toString());
				json.append('\"');
				json.append("}");
				json.append(",");
			}
			json.deleteCharAt(json.length() - 1);
		}		
		json.append("]");
	
		return json.toString();
               
    }
}
