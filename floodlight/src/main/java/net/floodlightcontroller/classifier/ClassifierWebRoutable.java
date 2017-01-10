package net.floodlightcontroller.classifier;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import net.floodlightcontroller.restserver.RestletRoutable;

public class ClassifierWebRoutable implements RestletRoutable {

	@Override
	public Restlet getRestlet(Context context) {
		// TODO Auto-generated method stub
		Router router=new Router(context);
		router.attach("/feature",ClassifierFeaturesResource.class);
		router.attach("/train",ClassifierTrainResource.class);
		router.attach("/test",ClassifierTestResource.class);
		router.attach("/predict/{domain}",ClassifierPredictResource.class);
		
		return router;
	}

	@Override
	public String basePath() {
		// TODO Auto-generated method stub
		return "/wm/classifier";
	}

}
