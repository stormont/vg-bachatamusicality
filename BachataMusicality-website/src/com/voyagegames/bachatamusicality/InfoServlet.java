package com.voyagegames.bachatamusicality;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.voyagegames.bachatamusicality.Common.LogLevel;

public class InfoServlet extends AbstractLoggingServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6872474105542092453L;
	private static final String TAG = InfoServlet.class.getName();
	private static final String VERSION_CODE = "2";

	public void doGet(final HttpServletRequest req, final HttpServletResponse resp)
			throws IOException {
    	try {
    		logLevel = LogLevel.INFO;
			log(TAG, "/v1/info GET");
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.getWriter().print(VERSION_CODE);
	    } catch (final Exception exc) {
    		log(TAG, "/v1/info failed", exc);
			final PrintWriter out = resp.getWriter();
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	        out.print("Server error");
    		logLevel = LogLevel.ERROR;
			log(TAG, "Server error");
    	}
	}

}
