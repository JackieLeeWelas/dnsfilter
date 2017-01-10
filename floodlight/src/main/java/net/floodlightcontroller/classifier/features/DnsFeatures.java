package net.floodlightcontroller.classifier.features;

import com.jdig.model.Type;

import net.floodlightcontroller.classifier.util.DnsUtil;
import net.floodlightcontroller.classifier.util.FeaturesUtil;

public class DnsFeatures implements FeaturesService {

	private double aRecordCount;     	//A记录数
	private double nsRecordCount;    	//ns记录数
	private double aRecordofNsCount;  	//所有ns的a记录总数
	private double cnameCount;      	//别名数
	private double ttl;           		//ttl值
	private double asCount;       		//AS数
	
	public String extractFeatures(String domain,int index) {
		// TODO Auto-generated method stub
		aRecordCount = 0;
		nsRecordCount = 0;
		aRecordofNsCount = 0;
		cnameCount = 0;
		ttl = 0;
		asCount = 0;
		
		aRecordCount = DnsUtil.getRecordCountByType(domain, Type.A);
		nsRecordCount = DnsUtil.getRecordCountByType(domain, Type.NS);
		if(nsRecordCount >= 1)
		    aRecordofNsCount = DnsUtil.getARecordofNsCount(domain);
		cnameCount = DnsUtil.getRecordCountByType(domain, Type.CNAME);
		//InetAddress inet = new Inet();

		return FeaturesUtil.featureToString(this, index).trim();
	}

	
	public String toString() {
		return "DnsFeatures [aRecordCount=" + aRecordCount + ", nsRecordCount="
				+ nsRecordCount + ", aRecordofNsCount=" + aRecordofNsCount
				+ ", cnameCount=" + cnameCount + ", ttl=" + ttl + ", asCount="
				+ asCount + "]";
	}
	
	
}