package net.floodlightcontroller.classifier.util;


import net.floodlightcontroller.classifier.features.LexicalFeatures;

public class test {

	public static void main(String args[]){
		
		//System.out.println(PreProccess.isIP("333.12.56.d3"));
		//PreProccess.preProccessFile("./data/phishingwebsites", "./data/newphishingwebsites");
		LexicalFeatures lf = new LexicalFeatures();
		System.out.println(lf.extractFeatures("www.baidu.com", 1));
		System.out.println(lf.toString());
//		String domain = "www.baidu.com";
//		System.out.println(FeaturesUtil.extractDomainFeature(domain));
//		Classifier classifier = new Classifier();
//		System.out.println(classifier.predict(domain));
//		try {
//			String result = InetAddress.getByName( "baidu.com" ).getHostAddress();
//			System.out.println(result);
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		try {
//			//List<DnsEntry> blacklistLookup = new DnsService().blacklistLookup("1.2.3.4", "dnsbl.sorbs.net");
//			//System.out.println(blacklistLookup.toString());
//		} catch (NamingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}
