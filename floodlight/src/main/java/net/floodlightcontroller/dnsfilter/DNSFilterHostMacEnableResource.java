package net.floodlightcontroller.dnsfilter;

import org.projectfloodlight.openflow.types.MacAddress;
import org.restlet.resource.Get;
import org.restlet.resource.Put;

public class DNSFilterHostMacEnableResource extends DNSFilterResourceBase {

	@Get("json")
    public Object handleRequest() {
         
        return "{\"status\" : \"failure\", \"details\" : \"Use PUT to add Host\"}";
    }
	@Put("json")
	public Object handlePut() {
		IDNSFilterService dnsfilter = this.getDNSFilterService();
		String param = (String) getRequestAttributes().get("host");
		MacAddress host;
		
		try {
			
			host=MacAddress.of(param);
		
		} catch (Exception e) {
			return "{\"status\" : \"The format of MAC is Error \"}";
		}
		dnsfilter.addHostMACFilterSet(host);
		return "{\"status\" : \"success\", \"details\" : \""+host.toString()+" has added in FilterSet \"}";
	    	
	}
}
