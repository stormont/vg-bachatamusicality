package com.voyagegames.bachatamusicality;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.google.analytics.tracking.android.GAServiceManager;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.voyagegames.java.tracking.AppSetting;
import com.voyagegames.java.tracking.BasicAccumulator;
import com.voyagegames.java.tracking.CustomEvent;
import com.voyagegames.java.tracking.JsonDispatcher;
import com.voyagegames.java.tracking.JsonDispatcherConfig;
import com.voyagegames.java.tracking.Tracker;
import com.voyagegames.java.tracking.UserAction;
import com.voyagegames.java.tracking.UserView;

public class Tracking extends Tracker<BasicAccumulator, JsonDispatcher> {
	
	private static final String TAG = Tracking.class.getName();
	
	private static Tracking INSTANCE;
	private static GoogleAnalytics mGaInstance;
	private static com.google.analytics.tracking.android.Tracker mGaTracker;
	private static Map<String, Long> mTimings = new HashMap<String, Long>();

	public static Tracking instance(final Context context) {
		if (INSTANCE == null) {
			final JsonDispatcherConfig config = new JsonDispatcherConfig();
			config.callback = new DispatchCallback();
			config.url = UrlConfig.logUrl();
			config.maxUploadSize = 0;
			
			if (config.url.startsWith(UrlConfig.DEBUG_HOST)) {
				config.useCompression = false;
				config.debugMode = true;
			}
			
			final PackageManager manager = context.getPackageManager();
			
			try {
				final PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);

				config.trackingHeader.add(new AppSetting("PackageName", info.packageName));
				config.trackingHeader.add(new AppSetting("VersionCode", String.valueOf(info.versionCode)));
				config.trackingHeader.add(new AppSetting("VersionName", info.versionName));
			} catch (final NameNotFoundException e) {
				Log.e(TAG, e.getMessage(), e);
				config.trackingHeader.add(new AppSetting("PackageName", "No package information available"));
			}
			
			mGaInstance = GoogleAnalytics.getInstance(context);
			mGaInstance.setDebug(config.debugMode);
			mGaTracker = mGaInstance.getTracker("UA-41937623-3");
			
			INSTANCE = new Tracking(new BasicAccumulator(), new JsonDispatcher(config));
		}
		
		return INSTANCE;
	}
	
	private Tracking(final BasicAccumulator accumulator, final JsonDispatcher dispatcher) {
		// private to force singleton
		super(accumulator, dispatcher);
	}

	@Override
	public boolean dispatch() {
		GAServiceManager.getInstance().dispatch();
		return super.dispatch();
	}
	
	@Override
	public void trackCustomEvent(final CustomEvent data) {
		super.trackCustomEvent(data);
		
		if (data.key.endsWith(".timing")) {
			if (data.value.contentEquals("start")) {
				mTimings.put(data.key, Long.valueOf(data.timestamp));
			} else if (data.value.contentEquals("stop")) {
				if (mTimings.containsKey(data.key)) {
					final long start = mTimings.get(data.key);
					final long end = data.timestamp;
					mGaTracker.sendTiming("timing", end - start, data.key, data.key);
				}
			}
		}
	}

	@Override
	public void trackUserAction(final UserAction data) {
		super.trackUserAction(data);
		mGaTracker.sendEvent("ui_action", data.value, data.key, 0L);
	}

	@Override
	public void trackUserView(final UserView data) {
		super.trackUserView(data);
		mGaTracker.sendView(data.key);
	}

}
