package net.floodlightcontroller.classifier.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class BigramsUtil
{
    
//    public  static void main(String[]args){
//    	//BigramsUtil gram = new BigramsUtil("JackieLee");
//    	List<String> list = BigramsUtil.bigram("baidu.com");
//    	
//    	System.out.println(list.toString());
//    }
    
	//把域名分割为bigram数组
    public static List<String> bigram(String domain)
    {
        ArrayList<String> bigram = new ArrayList<String>();
        for (int i = 0; i < domain.length() - 1; i++)
        {
            String str = "";
            str += domain.charAt(i);
            str += domain.charAt(i+1);
            bigram.add(str);
        }
        return bigram;
    }
    
   //按值排序
	public static Map<Integer, Integer> sortMapByKey(Map<Integer, Integer> map) {
		if (map == null || map.isEmpty()) {
			return null;
		}

		Map<Integer, Integer> sortMap = new TreeMap<Integer, Integer>();

		sortMap.putAll(map);
		return sortMap;
	}
    
    

}
