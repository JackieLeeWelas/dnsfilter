package net.floodlightcontroller.packet;

//dns query 报文结构

public class DNS_Query {
	protected byte[] query_name;
	protected short query_type;
	protected short query_class;
	protected int length;
	
	public DNS_Query set_query_name(byte[] query_name){
		this.query_name=query_name;
		return this;
	}
	public byte[] get_query_name(){
		return this.query_name;
	}
	public DNS_Query set_length(int length)
	{
		this.length=length;
		return this;
	}
	public int get_lenght()
	{
		return length;
	}
	public DNS_Query set_query_type(short query_type){
		this.query_type=query_type;
		return this;
	}
	public short get_query_type()
	{
		return query_type;
	}
	public DNS_Query set_query_class(short query_class){
		this.query_class=query_class;
		return this;
	}
	public short get_query_class(){
		return query_class;
	}
	public String toString(){
		
		StringBuffer sb = new StringBuffer();
		int  i=0;
		while(this.length!=0&&this.query_name[i]!=(byte)0x0)
		{
			int j=i+(int)(0xff&this.query_name[i]);//获取当前标号的长度		
			for(int k=i+1;k<=j;k++)
			{
				char c=(char)(0xff&this.query_name[k]);
				sb.append(c);
				
			}		
			i=j+1;
			if(i!=this.length-1)
				sb.append(".");//添加点分隔符。
		}
		return sb.toString();		
	}
	
	
	
	
}