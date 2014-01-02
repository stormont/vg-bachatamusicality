package com.voyagegames.bachatamusicality;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpRequest {
	
	public static String request(final String urlString, final int connectTimeoutMs, final int readTimeoutMs) throws MalformedURLException, IOException {
		final URL url = new URL(urlString);
		final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		
		if (connectTimeoutMs > 0) {
			conn.setConnectTimeout(connectTimeoutMs);
		}
		
		if (readTimeoutMs > 0) {
			conn.setReadTimeout(readTimeoutMs);
		}
		
		try {
			final InputStream in = new BufferedInputStream(conn.getInputStream());
			
			try {
				final BufferedInputStream bin = new BufferedInputStream(in);
				
				try {
					final byte[] contents = new byte[1024];
					final StringBuilder builder = new StringBuilder();
	
					int bytesRead = 0;
	
					while ((bytesRead = bin.read(contents)) != -1) {
						builder.append(new String(contents, 0, bytesRead));               
					}
	
					return builder.toString();
				} finally {
					bin.close();
				}
			} finally {
				in.close();
			}
		} finally {
			conn.disconnect();
		}
	}

}
