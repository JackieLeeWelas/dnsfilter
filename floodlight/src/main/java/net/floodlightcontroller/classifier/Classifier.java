package net.floodlightcontroller.classifier;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFType;

import net.floodlightcontroller.classifier.util.FeaturesUtil;
import net.floodlightcontroller.classifier.util.LogUtil;
import net.floodlightcontroller.classifier.util.PreProccess;
import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.restserver.IRestApiService;

import java.util.ArrayList;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;

public class Classifier implements IFloodlightModule, IOFMessageListener,
		IClassifierService {

	protected IFloodlightProviderService floodlightProvider;
	protected IRestApiService restApi;
	//protected static Logger logger = LoggerFactory.getLogger(Classifier.class);
	protected svm_node[][] datas;
	protected String trainfile = "./data/trainset.vector"; // 存放训练样本
	protected String testfile = "./data/testset.vector"; // 存放测试样本
	protected String modelfile = "./data/train.model"; // 存放模型文件
	protected String resultfile = "./data/test.result"; // 存放测试结果文件
	protected String train_scaledfile = "./data/train.scale"; //缩放后的训练特征
	protected String test_scaledfile = "./data/test.scale"; //缩放后的测试特征

	@Override
	public void init(FloodlightModuleContext context)
			throws FloodlightModuleException {
		// TODO Auto-generated method stub
		floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
		restApi = context.getServiceImpl(IRestApiService.class);
		//提取训练域名数据集特征
//		PreProccess.emptyFile(trainfile);
//		FeaturesUtil.extractFeatures("./data/blacklist",trainfile,"1");
		//FeaturesUtil.extractFeatures("./data/whitelist",trainfile,"-1"); 
		
//		String args[] = new String[]{"-l","0",trainfile};
//		try {
//			Svm_scale.main(args);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		//scale(trainfile, train_scaledfile);
		//scale(testfile, test_scaledfile);
		//训练
		//train(trainfile,modelfile); // 训练的结果存放在modelfile中
		
		//提取测试域名的特征
//		PreProccess.emptyFile(testfile);
//		FeaturesUtil.extractFeatures("./data/testbaddomain",testfile,"1");
//		FeaturesUtil.extractFeatures("./data/testgooddomain",testfile,"-1");
		//测试
		test(testfile, modelfile, resultfile);
		//归一化后测试
//		test(test_scaledfile, modelfile, resultfile);
		
		
		//预测

		predict("www.google.com");
		predict("www.qq.com");
		predict("cqu.edu.cn");
		predict("www.hallwaystreetsigns.com");
		predict("lovemura.com");
		predict("moto-agro.pl");
		
		//System.out.println(FeaturesUtil.extractDomainFeature("www.baidu.com"));
		System.exit(1);
	}
	
	private static double atof(String s) {
		return Double.valueOf(s).doubleValue();
	}

	private static int atoi(String s) {
		return Integer.parseInt(s);
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "classifier";
	}

	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public net.floodlightcontroller.core.IListener.Command receive(
			IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		// TODO Auto-generated method stub
		Collection<Class<? extends IFloodlightService>>l=
				new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IClassifierService.class);
		return l;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		// TODO Auto-generated method stub
		Map<Class<? extends IFloodlightService>, IFloodlightService>m=
				new HashMap<Class<? extends IFloodlightService>, IFloodlightService>();
		m.put(IClassifierService.class, this);
		return m;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		// TODO Auto-generated method stub
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IFloodlightProviderService.class);
		l.add(IRestApiService.class);
		return l;
	}

	@Override
	public void startUp(FloodlightModuleContext context)
			throws FloodlightModuleException {
		// TODO Auto-generated method stub
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
		restApi.addRestletRoutable(new ClassifierWebRoutable());

	}
	
	/**
	 *  提取特征
	 */
	@Override
	public String extractFeatures(String sourcefile,String featurefile,String label){
		return FeaturesUtil.extractFeatures(sourcefile, featurefile, label);
	}

	/**
	 *  训练
	 */
	@Override
	public String train(String trainfile,String modelfile) {
		// TODO Auto-generated method stub
		String[] arg = {"-h","0","-v","10",trainfile, // 存放SVM训练模型用的数据的路径
				modelfile}; // 存放SVM通过训练数据训练出来的模型的路径
		System.out.println("........SVM train begin..........");
		try {
			Svm_train.main(arg); // 训练
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			LogUtil.log(e.getMessage());
			return null;
		}
		this.modelfile = modelfile;
		return modelfile;
	}
	
	/**
	 *   测试
	 */
	@Override
	public String test(String testfile, String modelfile, String resultfile) {
		// TODO Auto-generated method stub
		String[] parg = {testfile, // 存放测试数据
				modelfile, // 调用的是训练以后的模型
				resultfile }; // 生成结果的文件的路径
		try {
			Svm_predict.main(parg); // 预测
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			LogUtil.log("测试失败..." + e.getMessage());
			return null;
		}
		return resultfile;
	}

	/**
	 *   预测
	 */
	@Override
	public double predict(String domain) {
		// TODO Auto-generated method stub

		svm_model model = null;
		try {
			model = svm.svm_load_model(this.modelfile);
			if (model == null) {
				System.err.print("can't open model file " + this.modelfile + "\n");
				System.exit(1);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 从域名中提取出特征数组
		String features = FeaturesUtil.extractDomainFeature(domain);
		LogUtil.log("to predicted domain \'"+domain+"\' feature:"+features);
		StringTokenizer st = new StringTokenizer(features, " \t\n\r\f:");

		int m = st.countTokens() / 2;
		svm_node[] x = new svm_node[m];
		for (int j = 0; j < m; j++) {
			x[j] = new svm_node();
			x[j].index = atoi(st.nextToken());
			x[j].value = atof(st.nextToken());
		}

		double v;
		v = svm.svm_predict(model, x);
		String result = (v == 1.0) ? "Malicious" : "Benign";
		LogUtil.log("the domain name: \"" + domain + "\" is classified to " + result);
		return v;
	}
	
	/**
	 * 归一化数据
	 * @param trainfile
	 * @param scaledfile
	 * @return
	 */
	@Override
	public String scale(String sourcefile, String scaledfile){
		String[] parg1 = { "-l", "0", "-u","1",//缩放范围，特征值为0过多时设置该项
				"-f",scaledfile,   //缩放之后的特征向量
				sourcefile}; // 训练特征向量集
		
		String[] parg2 = { "-r",scaledfile,   //缩放之后的特征向量
				sourcefile}; // 训练特征向量集
         
		try {
			Svm_scale.main(parg1);
			//Svm_scale.main(parg2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return scaledfile;
	}

}
