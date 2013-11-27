package tw.com.lightnovel.classes;

import org.json.JSONException;
import org.json.JSONObject;

import tw.com.lightnovel.core.ApplicationContext;
import tw.com.lightnovel.logging.ILogger;
import android.os.Parcel;
import android.os.Parcelable;

public class ListRowData implements Parcelable {
	private static final String TITLE_TAG = "title";
	private static final String IMAGE_URI_TAG = "imageuri";
	private static final String URI_TAG = "uri";

	private static ILogger mLogger = ApplicationContext.Logger
			.forType(ListRowData.class);
	private final String mTitle;
	
	private String mImageUri;
	private String mUri;

	public ListRowData(ListRowData data) {
		mTitle = data.getTitle();
		mImageUri = data.getImageUri();
		mUri = data.getUri();
	}
	
	public ListRowData(String title, String imageUrl, String novelUrl) {
		mLogger.log("title: " + title + " image url: " + imageUrl + " uri: " + novelUrl);
		mTitle = title;
		mImageUri = imageUrl;
		mUri = novelUrl;
	}

	public ListRowData(Parcel source) {
		mTitle = source.readString();
		mImageUri = source.readString();
		mUri = source.readString();
	}

	public String getTitle() {
		return mTitle;
	}

	public String getImageUri() {
		return mImageUri;
	}
	
	public void setImageUri(String uri) {
		mImageUri = uri;
	}

	public String getUri() {
		return mUri;
	}

	public void setUri(String uri) {
		mUri = uri;
	}

	@Override
	public String toString() {
		// return mTitle + "^" + mImageUrl + "^" + mNovelUrl;
		return toJson().toString();
	}

	public JSONObject toJson() {
		JSONObject jObject = new JSONObject();
		try {
			jObject.put(TITLE_TAG, mTitle);
			jObject.put(IMAGE_URI_TAG, mImageUri);
			jObject.put(URI_TAG, mUri);
		} catch (JSONException e) {
			mLogger.error(e, "fail to make json string");
		}

		return jObject;
	}

	public static ListRowData fromJson(String json) throws JSONException {
		JSONObject jObject = new JSONObject(json);
		
		return fromJson(jObject);
	}

	public static ListRowData fromJson(JSONObject json) throws JSONException {
		String title = json.getString(TITLE_TAG);
		String novelUrl = json.getString(URI_TAG);
		String imageUrl = json.getString(IMAGE_URI_TAG);

		return new ListRowData(title, imageUrl, novelUrl);
	}

	public int describeContents() {
		return this.hashCode();
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mTitle);
		dest.writeString(mImageUri);
		dest.writeString(mUri);
	}

	public static final Parcelable.Creator<ListRowData> CREATOR = new Parcelable.Creator<ListRowData>() {
		public ListRowData createFromParcel(Parcel source) {
			return new ListRowData(source);
		}

		public ListRowData[] newArray(int size) {
			return new ListRowData[size];
		}
	};
}
