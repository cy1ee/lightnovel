package tw.com.lightnovel.apis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import tw.com.lightnovel.classes.ListRowData;
import tw.com.lightnovel.core.ApplicationContext;
import tw.com.lightnovel.logging.ILogger;

public class JsonHelper {
	private static ILogger mLogger = ApplicationContext.Logger
			.forType(JsonHelper.class);

	public static Map<String, ListRowData> toMap(String json) {
		Map<String, ListRowData> map = new HashMap<String, ListRowData>();

		try {
			JSONObject jObject = new JSONObject(json);
			Iterator<String> iter = jObject.keys();

			while (iter.hasNext()) {
				String key = iter.next();
				String value = (String) jObject.get(key);
				ListRowData item = ListRowData.fromJson(value);
				map.put(key, item);
			}
		} catch (JSONException e) {
			mLogger.error(e, "fail to recover map from json [" + json + "]");
		}

		return map;
	}

	public static List<ListRowData> toList(String json) {
		Map<String, ListRowData> map = toMap(json);
		List<ListRowData> list = new ArrayList<ListRowData>();

		if (map != null) {
			for (String key : map.keySet()) {
				list.add(map.get(key));
			}
		}

		return list;
	}
}
