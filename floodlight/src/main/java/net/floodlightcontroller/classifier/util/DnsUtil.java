package net.floodlightcontroller.classifier.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import com.jdig.model.DnsEntry;
import com.jdig.model.Type;
import com.jdig.service.DnsService;

public class DnsUtil {

	public static void main(String[] args) throws IOException {


//			List<DnsEntry> entries = new DnsService().lookup("baidu.com",
//					Type.any());
//			for (DnsEntry entry : entries) {
//				System.out.println(entry.toString());
//			}
		//System.out.println(getRecordCountByType("baidu.com",Type.any()));
//			System.out.println(getRecordCountByType("sdgdghd.com",Type.A));
//			System.out.println(getRecordCountByType("sdgdghd.com",Type.NS));
//			System.out.println(getRecordCountByType("sdgdghd.com",Type.CNAME));
//			System.out.println(getRecordCountByType("sdgdghd.com",Type.MX));
			//System.out.println(getARecordofNsCount("baidu.com"));
			
//			try {
//				//String dottedQuadIpAddress = InetAddress.getByName( "blog.arganzheng.me" ).getHostAddress();
//				InetAddress ias[] = InetAddress.getAllByName("baidu.com");
//				for(InetAddress ia:ias){
//					System.out.println("getHostAddress():"+ia.getHostAddress());
//					System.out.println("getCanonicalHostName():"+ia.getCanonicalHostName());
//					System.out.println("getHostName():"+ia.getHostName());
//					System.out.println("isReachable(5000):"+ia.isReachable(65535));
//					System.out.println("getAddress():"+ia.getAddress());
//					//System.out.println("getAllByName(\"www.baidu.com\"):"+ia.getAllByName("www.baidu.com"));
//					System.out.println("=================================================");
//				}
//			} catch (UnknownHostException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		BufferedReader reader = new BufferedReader(new FileReader(
				"./data/whitelist"));
		String domain = "";
		int i = 1;
		int yes = 0;
		int no = 0;
		while ((domain = reader.readLine()) != null) {
			boolean b = isReachable(domain,1000);
			System.out.println(i + ": " + domain + " is reachable?  " + b);
			if(b) yes++;
			else no++;
			i++;
		}
		reader.close();
		System.out.println("all:" + i + " yes:" + yes +" no:" + no);
		
	}
	
	//测试是否能连通，通过socket连接
	public static boolean isReachable(String domain,int timeOutMillis){
		boolean isreachable = false;
		try {
			//String dottedQuadIpAddress = InetAddress.getByName( "blog.arganzheng.me" ).getHostAddress();
			InetAddress ias[] = InetAddress.getAllByName(domain);
			//boolean isreachable = false;
			for(InetAddress ia:ias){
				String ip = ia.getHostAddress();
				
				if(isReachable(ip,53,3000)){
					isreachable = true;
					break;
				}
				//System.out.println("=================================================");
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isreachable;
	}
	
	private static boolean isReachable(String addr, int openPort, int timeOutMillis) {
	    // Any Open port on other machine
	    // openPort =  22 - ssh, 80 or 443 - webserver, 25 - mailserver etc.
	    try {
	        try (Socket soc = new Socket()) {
	            soc.connect(new InetSocketAddress(addr, openPort), timeOutMillis);
	        }
	        return true;
	    } catch (IOException ex) {
	        return false;
	    }
	}

	public static double getRecordCountByType(String domain, Type type) {
		List<DnsEntry> entries = new ArrayList<DnsEntry>();
		try {
			entries = new DnsService().lookup(domain, type);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			LogUtil.log("\"" + domain + "\"" + " has no " + type + " record,"+e.getMessage());
			return 0;
		}
		return entries.size();
	}

	public static double getARecordofNsCount(String domain) {
		List<DnsEntry> nsentries = new ArrayList<DnsEntry>();
		double aRecordSum = 0;
		try {
			nsentries = new DnsService().lookup(domain, Type.NS);
			
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			LogUtil.log("\"" + domain + "\"" + " has no NS record,"+e.getMessage());
			return 0;
			//e.printStackTrace();
		}
		for (DnsEntry entry : nsentries) {
			aRecordSum += getRecordCountByType(entry.getName(),Type.A);
			//aentries.addAll(new DnsService().lookup(entry.getName(), Type.A));
			//System.out.println(entry.toString());
		}
//		for (DnsEntry entry : aentries) {
//			System.out.println(entry.toString());
//		}
		return aRecordSum;//aentries.size();
	}
}
