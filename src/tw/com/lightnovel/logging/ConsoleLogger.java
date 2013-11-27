package tw.com.lightnovel.logging;

import android.util.Log;

public class ConsoleLogger implements ILogger {
	private final String mName;

	public <T> ILogger forType(Class<T> type) {
		return forType(type.getSimpleName());
	}

	public ILogger forType(String name) {
		return new ConsoleLogger(name);
	}

	public void error(Exception e, String message) {
		Log.e(mName, e.getMessage() + ": " + message);
		
	}

	public void log(String message) {
		Log.d(mName, message);
	}

	public ConsoleLogger(String name) {
		mName = name;
	}
	
	public ConsoleLogger() {
		mName = "Console Logger";
	}
}
