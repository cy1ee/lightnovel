package tw.com.lightnovel.classes;

import org.json.JSONException;
import org.json.JSONObject;

import tw.com.lightnovel.core.ApplicationContext;
import tw.com.lightnovel.logging.ILogger;

public class NovelDescription {
	public static final String BEGIN_AUTHOR_TAG = "<td width=\"120\">";
	public static final String END_AUTHOR_TAG = "</td>";

	public static final String BEGIN_ICONOGRAPH_TAG = "<td width=\"120\">";
	public static final String END_ICONOGRAPH_TAG = "</td>";

	public static final String BEGIN_PUBLISH_TAG = "<td>";
	public static final String END_PUBLISH_TAG = "</td>";

	public static final String BEGIN_VIEW_TAG = "<td>";
	public static final String END_VIEW_TAG = "</td>";

	public static final String BEGIN_UPDATE_TAG = "<td colspan=\"3\">";
	public static final String END_UPDATE_TAG = "</td>";

	public static final String BEGIN_LATEST_TAG = "<td colspan=\"3\">";
	public static final String END_LATEST_TAG = "</td>";

	public static final String BEGIN_DUMMY_TAG = "<td>";
	public static final String END_DUMMY_TAG = "</td>";

	public static final String BEGIN_DESCRIPTION_TAG = "<p>";
	public static final String END_DESCRIPTION_TAG = "</p>";

	public static final String AUTHOR_TAG = "author";
	public static final String ICONOGRAPH_TAG = "iconograph";
	public static final String PUBLISH_TAG = "publish";
	public static final String VIEWCOUNT_TAG = "viewcount";
	public static final String UPDATEDATETIME_TAG = "updatedatetime";
	public static final String DESCRIPTION_TAG = "description";

	private static ILogger mLogger = ApplicationContext.Logger
			.forType(NovelDescription.class);
	private final String mAuthor;
	private final String mIconograph;
	private final String mPublish;
	private final String mViewCount;
	private final String mUpdateDateTime;
	private final String mDescription;

	public NovelDescription(String author, String iconograph, String publish,
			String view, String updateDateTime, String description) {
		mAuthor = author;
		mIconograph = iconograph;
		mPublish = publish;
		mViewCount = view;
		mUpdateDateTime = updateDateTime;
		mDescription = description;
	}

	public String getAuthor() {
		return mAuthor;
	}

	public String getViewCount() {
		return mViewCount;
	}

	public String getUpdateDateTime() {
		return mUpdateDateTime;
	}

	public String getDescription() {
		return mDescription;
	}

	public JSONObject toJsonObject() {
		JSONObject jObject = new JSONObject();

		try {
			jObject.put(AUTHOR_TAG, mAuthor);
			jObject.put(ICONOGRAPH_TAG, mIconograph);
			jObject.put(PUBLISH_TAG, mPublish);
			jObject.put(VIEWCOUNT_TAG, mViewCount);
			jObject.put(UPDATEDATETIME_TAG, mUpdateDateTime);
			jObject.put(DESCRIPTION_TAG, mDescription);
		} catch (JSONException e) {
			mLogger.error(e, "fail to create json object");
		}

		return jObject;
	}
	
	public static NovelDescription fromJson(JSONObject json) {
		NovelDescription novelDescription = null;
		
		try {
			
			String author = json.getString(AUTHOR_TAG);
			String iconograph = json.getString(ICONOGRAPH_TAG);
			String publish = json.getString(PUBLISH_TAG);
			String view = json.getString(VIEWCOUNT_TAG);
			String updateDateTime = json.getString(UPDATEDATETIME_TAG);
			String description = json.getString(DESCRIPTION_TAG);
			
			novelDescription = new NovelDescription(author, iconograph, publish, view, updateDateTime, description);
		} catch (JSONException e) {
			mLogger.error(e, "fail to create novel description from json object");
		}
		
		return novelDescription;
	}
	
	public static NovelDescription fromJson(String json) {
		NovelDescription novelDescription = null;
		
		try {
			JSONObject jObject = new JSONObject(json);
			novelDescription = fromJson(jObject);
		} catch (JSONException e) {
			mLogger.error(e, "fail to create novel description from json string");
		}
		
		return novelDescription;
	}

	@Override
	public String toString() {
		return "Author: " + mAuthor + " Iconograph: " + mIconograph
				+ " publish: " + mPublish + " view: " + mViewCount
				+ " updateDateTime: " + mUpdateDateTime + " description: "
				+ mDescription;
	}
}
