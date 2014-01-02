package com.voyagegames.bachatamusicality;

import java.io.UnsupportedEncodingException;

import android.util.Log;

import com.voyagegames.java.tracking.IDispatchCallback;

public class DispatchCallback implements IDispatchCallback {

	@Override
	public void debugTrack(final byte[] output) {
		System.out.print(output);
		
		try {
			Log.e("Tracking", new String(output, "UTF-8"));
		} catch (final UnsupportedEncodingException e) {
			Log.e("DispatchCallback", "Encoding failed", e);
		}
	}

	@Override
	public void error(final String error) {
		Log.e("Tracking", error);
	}
	
}
