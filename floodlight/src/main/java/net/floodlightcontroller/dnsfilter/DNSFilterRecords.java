package net.floodlightcontroller.dnsfilter;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
//定义被过滤的DNS数据包记录结构
@JsonSerialize(using=DNSFilterRecordsSerializer.class)
public class DNSFilterRecords {

	protected String time;
	protected String host;
	protected String dnsserver;
	protected String swid;
	protected String queryname;
	
	public DNSFilterRecords(){
		this.time="";
		this.host="";
		this.dnsserver="";
		this.swid="";
		this.queryname="";
	}
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("\"time\":");
		sb.append("\""+this.time+"\""+",");
		sb.append("\"host\":");
		sb.append("\""+this.host+"\""+",");
		sb.append("\"dsnserver\":");
		sb.append("\""+this.dnsserver+"\""+",");
		sb.append("\"swid\":");
		sb.append("\""+this.swid+"\""+",");
		sb.append("\"queryname\":");
		sb.append("\""+this.queryname+"\"");
		sb.append("}");
		
		return sb.toString();
		
	}
}
