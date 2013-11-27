package tw.com.lightnovel.core;

import android.app.Application;
import android.content.Context;

public class SubApplication extends Application {
	private static Context mContext;

	public static Context getContext() {
		return mContext;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		mContext = this;
	}
}
