package net.floodlightcontroller.dnsfilter;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.floodlightcontroller.core.internal.IOFSwitchService;
import net.floodlightcontroller.staticflowentry.StaticFlowEntryPusher;
import net.floodlightcontroller.storage.IStorageSourceService;

import org.projectfloodlight.openflow.types.DatapathId;
import org.restlet.resource.Get;
import org.restlet.resource.Put;

public class DNSFilterSwitchEnableResource extends DNSFilterResourceBase {
	@Get("json")
    public Object handleRequest() {
         
        return "{\"status\" : \"failure\", \"details\" : \"Use PUT to enable dnsfilter\"}";
    }
	//开启switch的DNS过滤功能，并添加相应的静态流表，使该switch总是把DNS请求数据包发送给控制器
    @Put("json")
    public Object handlePut() {
    	IDNSFilterService dnsfilter=this.getDNSFilterService();
		IOFSwitchService switchService = (IOFSwitchService) getContext().getAttributes().
				get(IOFSwitchService.class.getCanonicalName());
   		Set<DatapathId> switchDpids = switchService.getAllSwitchDpids();    	
   		
   		String param = (String) getRequestAttributes().get("switch");
    	
    	if(param.toLowerCase().equals("all"))
    	{
    		//所有switch都开启DNS过滤功能，并添加相应静态流表
    		for (DatapathId swid : switchDpids) {  
    				dnsfilter.addSwFilterSet(swid); 
    				this.addstaicflow(swid.toString());
    			}    
    		return "{\"status\" : \"Success\"}";
    	}else{

   		 try {
   			 	//检查该DPID是否存在。存在则开启DNS过滤功能，并添加相应静态流表
   		 		if(switchDpids.contains(DatapathId.of(param))){
   		 			dnsfilter.addSwFilterSet(DatapathId.of(param));
   		 			this.addstaicflow(param);
   		 			return "{\"status\" : \"Success\"}";
   		 		}else{
   		 			return "{\"status\" : \"failure\", \"details\" : \"the SwitchId is not exist\"}";
   		 		}
   		 		
   		 	}catch (NumberFormatException e){
   		 		return "{\"status\" : \"failure\", \"details\" : \"the SwitchId is error\"}";
   		 }
		 
    	}
    	
    }
    
    //添加静态流表，使该switch总是包DNS请求数据包发送给控制器
    public void addstaicflow(String swid){
    	
    	IStorageSourceService storageSource =
					(IStorageSourceService)getContext().getAttributes().
					get(IStorageSourceService.class.getCanonicalName());
			
		 Map<String, Object> rowValues= new HashMap<String, Object>();
		 rowValues.put("switch", swid);
		 rowValues.put("name","flow-"+swid);
		 rowValues.put("eth_type","0x0800");
		 rowValues.put("ip_proto","0x11");//UDP 协议号
		 rowValues.put("tp_dst","53");//端口号
		 rowValues.put("priority","900");
		 rowValues.put("active","true");
		 rowValues.put("actions","output=controller");//将匹配的数据流发送给控制器
		 
		 storageSource.insertRowAsync(StaticFlowEntryPusher.TABLE_NAME, rowValues);   	
    }
    
    
    
    
}

