package com.voyagegames.bachatamusicality;

public class UrlConfig {

	public static final String RELEASE_HOST = "http://bachatamusicality.appspot.com/";
	public static final String DEBUG_HOST = "http://localhost:8888/";
	public static final String EMULATOR_HOST = "http://10.0.2.2:8888/";
	
	private static final String LOG_REQUEST = "v1/log";
	private static final String INFO_REQUEST = "v1/info";
	
	private static String ACTIVE_HOST = RELEASE_HOST;
	
	public static String getActiveHost() {
		return ACTIVE_HOST;
	}
	
	public static void setActiveHost(final String host) {
		ACTIVE_HOST = host;
	}
	
	public static String logUrl() {
		return getActiveHost() + LOG_REQUEST;
	}
	
	public static String infoUrl() {
		return getActiveHost() + INFO_REQUEST;
	}

}
