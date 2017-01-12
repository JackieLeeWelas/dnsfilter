package net.floodlightcontroller.dnsfilter;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
//对记录进行序列化
public class DNSFilterRecordsSerializer extends JsonSerializer<DNSFilterRecords> {

	@Override
	public void serialize(DNSFilterRecords record,  JsonGenerator jGen,
            SerializerProvider serializer) throws IOException,
			JsonProcessingException {
		 jGen.writeStartObject();
		 jGen.writeStringField("time",record.time);
		 jGen.writeStringField("host",record.host);
		 jGen.writeStringField("dnsserver",record.dnsserver);
		 jGen.writeStringField("swid",record.swid);
		 jGen.writeStringField("queryname",record.queryname);
		 jGen.writeEndObject();
		
	}

}
