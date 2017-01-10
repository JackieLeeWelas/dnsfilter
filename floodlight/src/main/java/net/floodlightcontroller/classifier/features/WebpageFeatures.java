package net.floodlightcontroller.classifier.features;


import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


import net.floodlightcontroller.classifier.util.FeaturesUtil;
import net.floodlightcontroller.classifier.util.LogUtil;
import net.floodlightcontroller.classifier.util.WebpageUtil;

public class WebpageFeatures implements FeaturesService {

	private double hasWebpage; // 域名是否有网页,0表示没有网页，1表示有	1
	private double pageLineCount; // 网页行数         				2
	private double pageSize; // 网页大小 								3
	private double linksCount; // 超链接数 							4
	private double tagsCount; // html标签数 							5
	private double titleTagCount; // title标签数量 					6
	private double iframCount; // iframe数 							7
	private double ifram0SizeCount; // 0大小的ifram数 				8
	private double suspiciousJSFuncCount; // 可疑的js函数数 			9
	private double hasLoginForm;       //是否有登录表单              	10
	private double hasBadActionField;  //是否有不良行为的action		11

	// private double
	public static void main(String[] args) {
		WebpageFeatures features = new WebpageFeatures();
		String result = features.extractFeatures("www.google.com", 1); // zzxcws.com
		// Document shell = Document.createShell();
		// System.out.println(shell);
		System.out.println(result);
	}

	@Override
	public String extractFeatures(String domain, int index) {
		hasWebpage = 1;
		pageSize = 0;
		linksCount = 0;
		tagsCount = 0;
		iframCount = 0;
		ifram0SizeCount = 0;
		suspiciousJSFuncCount = 0;
		hasLoginForm = 0;
		hasBadActionField = 0;

        //Document document = null;
		if(!domain.startsWith("http")){
			domain = "http://" + domain;
		}
        Document document = WebpageUtil.get_page(domain);
		
		if (document == null) { // 获取不到页面，所有基于页面统计的特征没有
			hasWebpage = 0;
			LogUtil.log("this domain has no webpage, so no other Webpage features");
			return index + ":" + hasWebpage;
		}

		//System.out.println(document.toString());

		pageSize = document.toString().length(); // 计算源码长度
		pageLineCount = document.toString().split("\n").length; // 计算源码行数
		linksCount = document.getElementsByTag("a").size(); // 计算超链接数
		iframCount = document.getElementsByTag("iframe").size(); // 计算iframe标签数

		// System.out.println(document.getElementsByTag("a"));
		for (Element e : document.getElementsByTag("iframe")) {
			// System.out.println(e.toString());
			if (e.attr("width") == "0" || e.attr("height") == "0") {
				ifram0SizeCount++;
			}
		}
		// System.out.println(document.getElementsByTag("a").attr("title"));
		tagsCount = document.getAllElements().size(); // 获取总的标签数
		titleTagCount = document.getElementsByTag("title").size(); // 获取title标签数
		
		hasLoginForm = WebpageUtil.hasLoginForm(document)?1:0;
		
		try {
			URL website = new URL(domain);
			hasBadActionField = WebpageUtil.hasBadActionField(website)?1:0;
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

//		System.out.println(document.getElementsByTag("script"));
//
//		System.out.println(document.getAllElements().size());
//		System.out.println(document.getElementsByTag("title"));
//		System.out.println(document.getElementsByTag("iframe"));
//		System.out.println("linksCount:" + linksCount + "   iframCount:"
//				+ iframCount);

		// System.out.println("document.data():"+document.data());
		// System.out.println(document.hasText());
		// System.out.println(document.id());
		// System.out.println(document.isBlock());
		// System.out.println(document.location());
		// System.out.println(document.nodeName());
		// System.out.println(document.outerHtml());
		// System.out.println("===========================================");
		// System.out.println(document.html());
		// System.out.println(document.ownText());
		// System.out.println(document.tagName());
		// System.out.println(document.text());
		// System.out.println(document.title());
		// System.out.println(document.toString());
		// System.out.println(document.val());
		// System.out.println(document.body());

		// for(Element element:document.getAllElements()){
		// System.out.println("=========================================");
		// System.out.println(element);
		// }
		//
		//

		return FeaturesUtil.featureToString(this, index).trim();
	}

	@Override
	public String toString() {
		return "WebpageFeatures [hasWebpage=" + hasWebpage + ", pageLineCount="
				+ pageLineCount + ", pageSize=" + pageSize + ", linksCount="
				+ linksCount + ", tagsCount=" + tagsCount + ", titleTagCount="
				+ titleTagCount + ", iframCount=" + iframCount
				+ ", ifram0SizeCount=" + ifram0SizeCount
				+ ", suspiciousJSFuncCount=" + suspiciousJSFuncCount
				+ ", hasLoginForm=" + hasLoginForm + ", hasBadActionField="
				+ hasBadActionField + "]";
	}

	
	
}