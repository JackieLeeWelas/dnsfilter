package net.floodlightcontroller.dnsfilter;


import org.restlet.resource.ServerResource;


public class DNSFilterResourceBase extends  ServerResource {
	IDNSFilterService getDNSFilterService(){
		
		return (IDNSFilterService)getContext().getAttributes().
		        get(IDNSFilterService.class.getCanonicalName());
	}
}
