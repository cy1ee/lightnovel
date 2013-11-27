package tw.com.lightnovel.classes;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tw.com.lightnovel.apis.IJsonSerilizable;
import tw.com.lightnovel.core.ApplicationContext;
import tw.com.lightnovel.core.SubApplication;
import tw.com.lightnovel.database.BookmarkDatabase;
import tw.com.lightnovel.logging.ILogger;

public class Volume implements IJsonSerilizable {
	private static final String DESCRIPTION_TAG = "description";
	private static final String CHAPTER_TAG = "chapter";
	private static final String URI_TAG = "uri";
	private static final String IMAGE_URI_TAG = "imageuri";

	private final VolumeDescription mDescription;
	private final List<ListRowData> mChapters;
	private final String mNovelUri;

	private ILogger mLogger = ApplicationContext.Logger.forType(Volume.class);

	private String mImageUri;
	private String mUri = "";

	private Bookmark mBookmark;

	public Volume(VolumeDescription description, String novelUri,
			String imageUri, List<ListRowData> chapters) {
		this(description, novelUri, imageUri, chapters, new Bookmark());
	}

	public Volume(VolumeDescription description, String novelUri,
			String imageUri, List<ListRowData> chapters, Bookmark bookmark) {
		mDescription = description;
		mNovelUri = novelUri;
		mImageUri = imageUri;
		mChapters = chapters;
		mBookmark = bookmark;
	}

	public static Volume fromJson(String json) throws JSONException {
		JSONObject jObject = new JSONObject(json);

		VolumeDescription description = VolumeDescription
				.fromJson(jObject.getString(DESCRIPTION_TAG));
		JSONArray array = jObject.getJSONArray(CHAPTER_TAG);
		List<ListRowData> chapters = new ArrayList<ListRowData>();
		for (int i = 0; i < array.length(); i++) {
			chapters.add(ListRowData.fromJson(array.getString(i)));
		}
		String novelUri = jObject.getString(URI_TAG);
		String imageUri = jObject.getString(IMAGE_URI_TAG);

		BookmarkDatabase database = new BookmarkDatabase(
				SubApplication.getContext());
		Bookmark bookmark = database.fetchBookmark(novelUri);
		if (bookmark == null)
			bookmark = new Bookmark();

		return new Volume(description, novelUri, imageUri, chapters, bookmark);
	}

	public JSONObject toJson() {
		JSONObject jObject = new JSONObject();

		try {
			jObject.put(DESCRIPTION_TAG, mDescription.toString());

			for (ListRowData chapter : mChapters) {
				jObject.accumulate(CHAPTER_TAG, chapter.toString());
			}

			jObject.put(URI_TAG, mUri);
			jObject.put(IMAGE_URI_TAG, mImageUri);
		} catch (JSONException e) {
			mLogger.error(e, "fail to generate json object");
		}

		return jObject;
	}

	@Override
	public String toString() {
		return toJson().toString();
	}

	public VolumeDescription getDescription() {
		return mDescription;
	}

	public String getImageUri() {
		return mImageUri;
	}

	public void setImageUri(String uri) {
		mImageUri = uri;
	}

	public List<ListRowData> getChapters() {
		return mChapters;
	}

	public void setReading(String chapter) {
		mBookmark.setVolume(mUri);
		mBookmark.setChapter(chapter);
	}

	public Bookmark getBookmark() {
		return mBookmark;
	}

	public boolean isReading(String chapter) {
		return mBookmark.getChapter().equalsIgnoreCase(chapter);
	}

	public void setReadingPosition(int pos) {
		mBookmark.setLine(pos);
	}

	public int getReadingPosition() {
		return mBookmark.getLine();
	}

	public void setUri(String uri) {
		mUri = uri;
	}

	public String getUri() {
		return mUri;
	}

	public String getNovelUri() {
		return mNovelUri;
	}
}
