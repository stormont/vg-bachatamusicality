package com.voyagegames.bachatamusicality;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Common {
	
	private static final String TAG = Common.class.getName();
	
	public static final int TIMEOUT_MS = 10000;
	
	public enum LogLevel {
		DEBUG,
		INFO,
		WARNING,
		ERROR,
		CRITICAL
	}
	
	public static String urlEncode(final ILogger logger, final String value) {
		try {
			return URLEncoder.encode(value, "ISO-8859-1");
		} catch (final UnsupportedEncodingException e) {
			logger.log(TAG, e.getMessage(), e);
			return "";
		}
	}
	
    public static byte[] serialize(final Object obj)
    		throws IOException {
    	final ByteArrayOutputStream b = new ByteArrayOutputStream();
    	final ObjectOutputStream o = new ObjectOutputStream(b);
    	
        o.writeObject(obj);
        return b.toByteArray();
    }
    
    public static Object deserialize(final byte[] bytes)
    		throws IOException, ClassNotFoundException {
        final ByteArrayInputStream b = new ByteArrayInputStream(bytes);
        final ObjectInputStream o = new ObjectInputStream(b);
        return o.readObject();
    }

}
