package tw.com.lightnovel.logging;

public interface ILogger {	
	<T> ILogger forType(Class<T> type);
	
	ILogger forType(String name);

	void error(Exception e, String message);

	void log(String message);
}
