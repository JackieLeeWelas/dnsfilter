package net.floodlightcontroller.classifier.features;

import java.util.StringTokenizer;

import net.floodlightcontroller.classifier.util.FeaturesUtil;

public class LexicalFeatures implements FeaturesService {

	private double domainLen; // 域名长度							1
	private double dotCount; // 点的数量							2
	private double numCount; // 数字的数量						3
	private double vowelCount; //元音字母的个数					4
	private double signCount; // 特殊符号的数量					5
	private double capitalCount; // 大写字母的数量					6
	private double avgLen; // 点间平均长度							7
	private double numRate; // 数字占总长度的比率					8
	private double signRate; // 特殊字符占总长度的比率				9
	private double capitalRate; // 大写字母占总长度的比率			10
	private double maxContinueNumLen; // 连续数字的最大长度			11
	private double maxContinueVowelLen;//连续元音字母最大长度  		12
	private double maxContinueSignLen; // 连续特殊字符的最大长度		13
	
	@Override
	public String  extractFeatures(String domain, int index) {
		// 初始化特征值
		this.domainLen = 0;
		this.dotCount = 0;
		this.numCount = 0;
		this.vowelCount = 0;
		this.signCount = 0;
		this.capitalCount = 0;
		this.avgLen = 0;
		this.numRate = 0;
		this.signRate = 0;
		this.capitalRate = 0;
		this.maxContinueNumLen = 0;
		this.maxContinueVowelLen = 0;
		this.maxContinueSignLen = 0;

		domainLen = domain.length();
		dotCount = new StringTokenizer(domain, ".").countTokens() - 1;

		for (int i = 0; i < domain.length(); i++) {
			char c = domain.charAt(i);
			if (c >= '0' && c <= '9') {
				numCount++;
			} else if (c == '-' || c == '_') {
				signCount++;
			} else if (Character.isUpperCase(c) == true) {
				capitalCount++;
			}
			
			c = Character.toLowerCase(c);
			if(c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u')
				vowelCount++;
		}
		// 点间平均长度
		String[] dotStr = domain.split("\\.");
		double sumLen = 0;
		for (String str : dotStr) {
			sumLen += str.length();
		}
		avgLen = sumLen / dotStr.length;
		// 数字,特殊字符,大写字母等占总长度的比率
		numRate = numCount / domainLen;
		signRate = signCount / domainLen;
		capitalRate = capitalCount / domainLen;

		// 最大连续数字子串长度
		String[] str1 = domain.split("[^0-9]+");
		maxContinueNumLen = calMaxContinueLen(str1);

		// 最大连续特殊字符子串长度
		String[] str2 = domain.split("[^-_]+");
		maxContinueSignLen = calMaxContinueLen(str2);
		
		String[] str3 = domain.split("[^aeiou]+");
		maxContinueVowelLen = calMaxContinueLen(str3);
			
		return FeaturesUtil.featureToString(this, index).trim();
	}

	public double calMaxContinueLen(String[] str) {
		String max = "";
		double maxContinueLen = 0;
		for (int i = 0; i < str.length; i++) {
			if (max.length() < str[i].length()) {
				max = str[i];
				maxContinueLen = max.length();
			} else if (max.length() == str[i].length()) {
				max += str[i];
				maxContinueLen = max.length() / 2;
			}
		}
		return maxContinueLen;
	}

	@Override
	public String toString() {
		return "LexicalFeatures [domainLen=" + domainLen + ", dotCount="
				+ dotCount + ", numCount=" + numCount + ", vowelCount="
				+ vowelCount + ", signCount=" + signCount + ", capitalCount="
				+ capitalCount + ", avgLen=" + avgLen + ", numRate=" + numRate
				+ ", signRate=" + signRate + ", capitalRate=" + capitalRate
				+ ", maxContinueNumLen=" + maxContinueNumLen
				+ ", maxContinueVowelLen=" + maxContinueVowelLen
				+ ", maxContinueSignLen=" + maxContinueSignLen + "]";
	}

	
}