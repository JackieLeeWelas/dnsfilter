package net.floodlightcontroller.dnsfilter;


import java.util.Set;

import org.projectfloodlight.openflow.types.DatapathId;
import org.restlet.resource.Get;

public class DNSFilterSwFilterSetResource extends DNSFilterResourceBase{
	//获取开启DNS过滤功能的switch列表。
	@Get("json")
	public Object handleRequest() {
		IDNSFilterService dnsfilter=this.getDNSFilterService(); 
		
		StringBuilder json = new StringBuilder();  
		Set<DatapathId> set=dnsfilter.getSwFilterSet();
		json.append("[");
		if(set!=null&&set.size()>0){
			for(DatapathId swid :set){
				json.append("{");
				json.append('\"');
				json.append("swid");
				json.append('\"');
				json.append(":");
				json.append('\"');
				json.append(swid.toString());
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
