package com.voyagegames.bachatamusicality;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.voyagegames.bachatamusicality.Common.LogLevel;

public class LogRetrieveServlet extends AbstractLoggingServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8307345857703656723L;
	private static final String TAG = LogRetrieveServlet.class.getName();
	
	public void doGet(final HttpServletRequest req, final HttpServletResponse resp)
			throws IOException {
    	try {
    		logLevel = LogLevel.INFO;
			log(TAG, "/log/retrieve GET");
			
			final UserService userService = UserServiceFactory.getUserService();
			final User user = userService.getCurrentUser();

	        if (user == null) {
	            resp.sendRedirect(userService.createLoginURL(req.getRequestURI()));
	            return;
	        } else {
	        	final String userId = user.getEmail();
	        	
	        	if (!userId.contentEquals("stormont@gmail.com") && !userId.contentEquals("voyagegamesllc@gmail.com")) {
		            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
		        	return;
	        	}

	    		logLevel = LogLevel.WARNING;
	        	log(TAG, "User accessing logs: " + userId);
	        }

			final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
			final Query query = new Query("Logs").addSort("date", Query.SortDirection.DESCENDING);
	        final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		    final List<Entity> logs = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(100));
	        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        final Map<String, Integer> names = new HashMap<String, Integer>();
	        
	        int zippedEntries = 0;
		    
		    if (logs.size() > 0) {
		        final ZipOutputStream zos = new ZipOutputStream(baos);
		
			    for (final Entity e : logs) {
			    	try {
				    	final String date = dateFormat.format(e.getProperty("date"));
				    	final Blob blob = (Blob) e.getProperty("value");
				    	final byte[] data = blob.getBytes();
				    	
				    	int count = 1;
				    	
				    	if (names.containsKey(date)) {
				    		count = names.get(date) + 1;
				    	}
				    	
				    	names.put(date, count);
				    	zipLogAsFile(zos, Utilities.decompress(data), date + "_" + count + ".log");
				        
				        zos.flush();
				        baos.flush();
				        ++zippedEntries;
				        datastore.delete(e.getKey());
			    	} catch (final Exception logExc) {
			    		logLevel = LogLevel.WARNING;
			    		log(TAG, "Failed to add log to zip file", logExc);
			    	}
			    }
			    
		        zos.close();
		    }

	        baos.close();
	        
	        if (zippedEntries > 0) {
		        resp.setContentType("application/zip");
	
	            final String now = dateFormat.format(System.currentTimeMillis());
	            resp.setHeader("Content-Disposition", "attachment; filename=\"" + now + "_logs.zip\"");
		 
		        final ServletOutputStream sos = resp.getOutputStream();
		        final byte[] zip = baos.toByteArray();
		        
	            sos.write(zip);
	            sos.flush();
	        }
	    } catch (final Exception exc) {
    		logLevel = LogLevel.ERROR;
    		log(TAG, "Log retrieval failed", exc);
			final PrintWriter out = resp.getWriter();
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	        out.print("Server error");
    		logLevel = LogLevel.ERROR;
    	}
	}

    private void zipLogAsFile(final ZipOutputStream zos, final byte[] log, final String name) {
        final byte bytes[] = new byte[4096];
        final ByteArrayInputStream bis = new ByteArrayInputStream(log);
 
        int bytesRead;

        try {
	        zos.putNextEntry(new ZipEntry(name));
	 
	        while ((bytesRead = bis.read(bytes)) != -1) {
	            zos.write(bytes, 0, bytesRead);
	        }
	        
	        zos.closeEntry();
	        bis.close();
        } catch (final IOException e) {
    		logLevel = LogLevel.WARNING;
        	log(TAG, "Failed to zip log", e);
        }
        
        return;
    }
	
}
