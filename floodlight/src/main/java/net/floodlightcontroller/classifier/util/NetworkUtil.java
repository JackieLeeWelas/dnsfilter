//by jiabin_h
package net.floodlightcontroller.classifier.util;

import java.io.*;
import java.net.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetworkUtil {

	private static long TIMEOUT = 3000;
	public static void main(String args[]) {
		//NetworkUtil wa = new NetworkUtil();
		try {
			NetworkUtil.getSearchCount("icbccq.cn.com", "baidu");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static double getSearchCount(String domain, String engine) {
		String str = domain;

		String url = "";
		String inputLine = "";
		String searchCount = "";
		
		if (engine == "baidu") {
			url = "http://www.baidu.com/s?ie=utf-8&wd=" + domain;
		} else if (engine == "google") {
			url = "http://www.google.com.hk/search?client=ubuntu&channel=fs&q="
					+ domain + "&ie=utf-8&oe=utf-8";
		}
		//https://www.baidu.com/s?ie=utf-8&f=3&rsv_bp=1&tn=baidu&wd=google.com.eg
		try {
			url += URLEncoder.encode(str, "UTF-8");// 将关键字编码成URL格式
			final URL search = new URL(url);
			ExecutorService executor = Executors.newSingleThreadExecutor();     
	        Callable<BufferedReader> connect = new Callable<BufferedReader>() {  
	            public BufferedReader call() throws Exception {  
	                //开始执行耗时操作  
	            	BufferedReader in = new BufferedReader( // html输入流，UTF-8格式
	    					new InputStreamReader(search.openStream(), "UTF-8"));
	                return in;  
	            }  
	        };  
	        Future<BufferedReader> future1 = executor.submit(connect); 
	        BufferedReader in;
			try {  
				 in = future1.get(TIMEOUT, TimeUnit.MILLISECONDS); //任务处理超时时间设为 TIMEOUT 秒  
				 LogUtil.log("连接成功，正在获取"+engine+"搜索结果...");
	        } catch (TimeoutException | InterruptedException | ExecutionException ex) {  
	        	LogUtil.log(domain+"获取"+engine+"搜索结果超时...."); 
	        	future1.cancel(true);
	        	executor.shutdown();
	        	return 0;
	        } 	

			while ((inputLine = in.readLine()) != null) {
				Pattern p = null;
				if (engine == "baidu") {
					p = Pattern.compile("</div>百度为您找到相关结果约([^</div>]*)"); // 正则表达式
				} else if (engine == "google") {
					p = Pattern.compile("</div>约有([^</div>]*)"); // 正则表达式
				}

				Matcher m = p.matcher(inputLine);
				if (m.find()) {
					String temp = m.group(1).toString();
					searchCount = temp.substring(0, temp.length() - 1);
				}
			}
			in.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		LogUtil.log(domain + " 在"+engine + "中找到相关结果约 " + searchCount + " 条"); 

		return Double.valueOf(searchCount.replaceAll(",", "")).doubleValue();
	}
}