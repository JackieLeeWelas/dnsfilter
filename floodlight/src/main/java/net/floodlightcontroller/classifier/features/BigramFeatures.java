package net.floodlightcontroller.classifier.features;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.floodlightcontroller.classifier.util.BigramsUtil;
import net.floodlightcontroller.classifier.util.LogUtil;

public class BigramFeatures implements FeaturesService {

//	public static void main(String[] args) {
//		BigramFeatures bf = new BigramFeatures();
//		System.out.println(bf.extractFeatures("hsfj.yinglianwy.com", 1));
//	}
	
	@Override
	public String extractFeatures(String domain, int index) {
		// TODO Auto-generated method stub
		String features = "";
		Map<Integer, Integer> bigramFreq = new HashMap<Integer, Integer>();
		List<String> bigrams = BigramsUtil.bigram(domain);
		for(String str:bigrams){
			index = (int)str.charAt(0)*256 + (int)str.charAt(1);
			
			if(bigramFreq.containsKey(index)){
				bigramFreq.put(index, bigramFreq.get(index)+1);
			}else 
				bigramFreq.put(index,1);
		}
		
		bigramFreq = BigramsUtil.sortMapByKey(bigramFreq);
		
		for(Entry<Integer, Integer> entry : bigramFreq.entrySet()){
			features += entry.getKey()+":"+entry.getValue() + " ";
		}
		LogUtil.log(domain + ": BigramFeatures " + bigrams.toString() +  " " + features);
		return features.trim();
	}

}
