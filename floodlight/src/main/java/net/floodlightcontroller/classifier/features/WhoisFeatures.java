package net.floodlightcontroller.classifier.features;

import java.io.IOException;
import org.apache.commons.net.whois.WhoisClient;


import net.floodlightcontroller.classifier.util.FeaturesUtil;


public class WhoisFeatures implements FeaturesService {

//	private static final int DEFAULT_PORT = 43;
	//public final static String DEFAULT_HOST = "whois.cnnic.net.cn";// cn接口："whois.cnnic.net.cn" com接口："whois.internic.net"

	public String extractFeatures(String domain, int index) {
		return FeaturesUtil.featureToString(this, index).trim();
	}

	
	
	public static void main(String[] args) {

//		WhoisFeatures obj = new WhoisFeatures();
//		System.out.println(obj.getWhois("qq.com"));
//		System.out.println("Done");
		
//		WhoisApi whoisApi = new WhoisApi("w8smYKX2ftmshI11umIWsb0q48Xhp16mnSljsnUisaIje5JMG4");
//		try {
//			System.out.println(whoisApi.isAvailable("baidu.com") ? "available" : "registered");
//		} catch (RecoverableWhoisApiException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
	}

	public String getWhois(String domainName) {

		StringBuilder result = new StringBuilder("");

		WhoisClient whois = new WhoisClient();
		try {

			//default is internic.net
			whois.connect(WhoisClient.DEFAULT_HOST);
			String whoisData1 = whois.query("=" + domainName);
			result.append(whoisData1);
			whois.disconnect();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return result.toString();
	}

	@Override
	public String toString() {
		return "WhoisFeatures []";
	}

	

}