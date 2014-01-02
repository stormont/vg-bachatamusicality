package com.voyagegames.bachatamusicality;

public interface IHttpRequestCallback <T> {
	
	public void run();
	public void onCancelled();
	public void onProgressUpdate(String... values);
	public void onPostExecute(T result);

}
