package net.floodlightcontroller.classifier.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class WebpageUtil {

	
	static ArrayList<Element> loginForms = new ArrayList<>();
	private static long TIMEOUT = 3000;

//	public static void main(String args[]) {
//		WebpageUtil parse = new WebpageUtil("www.baidu.com");
//		Document doc1 = parse.get_page();
//		System.out.println(parse.getDoc().html());
//		try {
//			URL url = new URL("http://www.baidu.com");
//			System.out.println("badActionField:"+parse.badActionField(url));
//			
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		// parse.parse_page();
//		System.out.println("hasLoginForm:"+parse.hasLoginForm(doc1));
//	}
	/**
	 * 根据域名获取网页文档
	 * @param domain
	 * @return
	 */
	public static Document get_page(String domain) {
		Document doc = null;
		try {
//			if(!domain.startsWith("http")){
//				domain = "http://" + domain;
//			}
			URL website = new URL(domain); // The website you want to connect
			
			long startTime = System.currentTimeMillis();
			final HttpURLConnection httpUrlConnetion = (HttpURLConnection) website
					.openConnection();
			
			ExecutorService executor = Executors.newSingleThreadExecutor();     
	        Callable<String> connect = new Callable<String>() {  
	            public String call() throws Exception {  
	                //开始执行耗时操作  
	            	httpUrlConnetion.connect();
	                return "域名连接成功。。。";  
	            }  
	        };  
	        Callable<StringBuilder> getContent = new Callable<StringBuilder>() {  
	            public StringBuilder call() throws Exception {  
	                //开始执行耗时操作  
	            	BufferedReader br = new BufferedReader(new InputStreamReader(
	    					httpUrlConnetion.getInputStream()));
	    			// -- Download the website into a buffer
	            	StringBuilder buffer = new StringBuilder();
	    			String str;
	    			while ((str = br.readLine()) != null) {
	    				buffer.append(str);
	    			}
	                return buffer;  
	            }  
	        };  
			//httpUrlConnetion.connect();
	        Future<String> future1 = executor.submit(connect); 
	        Future<StringBuilder> future2 = executor.submit(getContent);
			try {  
				LogUtil.log("开始http连接...."); 
	            String obj = future1.get(TIMEOUT, TimeUnit.MILLISECONDS); //任务处理超时时间设为 TIMEOUT 秒  
	            LogUtil.log(obj.toString());  
	            
	        } catch (TimeoutException ex) {  
	        	LogUtil.log("连接超时...."); 
	        	future1.cancel(true);
	        	executor.shutdown();
	        	return doc;
	        	//exec.shutdown();
	            //ex.printStackTrace();  
	        } 	
			StringBuilder buffer;
			try {
				buffer = future2.get(TIMEOUT, TimeUnit.MILLISECONDS);
				 // -- Parse the buffer with Jsoup
	            doc = Jsoup.parse(buffer.toString());
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				LogUtil.log("获取网页源码超时...."); 
	        	future1.cancel(true);
	        	executor.shutdown();
	        	return doc;
			}
			long endTime = System.currentTimeMillis();
			long time = endTime - startTime;
			LogUtil.log(domain + " 获取网页源码花费的时间为： "+Long.toString(time));
           
		} catch (IOException e) {
			//e.printStackTrace();
			LogUtil.log("IOException 获取网页源码出现异常：" + e.getMessage());
		}catch (Exception e) {  
            LogUtil.log("Exception 获取网页源码失败...." + e.getMessage()); 
        }  	
		return doc;
	}

	/**
	 * 另外一种获取网页的方法
	 * @param domain
	 * @return
	 */
	public static Document parse_page(final String domain) {
		Document doc = null;
		
		ExecutorService executor = Executors.newSingleThreadExecutor();     
        Callable<Document> getdoc = new Callable<Document>() {  
            public Document call() throws Exception {  
                //开始执行耗时操作  
            	Document doc = Jsoup.connect(domain).get();
                return doc;  
            }  
        };  
        Future<Document> future = executor.submit(getdoc); 
		try {  
			LogUtil.log("开始获取doc...."); 
			doc = future.get(TIMEOUT, TimeUnit.MILLISECONDS); //任务处理超时时间设为 TIMEOUT 秒  
           // LogUtil.log(obj.toString());  
            
        } catch (TimeoutException ex) {  
        	LogUtil.log("连接超时...." + ex.getMessage()); 
        	future.cancel(true);
        	executor.shutdown();
        } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
        	LogUtil.log(e.getMessage());
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			LogUtil.log(e.getMessage());
		} 
		return doc;
	}
	
	public static boolean hasLoginForm(Document doc) {
        Elements foms = doc.select("form");
       // System.out.println("\nTEXT : " + foms.size());
        for (Element fom : foms) {
            int ctr = 0;
            // get the value from href attribute
            //System.out.println("\nTEXT : " + fom.text());
            Elements inputs = fom.getElementsByTag("input");

            for (Element input : inputs) {
                if (input.hasAttr("type")) {
                    if (input.attr("type").compareToIgnoreCase("password") == 0) {
                        loginForms.add(fom);
                        ctr++;
                    }
                }
            }
            if (ctr >= 1 && ctr != 2)
                return true;
        }
        return false;
    }
	public static boolean hasBadActionField(URL url) {
        //System.out.println("jkj" + loginForms.size());
        for (Element fom : loginForms) {
            String action = fom.attr("action");
            if (action == null)
                return true;
            try {
                URL _action = new URL(action);
                if (!(_action.getHost().equalsIgnoreCase(url.getHost())))
                    return true;
            } catch (MalformedURLException e) {
                return true;
            }
        }
        return false;
    }

}
