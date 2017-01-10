package net.floodlightcontroller.dnsfilter;

import java.util.Set;

import org.projectfloodlight.openflow.types.MacAddress;
import org.restlet.resource.Get;

public class DNSFilterHostMacFilterSetResource extends DNSFilterResourceBase {

	@Get("json")
	public Object handleRequest() {
		IDNSFilterService dnsfilter=this.getDNSFilterService(); 
		
		StringBuilder json = new StringBuilder();  
		Set<MacAddress> set=dnsfilter.getHostMACFilterSet();
		json.append("[");
		if(set!=null&&set.size()>0){
			for(MacAddress ip :set){
				json.append("{");
				json.append('\"');
				json.append("host_mac");
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
