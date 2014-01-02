package com.voyagegames.bachatamusicality;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.voyagegames.bachatamusicality.Common.LogLevel;

public class LogServlet extends AbstractLoggingServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1657758436097384306L;
	private static final String TAG = LogServlet.class.getName();
	
	public void doPost(final HttpServletRequest req, final HttpServletResponse resp)
			throws IOException {
		try {
			logLevel = LogLevel.INFO;
			log(TAG, "/v1/log POST");
	
			final int contentLength = req.getContentLength();

			logLevel = LogLevel.INFO;
			log(TAG, "req.getContentLength: " + req.getContentLength());
			
			final byte[] data = new byte[contentLength];
			final int chunkSize = 4096;
			final int maxSize = 1024 * 1024;  // 1 MB limit
		
			final ServletInputStream input = req.getInputStream();
			
			int index = 0;
			
			while (index < contentLength && index < maxSize) {
				index += input.read(data, index, chunkSize);
			}
			
			input.close();
		
			final Entity logEntry = new Entity("Logs");
			logEntry.setProperty("value", new Blob(data));
			logEntry.setProperty("date", System.currentTimeMillis());
	
	        final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	        datastore.put(logEntry);
		} catch (final Exception e) {
    		logLevel = LogLevel.ERROR;
    		log(TAG, "Log failed", e);
			final PrintWriter out = resp.getWriter();
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	        out.print("Server error");
    		logLevel = LogLevel.ERROR;
		}
	}

}
