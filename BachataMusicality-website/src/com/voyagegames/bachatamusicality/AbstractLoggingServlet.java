package com.voyagegames.bachatamusicality;

import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;

import com.voyagegames.bachatamusicality.Common.LogLevel;

public abstract class AbstractLoggingServlet extends HttpServlet implements ILogger {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3383110185148210943L;

	private static final Logger LOG = Logger.getLogger(AbstractLoggingServlet.class.getName());

	protected LogLevel logLevel;

	@Override
	public void log(final String tag, final String msg) {
		switch (logLevel) {
		case DEBUG:
			LOG.info(tag + " - " + msg);
			break;
		case INFO:
			LOG.info(tag + " - " + msg);
			break;
		case WARNING:
			LOG.warning(tag + " - " + msg);
			break;
		case ERROR:
			LOG.severe(tag + " - " + msg);
			break;
		case CRITICAL:
			LOG.severe(tag + " - " + msg);
			break;
		}
	}

	@Override
	public void log(final String tag, final String msg, final Exception e) {
		switch (logLevel) {
		case DEBUG:
			LOG.info(tag + " - " + msg);
			LOG.info(e.getMessage());
			break;
		case INFO:
			LOG.info(tag + " - " + msg);
			LOG.info(e.getMessage());
			break;
		case WARNING:
			LOG.warning(tag + " - " + msg);
			LOG.warning(e.getMessage());
			break;
		case ERROR:
			LOG.severe(tag + " - " + msg);
			LOG.severe(e.getMessage());
			break;
		case CRITICAL:
			LOG.severe(tag + " - " + msg);
			LOG.severe(e.getMessage());
			break;
		}

		e.printStackTrace();
	}
	
}
