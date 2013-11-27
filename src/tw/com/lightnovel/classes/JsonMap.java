package tw.com.lightnovel.classes;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import tw.com.lightnovel.core.ApplicationContext;
import tw.com.lightnovel.logging.ILogger;

public class JsonMap extends HashMap<String, String> {
	private final ILogger mLogger = ApplicationContext.Logger
			.forType(JsonMap.class);

	public void add(String key, String value) {
		this.put(key, value);
	}

	public String toJsonString() {
		JSONObject jObject = new JSONObject();

		try {
			for (String key : this.keySet()) {				
				jObject.put(key, this.get(key));
			}
		} catch (JSONException e) {
			mLogger.error(e, "fail to cover json map to json string");
		}

		return jObject.toString();
	}
}
