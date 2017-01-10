package net.floodlightcontroller.packet;

import java.nio.ByteBuffer;
import java.util.ArrayList;

//dns 报文结构

public class DNS extends BasePacket{
    protected short identification;
    protected short flags;
    protected short question_num;
    protected short answer_num;
    protected short authority_num;
    protected short additional_num;
    protected DNS_Query query_name;
    
    public short getqustion_num()
    {
    	return this.question_num;
    }
    public short getanswer_num(){
    	return this.answer_num;
    }
    //提取dns查询域名
    public String toString(){
    	if(this.question_num!=0)
    		return query_name.toString();
    	else
    		return "";
    }
    //序列化，构造dns数据包
	@Override
	public byte[] serialize() {
        byte[] payloadData = null;
        if (payload != null) {
            payload.setParent(this);
            payloadData = payload.serialize();
        }
        
        byte[] data = new byte[12+payloadData.length];
        ByteBuffer bb = ByteBuffer.wrap(data);
        bb.putShort(this.identification);
        bb.putShort(this.flags);
        bb.putShort(this.question_num);
        bb.putShort(this.answer_num);
        bb.putShort(this.authority_num);
        bb.putShort(this.additional_num);
        bb.put(this.query_name.get_query_name());
        bb.putShort(this.query_name.get_query_type());
        bb.putShort(this.query_name.get_query_class());
        
        if (payloadData != null)
            bb.put(payloadData);
		return data;
	}
	//反序列化，提取dns数据包中关于dns的各字段信息
	@Override
	public IPacket deserialize(byte[] data, int offset, int length)
			throws PacketParsingException {
		ByteBuffer bb = ByteBuffer.wrap(data, offset, length);
		//提取标识、标志、问题数等字段
		this.identification=bb.getShort();
		this.flags=bb.getShort();
		this.question_num=bb.getShort();
		this.answer_num=bb.getShort();
		this.authority_num=bb.getShort();
		this.additional_num=bb.getShort();	
		// 提取查询名的
		this.query_name=new DNS_Query();		
		if(this.question_num!=0){
			
		ArrayList<Byte> tmpA =new  ArrayList<Byte>();
		//由于查询名是变长的，因此只能逐个字节提取，直到遇到结束位0为止
		byte tmp=bb.get();
		while(tmp!=0x00){
			
			tmpA.add(tmp);
			tmp=bb.get();
		}
		tmpA.add(tmp);	
		byte[] tmp_query_name= new byte[tmpA.size()];
		for(int i=0;i<tmpA.size();i++){
			tmp_query_name[i]=tmpA.get(i);
		}	
		this.query_name.set_query_name(tmp_query_name);
		this.query_name.set_length(tmpA.size());
		this.query_name.set_query_type(bb.getShort());
		this.query_name.set_query_class(bb.getShort());		
		}
		//将剩余的字段作为负载。		
		this.payload = new Data();
        this.payload = payload.deserialize(data, bb.position(), bb.limit()-bb.position());
        this.payload.setParent(this);
        return this;
	}
	
}
