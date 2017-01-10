package net.floodlightcontroller.classifier.util;

import net.floodlightcontroller.classifier.Classifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtil {

	protected static Logger logger;
	
	public static void log(String messages){
		logger = LoggerFactory.getLogger(Classifier.class);
		logger.info(messages);
	}
	
	public static void error(String messages){
		logger = LoggerFactory.getLogger(Classifier.class);
		logger.error(messages);
	}
}
