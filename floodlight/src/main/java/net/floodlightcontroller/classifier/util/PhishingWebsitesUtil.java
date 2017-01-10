package net.floodlightcontroller.classifier.util;


import java.io.*;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


//fix the location of download
public class PhishingWebsitesUtil {
    FileOutputStream fos;
    String sourcefile = "./data/verified_online.json";
    String phishingfile = "./data/phishingwebsites";

    public static void main(String args[]) {
        //new Load().read();

    	PhishingWebsitesUtil update = new PhishingWebsitesUtil();
    	//update.download();
    	update.read();
    }

    public void download() {
        String key = "59af6ee5b96f07bd4a9f76cde44ef4aca8f5cab845c10a779ccc47327c6b42ee";//"f2aadbac4691e264a737b37acf154d278612f54290ab85a56b5859513e1aeb91";

        String url = "http://data.phishtank.com/data/";

        System.out.println(url + key + "/" + sourcefile);
        URL link;
        try {
            Authenticator.setDefault(
                    new Authenticator() {
                        public PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(
                                    "edcguest", "edcguest".toCharArray());
                        }
                    }
            );
            link = new URL(url + key + "/" + sourcefile);

            ReadableByteChannel rbc = Channels.newChannel(link.openStream());
            fos = new FileOutputStream(sourcefile);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void read() {
    	File file =new File(phishingfile);
		PreProccess.emptyFile(phishingfile);
        try {
        	BufferedReader reader = new BufferedReader(new FileReader(sourcefile));
        	BufferedWriter writer = new BufferedWriter(new FileWriter(file,true));  
    		
            Pattern pattern = Pattern.compile("\"url\":\"([^\"]*)");
            
            String line;

            while ((line = reader.readLine()) != null) {

            	Matcher m = pattern.matcher(line);
                while (m.find()) {
                    String url = m.group(1);
                    url = parseUrl(url);
                    URL u = new URL(url);
                    
                    writer.write(u.getHost());
                    writer.newLine();
                }
            }
            reader.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String parseUrl(String url) {
        String newurl = url.replaceAll("\\\\", "");//.replaceFirst("://www.", "://"); //去掉www
        return newurl;

    }
}
