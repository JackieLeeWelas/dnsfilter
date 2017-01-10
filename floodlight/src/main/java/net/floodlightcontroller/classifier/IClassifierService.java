package net.floodlightcontroller.classifier;

//import java.io.File;

import net.floodlightcontroller.core.module.IFloodlightService;

public interface IClassifierService extends IFloodlightService {
	
	public String extractFeatures(String sourceFile,String featureFile,String label); //提取原始数据的特征，形成特征向量，返回存放特征向量的文件名
	//public String extractDomainFeature(String domain);  //提取待预测的域名的特征，返回特征字符串
	public String train(String trainfile,String modelfile);    //,String modelfile输入文件名进行训练，文件存放了特征向量，输出模型文件
	public String test(String testfile,String modelfile,String resultfile);   //测试，
	public String scale(String sourcefile, String scaledfile);  //归一化数据
	public double predict(String domain);  //预测，根据域名来预测类别，1 表示恶意域名，-1 表示良性域名
}
