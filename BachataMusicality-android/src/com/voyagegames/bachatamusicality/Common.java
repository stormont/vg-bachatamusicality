package com.voyagegames.bachatamusicality;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Common {
	
	public static final int CONN_TIMEOUT_MS = 15000;
	public static final int READ_TIMEOUT_MS = 15000;

	private static final String TAG = Common.class.getName();
	
	public static String urlEncode(final ILogger logger, final String value) {
		try {
			return URLEncoder.encode(value, "ISO-8859-1");
		} catch (final UnsupportedEncodingException e) {
			logger.log(TAG, e.getMessage(), e);
			return "";
		}
	}

}
