package tw.com.lightnovel.classes;

import org.json.JSONException;
import org.json.JSONObject;

import tw.com.lightnovel.apis.IJsonSerilizable;
import tw.com.lightnovel.core.ApplicationContext;
import tw.com.lightnovel.logging.ILogger;

public class VolumeDescription implements IJsonSerilizable {

	public static final String BEGIN_TITLE_TAG = "<td width=\"200\">";
	public static final String END_TITLE_TAG = "</td>";

	public static final String BEGIN_AUTHOR_TAG = "<td>";
	public static final String END_AUTHOR_TAG = "</td>";

	public static final String BEGIN_ICONOGRAPH_TAG = "</td><td>";
	public static final String END_ICONOGRAPH_TAG = "</td>";

	public static final String BEGIN_PUBLISH_TAG = "</td><td>";
	public static final String END_PUBLISH_TAG = "</td>";

	public static final String BEGIN_VIEW_TAG = "</td><td>";
	public static final String END_VIEW_TAG = "</td>";

	public static final String BEGIN_UPDATE_TAG = "</td><td>";
	public static final String END_UPDATE_TAG = "</td>";

	public static final String BEGIN_DESCRIPTION_TAG = "\"text-indent: 2em;\">";
	public static final String END_DESCRIPTION_TAG = "</p>";

	private final String mName;
	private final String mAuthor;
	private final String mIconograph;
	private final String mPublish;
	private final String mViewCount;
	private final String mUpdateDateTime;
	private final String mDescription;

	private static final String NAME_TAG = "name";
	private static final String AUTHOR_TAG = "author";
	private static final String ICONOGRAPH = "iconograph";
	private static final String PUBLISH_TAG = "publish";
	private static final String VIEW_COUNT_TAG = "viewcount";
	private static final String UPDATE_DATE_TIME_TAG = "updatedatetime";
	private static final String DESCRIPTION_TAG = "description";

	private ILogger mLogger = ApplicationContext.Logger
			.forType(VolumeDescription.class);

	public VolumeDescription(String name, String author, String iconograph,
			String publish, String view, String updateDateTime,
			String description) {
		mName = name;
		mAuthor = author;
		mIconograph = iconograph;
		mPublish = publish;
		mViewCount = view;
		mUpdateDateTime = updateDateTime;
		mDescription = description;
	}

	public String getName() {
		return mName;
	}

	public String getAuthor() {
		return mAuthor;
	}

	public String getViewCount() {
		return mViewCount;
	}

	public String getIconograph() {
		return mIconograph;
	}

	public String getPublish() {
		return mPublish;
	}

	public String getUpdateDateTime() {
		return mUpdateDateTime;
	}

	public String getDescription() {
		return mDescription;
	}
	
	public static VolumeDescription fromJson(String json) throws JSONException {
		JSONObject jObject = new JSONObject(json);
		
		return fromJson(jObject);
	}

	public static VolumeDescription fromJson(JSONObject json)
			throws JSONException {
		return new VolumeDescription(json.getString(NAME_TAG),
				json.getString(AUTHOR_TAG), json.getString(ICONOGRAPH),
				json.getString(PUBLISH_TAG), json.getString(VIEW_COUNT_TAG),
				json.getString(UPDATE_DATE_TIME_TAG),
				json.getString(DESCRIPTION_TAG));
	}

	public JSONObject toJson() {
		JSONObject json = new JSONObject();

		try {
			json.put(NAME_TAG, mName);
			json.put(AUTHOR_TAG, mAuthor);
			json.put(ICONOGRAPH, mIconograph);
			json.put(PUBLISH_TAG, mPublish);
			json.put(VIEW_COUNT_TAG, mViewCount);
			json.put(UPDATE_DATE_TIME_TAG, mUpdateDateTime);
			json.put(DESCRIPTION_TAG, mDescription);

		} catch (Exception e) {
			mLogger.error(e, "fail to generate json string");
		}

		return json;
	}

	@Override
	public String toString() {
		return toJson().toString();
	}
}
