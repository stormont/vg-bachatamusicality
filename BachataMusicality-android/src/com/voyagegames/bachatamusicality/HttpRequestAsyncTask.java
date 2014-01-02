package com.voyagegames.bachatamusicality;

import android.content.Context;
import android.os.AsyncTask;

import com.voyagegames.java.tracking.AppEvent;
import com.voyagegames.java.tracking.CustomEvent;
import com.voyagegames.java.tracking.Utilities;

public class HttpRequestAsyncTask extends AsyncTask<String, String, String> implements ILogger {
	
	private static final String TAG = HttpRequestAsyncTask.class.getName();
	
	private final Context mContext;
	private final IHttpRequestCallback<String> mCallback;
	
	public HttpRequestAsyncTask(final IHttpRequestCallback<String> callback, final Context context) {
		if (callback == null) {
			throw new IllegalArgumentException("callback is null");
		}
		
		mContext = context;
		mCallback = callback;
	}

	@Override
	protected String doInBackground(final String... params) {
		if (params == null || params.length != 1) {
			throw new IllegalArgumentException("params are invalid");
		}
		
		try {
			return HttpRequest.request(params[0], Common.CONN_TIMEOUT_MS, Common.READ_TIMEOUT_MS);
		} catch (final Exception e) {
        	log(TAG, e.toString(), e);
		}
        
        return null;
	}

	@Override
	protected void onCancelled() {
		try {
			mCallback.onCancelled();
		} catch (final Exception e) {
			log(TAG, e.getMessage(), e);
		}
		
		super.onCancelled();
	}

	@Override
	protected void onPostExecute(final String result) {
		try {
			mCallback.onPostExecute(result);
		} catch (final Exception e) {
			log(TAG, e.getMessage(), e);
		}
		
		super.onPostExecute(result);
	}

	@Override
	protected void onProgressUpdate(final String... values) {
		try {
			mCallback.onProgressUpdate(values);
		} catch (final Exception e) {
			log(TAG, e.getMessage(), e);
		}
		
		super.onProgressUpdate(values);
	}

	@Override
	public void log(final String tag, final String msg) {
		Tracking.instance(mContext).trackCustomEvent(new CustomEvent("HttpRequestAsyncTask." + tag, msg));
	}

	@Override
	public void log(final String tag, final String msg, final Exception e) {
		Tracking.instance(mContext).trackCustomEvent(new CustomEvent("HttpRequestAsyncTask." + tag, msg));
		Tracking.instance(mContext).trackAppEvent(new AppEvent("HttpRequestAsyncTask." + tag, Utilities.exceptionToString(e)));
	}

}
