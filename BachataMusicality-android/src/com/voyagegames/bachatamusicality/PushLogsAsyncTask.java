package com.voyagegames.bachatamusicality;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class PushLogsAsyncTask extends AsyncTask<Void, Void, Void> {
	
	private static final String TAG = PushLogsAsyncTask.class.getName();
	
	final Context context;
	
	public PushLogsAsyncTask(final Context context) {
		this.context = context;
	}

	@Override
	protected Void doInBackground(final Void... params) {
		try {
			if (!Tracking.instance(context).dispatch()) {
				Log.e(TAG, "Failed to dispatch");
			}
		} catch (final Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
		
        return null;
	}

}
