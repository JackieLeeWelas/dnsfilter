package net.floodlightcontroller.dnsfilter;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import net.floodlightcontroller.restserver.RestletRoutable;

public class DNSFilterWebRoutable implements RestletRoutable {
	//定义DNSFilter的REST API
	@Override
	public Restlet getRestlet(Context context) {
		Router router=new Router(context);
		router.attach("/test",testResource.class);
		router.attach("/records",DNSFilterRecordResource.class);
		router.attach("/switch/{switch}/enable",DNSFilterSwitchEnableResource.class);
		router.attach("/switch/{switch}/disable",DNSFilterSwitchDisableResource.class);
		router.attach("/swfilterset",DNSFilterSwFilterSetResource.class);
		router.attach("/blackdomainname",DNSFilterBlackDomainNameResource.class);
		router.attach("/whitedomainname",DNSFilterWhiteDomainNameResource.class);
		router.attach("/dnsredirect/{ip}",DNSFilterDNSRedirectResource.class);
		router.attach("/dnsredirect",DNSFilterDNSRedirectResource.class);
		router.attach("/proxy",DNSFilterProxyResource.class);
		router.attach("/host/{host}/enable",DNSFilterHostEnableResource.class);
		router.attach("/host/{host}/disable",DNSFilterHostDisableResource.class);
		router.attach("/hostfilterset",DNSFilterHostFilterSetResource.class);
		router.attach("/hostmac/{host}/enable",DNSFilterHostMacEnableResource.class);
		router.attach("/hostmac/{host}/disable",DNSFilterHostMacDisableResource.class);
		router.attach("/hostmacfilterset",DNSFilterHostMacFilterSetResource.class);
		return router;
	}

	@Override
	public String basePath() {
		return "/wm/dnsfilter";
	}

}
