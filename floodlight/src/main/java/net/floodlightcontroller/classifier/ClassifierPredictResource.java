package net.floodlightcontroller.classifier;

import org.restlet.resource.Get;
import org.restlet.resource.Put;


public class ClassifierPredictResource extends ClassifierResourceBase {
	@Get("json")
    public Object handleRequest() {
         
        return "{\"status\" : \"failure\", \"details\" : \"Use PUT to give domain for predict\"}";
    }

	@Put("json")
	public Object handlePut() {
    	IClassifierService predict = this.getClassifierService();
    	String domain = (String) getRequestAttributes().get("domain");
    	
    	if(predict.predict(domain) > 0){
    		return "{\"status\" : \"Success\", \"predict result\" : \"***"+domain+"*** is predicted as malicious domain !\"}";
    	}else{
    		return "{\"status\" : \"Success\", \"predict result\" : \"***"+domain+"*** is predicted as benign domain...\"}";
    	}
    }
}
