package net.floodlightcontroller.classifier.features;

import net.floodlightcontroller.classifier.util.FeaturesUtil;
import net.floodlightcontroller.classifier.util.NetworkUtil;


public class NetworkFeatures implements FeaturesService {

	private double downloadSpeed;    //下载速度
	private double downloadBytes;   //下载字节
	private double searchCount;    //搜索引擎搜索结果数
	private double redirectCount; //重定向数
	//private double 
	
	public static void main(String args[]){
		//NetworkFeatures features = new NetworkFeatures();
	}
	
	public String extractFeatures(String domain, int index) {
		downloadSpeed = 0;
		downloadBytes = 0;
		searchCount = 0;
		redirectCount = 0;
		
		searchCount = NetworkUtil.getSearchCount(domain, "baidu");
		
		return FeaturesUtil.featureToString(this, index).trim();
	}

	
	public String toString() {
		return "NetworkFeatures [downloadSpeed=" + downloadSpeed
				+ ", downloadBytes=" + downloadBytes + ", searchCount="
				+ searchCount + ", redirectCount=" + redirectCount + "]";
	}
	
	
}