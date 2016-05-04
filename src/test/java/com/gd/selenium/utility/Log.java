package com.gd.selenium.utility;

import java.io.File;

import org.apache.log4j.Logger;

public class Log {

	//Initialize Log4j logs
	private static Logger Log = getLoggerAfterSavingOldLogFile();
	
	private static Logger getLoggerAfterSavingOldLogFile(){
		File logFile = new File("./test.log");
		String logFileName = logFile.getName();
		int dotIndex = logFileName.indexOf(".");
		String purgedName = logFileName.substring(0, dotIndex) + "-" + System.currentTimeMillis() + logFileName.substring(dotIndex);
	    File purgedLogFile = new File(logFile.getParentFile() + "\\" + purgedName);
	    logFile.renameTo(purgedLogFile);
		return Logger.getLogger(Log.class.getName());
	}

	// This is to print log for the beginning of the test case, as we usually run so many test cases as a test suite
	public static void startTestCase(String testCaseName){
		Log.info("****************************************************************************************");
		Log.info("****************************************************************************************");
		Log.info("$$$$$$$$$$$$$$$$$$$$$                 "+testCaseName+ "       $$$$$$$$$$$$$$$$$$$$$$$$$");
		Log.info("****************************************************************************************");
		Log.info("****************************************************************************************");
	}

	//This is to print log for the ending of the test case
	public static void endTestCase(String testCaseName){
		Log.info("XXXXXXXXXXXXXXXXXXXXXXX             --- "+testCaseName+" execution complete---"+"             XXXXXXXXXXXXXXXXXXXXXX");
		Log.info("X");
		Log.info("X");
		Log.info("X");
		Log.info("X");
	}

	// Need to create these methods, so that they can be called  
	public static void info(String message) {
		Log.info(message);
	}

	public static void warn(String message) {
		Log.warn(message);
	}

	public static void error(String message) {
		Log.error(message);
	}

	public static void fatal(String message) {
		Log.fatal(message);
	}

	public static void debug(String message) {
		Log.debug(message);
	}
}
