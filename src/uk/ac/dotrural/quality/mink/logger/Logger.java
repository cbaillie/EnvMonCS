package uk.ac.dotrural.quality.mink.logger;

public class Logger {
	
	/**
	 * Spit debug info to terminal
	 * @param msg The message to display
	 */
	public static void info(String cls, String msg)
	{
		System.out.println("[" + cls + "][INFO] " + msg);
	}
	
	/** 
	 * Spit debug errors to terminal
	 * @param msg The message to display
	 */
	public static void error(String cls, String msg)
	{
		System.err.println("[" + cls + "][ERROR] " + msg);
	}

}
