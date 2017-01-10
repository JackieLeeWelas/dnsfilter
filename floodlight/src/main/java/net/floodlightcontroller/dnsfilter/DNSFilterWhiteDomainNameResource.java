package net.floodlightcontroller.dnsfilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.restlet.resource.Get;
import org.restlet.resource.Post;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.MappingJsonFactory;

public class DNSFilterWhiteDomainNameResource extends DNSFilterResourceBase {
	@Get("json")
    public Object handleGet() {
		return "{\"status\" : \"failure\", \"details\" : \"Use POST to operate WhiteDomainName\"}";
		
	}
	//添加，删除域名黑名单，或查询域名是否在白名单中
	@Post
    public String handlePost(String json) {
		Map<String, String> name;
		try {
			IDNSFilterService dnsfilter=this.getDNSFilterService();
			name=this.jsonToDomainNameEntry(json);
			String domain =name.get("domainname").toString();
			switch(name.get("action").toLowerCase()){
			case "add":
				if(dnsfilter.insertWhiteDomain(domain))
					return "{\"status\" : \"success\", \"details\" : \""+domain+" has been inserted in whitelist\"}";
				else
					return "{\"status\" : \"failure\", \"details\" : \""+domain+" is not a correct domain\"}";
			case "remove":
				if(dnsfilter.removeWhiteDomain(domain))
					return "{\"status\" : \"success\", \"details\" : \""+domain+" has been removed from whitelist\"}";
				else
					return "{\"status\" : \"failure\", \"details\" : \""+domain+" is not a correct domain or not in the whitelist\"}";
			case "search":
				if(dnsfilter.hasWhiteDomain(domain))
					return "{\"status\" : \"success\", \"details\" : \""+domain+" in the whitelist\"}";
				else
					return "{\"status\" : \"failure\", \"details\" : \""+domain+" is not a correct domain or not in the whitelist\"}";
			default:
				return "{\"status\" : \"failure\", \"details\" : \"The action only can be add,remove or search.\"}";
			}
		} catch (IOException e) {
			return "{\"status\" : \"Error! Could not parse , \"}";
		}  
		
	}
	@SuppressWarnings("deprecation")
	public  Map<String, String> jsonToDomainNameEntry(String fmJson) throws IOException {
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
				case "domainname":
					entry.put("domainname", jp.getText());
					break;
				case "action":
					entry.put("action", jp.getText());
					break;
				default:
					throw new IOException("UnExpected FIELD_NAME"); 
			}
		
		}
		return entry;
	}
}
