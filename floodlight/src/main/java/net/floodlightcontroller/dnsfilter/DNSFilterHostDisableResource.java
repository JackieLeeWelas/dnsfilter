package net.floodlightcontroller.dnsfilter;

import org.projectfloodlight.openflow.types.IPv4Address;
import org.restlet.resource.Get;
import org.restlet.resource.Put;

public class DNSFilterHostDisableResource extends DNSFilterResourceBase{
	@Get("json")
    public Object handleRequest() {
         
        return "{\"status\" : \"failure\", \"details\" : \"Use PUT to remove Host\"}";
    }
	@Put("json")
	public Object handlePut() {
		IDNSFilterService dnsfilter = this.getDNSFilterService();
		String param = (String) getRequestAttributes().get("host");
		IPv4Address host;
		
		try {
			
			host=IPv4Address.of(param);
		
		} catch (Exception e) {
			return "{\"status\" : \"The format of IP is Error \"}";
		}
		dnsfilter.removeHostIPFilterSet(host);
		return "{\"status\" : \"success\", \"details\" : \""+host.toString()+" has removed from FilterSet \"}";
	    	
	}
}
