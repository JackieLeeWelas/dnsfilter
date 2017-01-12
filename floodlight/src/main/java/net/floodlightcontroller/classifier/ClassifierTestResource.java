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

public class ClassifierTestResource extends ClassifierResourceBase {
	@Get("json")
    public Object handleRequest() {
         
        return "{\"status\" : \"failure\", \"details\" : \"Use POST to give testfile and modelfile for test\"}";
    }

	@Post
    public String handlePost(String json) {
		Map<String, String> testargs;
		try {
			IClassifierService classifier=this.getClassifierService();
			testargs=this.jsonToFileEntry(json);
			String testfile =testargs.get("testfile").toString();
			String modelfile =testargs.get("modelfile").toString();
			
			String[] pargs = null;
			if(testargs.get("pargs") != ""){
				pargs = testargs.get("pargs").split(" "); //参数之间以空格分隔
			}
			String resultfile = classifier.test(testfile, modelfile,pargs);
			if(resultfile != null){
				return "{\"status\" : \"success\", \"details\" : \"SVM test result is saved in" + resultfile + "\"}";
			}else{
				return "{\"status\" : \"failure\", \"details\" : \""+" SVM test is failed\"}";
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
				case "testfile":
					entry.put("testfile", jp.getText());
					break;
				case "modelfile":
					entry.put("modelfile", jp.getText());
					break;
				case "pargs":
					entry.put("pargs", jp.getText());
					break;
				default:
					throw new IOException("UnExpected FIELD_NAME"); 
			}
		
		}
		return entry;
	}
}
