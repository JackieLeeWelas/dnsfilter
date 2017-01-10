package net.floodlightcontroller.classifier.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;

import net.floodlightcontroller.classifier.features.BigramFeatures;
import net.floodlightcontroller.classifier.features.DnsFeatures;
import net.floodlightcontroller.classifier.features.LexicalFeatures;
import net.floodlightcontroller.classifier.features.NetworkFeatures;
import net.floodlightcontroller.classifier.features.WebpageFeatures;
import net.floodlightcontroller.classifier.features.WhoisFeatures;

public class FeaturesUtil {

	//遍历每个域名并提取其所有特征，并保存为训练集文件
	public static String extractFeatures(String sourceFile, String featureFile,
			String label) {
		// TODO Auto-generated method stub
		BufferedWriter writer = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					sourceFile));
			writer = new BufferedWriter(new FileWriter(featureFile, true));
			String domain = "";
			int count = 1;
			while ((domain = reader.readLine()) != null) {
				if(domain.isEmpty())
					continue;
				String features = label + " " + extractDomainFeature(domain);
				LogUtil.log(count+"==="+domain+"\'s features: "+features);
				writer.write(features);
				writer.newLine();
				count++;
			}
			reader.close();
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return featureFile;
	}

	// 提取一个域名的所有特征
	public static String extractDomainFeature(String domain) {
		// TODO Auto-generated method stub
		
		String features = "";
		int index = 1;
		int featureindex = 1;
		
		// 从域名中提取词汇特征数组
		LexicalFeatures lfeatures = new LexicalFeatures();
		features += lfeatures.extractFeatures(domain,index);
		LogUtil.log(domain+": "+featureindex++ + "***LexicalFeatures***" + lfeatures.toString());
		index += lfeatures.getClass().getDeclaredFields().length;//features.split(":").length;
		
		// 从域名中提取网页特征数组
		WebpageFeatures wpfeatures = new WebpageFeatures();
		features += " " + wpfeatures.extractFeatures(domain,index);
		LogUtil.log(domain+": "+featureindex++ + "***WebpageFeatures***" + wpfeatures.toString());
		index += wpfeatures.getClass().getDeclaredFields().length;//features.split(":").length;
		
//		// 从域名中提取DNS特征数组
		DnsFeatures dnsfeatures = new DnsFeatures();
		features += " " + dnsfeatures.extractFeatures(domain,index);
		LogUtil.log(domain+": "+featureindex++ + "***DnsFeatures***" + dnsfeatures.toString());
		index += dnsfeatures.getClass().getDeclaredFields().length;//index = features.split(":").length;

//		// 从域名中提取网络特征数组
		NetworkFeatures nwfeatures = new NetworkFeatures();
		features += " " + nwfeatures.extractFeatures(domain,index);
		LogUtil.log(domain+": "+featureindex++ + "***NetworkFeatures***" + nwfeatures.toString());
		index += nwfeatures.getClass().getDeclaredFields().length;//index = features.split(":").length;

//		// 从域名中提取Whois特征数组
		WhoisFeatures wifeatures = new WhoisFeatures();
		features += " " + wifeatures.extractFeatures(domain,index);
		LogUtil.log(domain+": "+featureindex++ + "***WhoisFeatures***" + wifeatures.toString());
		
		//从域名中提取Bigram特征数组
		BigramFeatures bgfeatures = new BigramFeatures();
		features += bgfeatures.extractFeatures(domain,index);
		LogUtil.log(domain+": " +featureindex+++ "***BigramFeatures***" + BigramsUtil.bigram(domain).toString() +  " " + bgfeatures);

		return features.trim();
	}
	
	//把特征属性转换为以冒号分隔的字符串 形式如：index:value
	public static String featureToString(Object object, int index){
		String features = "";
		Field[] fields = object.getClass().getDeclaredFields();

		try {
			for (Field field : fields) {
				field.setAccessible(true);
				if(Double.valueOf(field.get(object).toString()) == 0){
					index++;
					continue;
				}
				features += index + ":" + field.get(object) + " ";
				index++;
			}
		} catch (IllegalAccessException ie) {
			ie.printStackTrace();
		}
		return features.trim();
	}
}