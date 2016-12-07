package net.floodlightcontroller.dnsfilter;



import java.util.ArrayList;

import org.restlet.resource.Get;

public class DNSFilterRecordResource extends DNSFilterResourceBase {
	//获取DNS过滤记录
	@Get("json")
	 public ArrayList<DNSFilterRecords> retrieve() {
		 IDNSFilterService dnsfilter=this.getDNSFilterService();
		
		 return dnsfilter.getrecords();
	 }
	
}
