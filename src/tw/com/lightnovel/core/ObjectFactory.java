package tw.com.lightnovel.core;

import tw.com.lightnovel.logging.ILogger;
import tw.com.lightnovel.parsers.BaseParser;

public class ObjectFactory<T extends BaseParser> {
	private ILogger mLogger = ApplicationContext.Logger
			.forType(ObjectFactory.class);
	private Class<T> type;

	public ObjectFactory(Class<T> type) {
		this.type = type;
	}

	public Class<T> getType() {
		return type;
	}

	public T newInstance() {
		T t = null;

		try {
			t = type.newInstance();
		} catch (InstantiationException e) {
			mLogger.error(e,
					"InstantiationException of type [" + type.toString() + "]");
		} catch (IllegalAccessException e) {
			mLogger.error(e,
					"IllegalAccessException of type [" + type.toString() + "]");
		}

		return t;
	}
}