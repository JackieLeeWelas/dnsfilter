package net.floodlightcontroller.classifier;


import org.restlet.resource.ServerResource;

public class ClassifierResourceBase extends ServerResource {

	IClassifierService getClassifierService(){
		return (IClassifierService)getContext().getAttributes().
		        get(IClassifierService.class.getCanonicalName());
	}
}
