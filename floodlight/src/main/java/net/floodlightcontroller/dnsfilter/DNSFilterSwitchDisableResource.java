package net.floodlightcontroller.dnsfilter;



import java.util.Set;

import net.floodlightcontroller.core.internal.IOFSwitchService;
import net.floodlightcontroller.staticflowentry.StaticFlowEntryPusher;
import net.floodlightcontroller.storage.IStorageSourceService;

import org.projectfloodlight.openflow.types.DatapathId;
import org.restlet.resource.Get;
import org.restlet.resource.Put;

public class DNSFilterSwitchDisableResource extends DNSFilterResourceBase {
	@Get("json")
    public Object handleRequest() {
         
        return "{\"status\" : \"failure\", \"details\" : \"Use PUT to enable dnsfilter\"}";
    }
	//关闭Switch的DNS过滤功能，并删除相应的静态流表
    @Put("json")
    public Object handlePut() {
    	IDNSFilterService dnsfilter=this.getDNSFilterService();
    	String param = (String) getRequestAttributes().get("switch");
		IOFSwitchService switchService = (IOFSwitchService) getContext().getAttributes().
   		 			get(IOFSwitchService.class.getCanonicalName());
   		Set<DatapathId> switchDpids = switchService.getAllSwitchDpids();
    	if(param.toLowerCase().equals("all"))
    	{
    		//关闭所有Switch的DNS过滤功能，并删除相应的静态流表
    		dnsfilter.clearSwFilterSet();
    		for (DatapathId swid : switchDpids) {  			
				this.deletestaicflow(swid.toString());
			}   
    		return "{\"status\" : \"Success\"}";
    	}else{

   		 try {
   			 	//检查该DPID是否存在。存在则关闭DNS过滤功能，并添加相应静态流表
   		 		if(switchDpids.contains(DatapathId.of(param))){
   		 			dnsfilter.removeSwFilterSet(DatapathId.of(param));
   		 			this.deletestaicflow(param);
   		 			return "{\"status\" : \"Success\"}";
   		 		}else{
   		 			return "{\"status\" : \"failure\", \"details\" : \"the SwitchId is not exist\"}";
   		 		}
   		 		
   		 	}catch (NumberFormatException e){
   		 		return "{\"status\" : \"failure\", \"details\" : \"the SwitchId is error\"}";
   		 }
		 
    	}
    	
    }
  //删除静态流表
    public void deletestaicflow(String swid){
    	
    	IStorageSourceService storageSource =
					(IStorageSourceService)getContext().getAttributes().
					get(IStorageSourceService.class.getCanonicalName());
    	
		 storageSource.deleteRowAsync(StaticFlowEntryPusher.TABLE_NAME, "flow-"+swid);
    }
    
}