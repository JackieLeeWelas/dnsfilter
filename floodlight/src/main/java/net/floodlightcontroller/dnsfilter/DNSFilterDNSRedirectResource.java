package net.floodlightcontroller.dnsfilter;

import org.projectfloodlight.openflow.types.IPv4Address;
import org.restlet.resource.Get;
import org.restlet.resource.Put;

public class DNSFilterDNSRedirectResource extends DNSFilterResourceBase{
	@Get("json")
    public Object handleGet() {
		IDNSFilterService dnsfilter=this.getDNSFilterService();
		
		
		return "{\"dnsredirectip\" : \""+dnsfilter.getDnsRedirect().toString()+"\" }";	
	}
	@Put("json")
	public Object handlePut() {
    	IDNSFilterService dnsfilter=this.getDNSFilterService();
    	String param = (String) getRequestAttributes().get("ip");
    	IPv4Address serveraddr;
    	try{
    		serveraddr= IPv4Address.of(param);
    	} catch (Exception e) {
			return "{\"status\" : \"The format of IP is Error \"}";
		}
    	dnsfilter.setDnsRedirect(serveraddr);
    	return "{\"status\" : \"Success\", \"DNS redirect server ip\" : \""+serveraddr.toString()+"\"}";

    	
    }
	
	
}
