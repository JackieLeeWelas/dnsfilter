package net.floodlightcontroller.classifier.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class PreProccess {
	
	public static void main(String args[]){
		String srcfile = "./data/trainset.vector";
		String destfile = "./data/newtrainset.vector";
		preProccessFile_1(srcfile, destfile);
	}

	/**
	 * String is IP ?
	 * 
	 * @param str
	 * @return b
	 */
	public static boolean isIP(String str) {
		boolean b = false;
		str = str.trim();
		if (str.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
			b = true;
		}
		return b;
	}

	/**
	 *  对文件进行预处理，把ip或者相同的行删除
	 * @param srcfile
	 * @param destfile
	 */
	public static void preProccessFile_1(String srcfile, String destfile) {
		File file = new File(destfile);
		Set<String> domainSet = new HashSet<String>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(srcfile));
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			String line;
			int count = 0;
			int oldsumline = 0;
			int newsumline = 0;
			while ((line = reader.readLine()) != null) {

				if (isIP(line)) {
					LogUtil.log(count++ + " deleted ip: " + line);
					continue;
				}
				oldsumline++;
				domainSet.add(line);
			}

			newsumline = domainSet.size();
			for (String str : domainSet) {
				writer.write(str);
				writer.newLine();
			}
			
			System.out.println("oldsumline:"+oldsumline+"\nnewsumline:"+newsumline);
			
			reader.close();
			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 对文件预处理，把不可达或者没有网页的域名删除
	 * @param srcfile
	 * @param destfile
	 */
	public static void preProccessFile_2(String srcfile, String destfile) {
		File file = new File(destfile);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(srcfile));
			BufferedWriter writer = new BufferedWriter(new FileWriter(file,
					true));
			String domain;
			int count = 0;
			while ((domain = reader.readLine()) != null) {

				if (domain.isEmpty() || !DnsUtil.isReachable(domain, 3000)
						|| WebpageUtil.get_page(domain) == null) {
					LogUtil.log(count++ + " deleted domain: " + domain + "!!!");
					continue;
				}
				writer.write(domain);
				// domainSet.add(line);
			}
			reader.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * empty the file before read
	 * 
	 * @param filename
	 */
	public static void emptyFile(String filename) {

		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(filename));
			writer.write("");
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
