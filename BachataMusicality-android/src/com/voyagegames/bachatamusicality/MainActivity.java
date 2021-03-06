package com.voyagegames.bachatamusicality;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.voyagegames.java.tracking.AppEvent;
import com.voyagegames.java.tracking.CustomEvent;
import com.voyagegames.java.tracking.CustomSetting;
import com.voyagegames.java.tracking.UserAction;
import com.voyagegames.java.tracking.UserView;
import com.voyagegames.java.tracking.Utilities;

public class MainActivity extends AndroidApplication implements IHttpRequestCallback<String> {

	private AlertDialog updateAppDialog;
	private MainGdxGame game;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		
		try {
			Tracking.instance(this).trackAppEvent(new AppEvent("MainActivity.activity.start", "onCreate"));
			new HttpRequestAsyncTask(this, this).execute(new String[] { UrlConfig.infoUrl() });
	        
	        final AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
	        cfg.useGL20 = false;
	        cfg.hideStatusBar = false;
	        
	        game = new MainGdxGame();
	        initialize(game, cfg);
	        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		} catch (final Exception e) {
			Tracking.instance(this).trackAppEvent(
					new AppEvent("MainActivity.onCreate", Utilities.exceptionToString(e)));
			Toast.makeText(this, getResources().getString(R.string.common_problem), Toast.LENGTH_SHORT).show();
			finish();
		}
    }

	@Override
	protected void onPause() {
		super.onPause();
		if (game != null) game.pause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Tracking.instance(this).trackUserView(new UserView("MainActivity.onResume", "view"));
		if (game != null) game.resume();
	}

	@Override
	protected void onDestroy() {
		try {
			Tracking.instance(this).trackAppEvent(new AppEvent("MainActivity.onDestroy", "onDestroy"));
			new PushLogsAsyncTask(this).execute(new Void[] {});
	    } catch (final Exception e) {
	    	Log.e("GuidedSelectionActivity", "onDestroy exception", e);
	    }
		
		super.onDestroy();
	}

	@Override
	public void onPostExecute(final String result) {
		Tracking.instance(this).trackCustomEvent(
				new CustomEvent("MainActivity.onPostExecute", "info"));
		Tracking.instance(this).trackCustomSetting(
				new CustomSetting("MainActivity.info", result));
		
		try {
			final int versionCode = getApplicationContext().getPackageManager().getPackageInfo(
					this.getApplicationInfo().packageName, 0).versionCode;
			final int latestVersionCode = Integer.parseInt(result);
			
			if (versionCode >= latestVersionCode) {
				return;
			}
			
			promptToDownloadLatest();
		} catch (final Exception e) {
			Tracking.instance(this).trackAppEvent(
					new AppEvent("MainActivity.onPostExecute", Utilities.exceptionToString(e)));
		}
	}
	
	@Override
	public void onCancelled() {
		Tracking.instance(this).trackCustomEvent(
				new CustomEvent("MainActivity.onCancelled", "info"));
	}

	@Override
	public void onBackPressed() {
		// Capture back button; handle through libGdx
		//super.onBackPressed();
	}

	@Override
	public void run() {}

	@Override
	public void onProgressUpdate(String... values) {}

	private void promptToDownloadLatest() {
		Tracking.instance(this).trackUserView(new UserView("MainActivity.promptToDownloadLatest", "view"));
		updateAppDialog = new AlertDialog.Builder(this)
			.setMessage(R.string.update_available_prompt)
			.setPositiveButton(R.string.update_available_yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(final DialogInterface dialog, final int id) {
					try {
						final Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse("market://details?id=com.voyagegames.bachatamusicality"));
						startActivity(intent);
						Tracking.instance(MainActivity.this).trackUserAction(
								new UserAction("MainActivity.promptToDownloadLatest", "update"));
						game.pause();
					} catch (final Exception e) {
						Tracking.instance(MainActivity.this).trackAppEvent(
								new AppEvent(
										"MainActivity.promptToDownloadLatest",
										Utilities.exceptionToString(e)));
					}
				}
			})
			.setNegativeButton(R.string.update_available_no, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(final DialogInterface dialog, final int id) {
					updateAppDialog.cancel();
					Tracking.instance(MainActivity.this).trackUserAction(
							new UserAction("MainActivity.promptToDownloadLatest", "ignore"));
				}
			})
			.create();
        
		updateAppDialog.show();
	}

}
