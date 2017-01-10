package net.floodlightcontroller.dnsfilter;

//测试用

import java.util.HashMap;
import java.util.Map;



import net.floodlightcontroller.staticflowentry.StaticFlowEntryPusher;
import net.floodlightcontroller.storage.IStorageSourceService;


import org.restlet.resource.Get;


public class testResource extends DNSFilterResourceBase{
	 @Get("json")
	 public Object handleRequest() {
		 IDNSFilterService dnsfilter=this.getDNSFilterService();
		 IStorageSourceService storageSource =
					(IStorageSourceService)getContext().getAttributes().
					get(IStorageSourceService.class.getCanonicalName());
		
		 
	
		 Map<String, Object> rowValues= new HashMap<String, Object>();
		 rowValues.put("switch", "00:00:00:00:00:00:00:01");
		 rowValues.put("name","flow-1");
		 rowValues.put("eth_type","0x0800");
		 rowValues.put("ip_proto","0x11");
		 rowValues.put("tp_dst","53");
		 rowValues.put("active","true");
		 rowValues.put("actions","output=controller");
		 
		 
		 storageSource.insertRowAsync(StaticFlowEntryPusher.TABLE_NAME, rowValues);
		 
		 return "{\"result\" :\""+dnsfilter.test()+"\"}";	
		 
	 }
	 


}
