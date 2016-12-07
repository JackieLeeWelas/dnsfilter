package net.floodlightcontroller.dnsfilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.MacAddress;
import org.projectfloodlight.openflow.types.OFPort;
import org.restlet.resource.Post;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import org.projectfloodlight.openflow.types.DatapathId;

public class DNSFilterProxyResource extends DNSFilterResourceBase{
	@Post
    public String handlePost(String json) {
		IDNSFilterService dnsfilter = this.getDNSFilterService();
		Map<String, String> entry;
		DatapathId switchid;
		IPv4Address src_ip,dst_ip,proxy_ip;
		MacAddress src_mac,dst_mac,proxy_mac;
		OFPort monitor_port=OFPort.ALL;
		int idletimeout=10,hardtimeout=0;
		
		try {
			
			entry=this.jsonToProxyEntry(json);
			//System.out.println(entry.toString());
		
		} catch (IOException e) {
			return "{\"status\" : \"Error! Could not parse , \"}";
		}
		
		try{
			switchid = DatapathId.of(entry.get("switch"));		
		}catch (Exception e) {
			return "{\"status\" : \"The format of switch id is Error \"}";
		}
			
		try{
			
			src_ip = IPv4Address.of(entry.get("src_ip"));
			dst_ip = IPv4Address.of(entry.get("dst_ip"));
			proxy_ip = IPv4Address.of(entry.get("proxy_ip"));
			
		}catch (Exception e) {
			return "{\"status\" : \"The format of IP is Error \"}";
		}
		
		try{
			
			src_mac=MacAddress.of(entry.get("src_mac"));
			dst_mac=MacAddress.of(entry.get("dst_mac"));
			proxy_mac=MacAddress.of(entry.get("proxy_mac"));
			
		}catch (Exception e) {
			return "{\"status\" : \"The format of MAC is Error \"}";
		}
		
		try{
			if(entry.containsKey("idletimeout")){
				idletimeout=Integer.valueOf(entry.get("idletimeout"));
			}
			if(idletimeout<0)
			{
				idletimeout=10;
			}
		}catch (Exception e) {
			
		}

		try{
			if(entry.containsKey("hardtimeout")){
				hardtimeout=Integer.valueOf(entry.get("hardtimeout"));
			}
			
			if(hardtimeout<0){
				hardtimeout=0;
			}
		}catch (Exception e) {
		}		
		
		try{
			if(entry.containsKey("monitor_port")){
				if(entry.get("monitor_port").toLowerCase()=="all")
				{
					monitor_port=OFPort.ALL;
				}else{
					monitor_port=OFPort.of(Integer.valueOf(entry.get("monitor_port")));
				}
					
			}
			
		}catch (Exception e) {
		}	
		
		System.out.println(hardtimeout+" : "+idletimeout);
		
		dnsfilter.setProxy(switchid, src_ip, dst_ip, proxy_ip, src_mac, dst_mac, proxy_mac, hardtimeout, idletimeout, monitor_port);
		
		return "{\"status\" : \"success , \"}";	
	}

	@SuppressWarnings("deprecation")
	private Map<String, String> jsonToProxyEntry(String fmJson)  throws IOException {
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
				case "switch":
					entry.put("switch", jp.getText());
					break;
				case "src_ip":
					entry.put("src_ip", jp.getText());
					break;
				case "proxy_ip":
					entry.put("proxy_ip", jp.getText());
					break;
				case "dst_ip":
					entry.put("dst_ip", jp.getText());
					break;
				case "src_mac":
					entry.put("src_mac", jp.getText());
					break;
				case "proxy_mac":
					entry.put("proxy_mac", jp.getText());
					break;
				case "dst_mac":
					entry.put("dst_mac", jp.getText());
					break;
				case "hardtimeout":
					entry.put("hardtimeout", jp.getText());
					break;
				case "idletimeout":
					entry.put("idletimeout", jp.getText());
					break;
				case "monitor_port":
					entry.put("monitor_port", jp.getText());
					break;
				default:
					throw new IOException("UnExpected FIELD_NAME"); 
			}
		
		}

		return entry;
	
	}
		
}
