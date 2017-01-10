package net.floodlightcontroller.classifier;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.restlet.resource.Get;
import org.restlet.resource.Post;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.MappingJsonFactory;

public class ClassifierFeaturesResource extends ClassifierResourceBase {
	@Get("json")
    public Object handleRequest() {
         
        return "{\"status\" : \"failure\", \"details\" : \"Use POST to give sourcefile and featurefile and label for extract features\"}";
    }
	
	@Post
    public String handlePost(String json) {
		Map<String, String> filename;
		try {
			IClassifierService classifier=this.getClassifierService();
			filename=this.jsonToFileEntry(json);
			String sourcefile =filename.get("sourcefile").toString();
			String featurefile =filename.get("featurefile").toString();
			String label = filename.get("label").toString();
			if( classifier.extractFeatures(sourcefile, featurefile, label) != null){
				return "{\"status\" : \"success\", \"details\" : features extracted result is saved in\""+featurefile+" \"}";
			}else{
				return "{\"status\" : \"failure\", \"details\" : \""+" features extracted is failed\"}";
			}
			
		} catch (IOException e) {
			return "{\"status\" : \"Error! Could not parse , \"}";
		}  
	}

	@SuppressWarnings("deprecation")
	public  Map<String, String> jsonToFileEntry(String fmJson) throws IOException {
		Map<String, String> entry = new HashMap<String, String>();
		MappingJsonFactory f = new MappingJsonFactory();
		JsonParser jp;

		try {
			jp = f.createJsonParser(fmJson);
		} catch (JsonParseException e) {
			throw new IOException(e);
		}
			
		jp.nextToken();
		if (jp.getCurrentToken() != JsonToken.START_OBJECT) {
			throw new IOException("Expected START_OBJECT");
		}
		
		while (jp.nextToken() != JsonToken.END_OBJECT) {
			if (jp.getCurrentToken() != JsonToken.FIELD_NAME) {
				throw new IOException("Expected FIELD_NAME");
			}

			String n = jp.getCurrentName();
			jp.nextToken();
			switch (n) {
				case "sourcefile":
					entry.put("sourcefile", jp.getText());
					break;
				case "featurefile":
					entry.put("featurefile", jp.getText());
					break;
				case "label":
					entry.put("label", jp.getText());
					break;
				default:
					throw new IOException("UnExpected FIELD_NAME"); 
			}
		
		}
		return entry;
	}
}
