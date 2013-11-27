package tw.com.lightnovel.classes;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tw.com.lightnovel.apis.IJsonSerilizable;

public class NovelIndex implements IJsonSerilizable {
	private static final String NAME_TAG = "name";
	private static final String IMAGEURI_TAG = "imageuri";
	private static final String DESCRIPTION_TAG = "description";
	private static final String VOLUME_TAG = "volume";

	private final String mName;
	private final NovelDescription mDescription;

	private List<ListRowData> mVolumes;

	private String mImageUri;

	public NovelIndex(String name, String imageUri,
			NovelDescription description, List<ListRowData> volumes) {
		mName = name;
		mImageUri = imageUri;
		mDescription = description;
		mVolumes = volumes;
	}

	public NovelDescription getDescription() {
		return mDescription;
	}

	public String getName() {
		return mName;
	}

	public String getImageUri() {
		return mImageUri;
	}

	public void setImageUri(String uri) {
		mImageUri = uri;
	}

	public List<ListRowData> getVolumes() {
		return mVolumes;
	}

	public void setVolumes(List<ListRowData> volumes) {
		mVolumes = volumes;
	}

	public static NovelIndex fromJson(String json) throws JSONException {
		JSONObject jObject = new JSONObject(json);
		String name = jObject.getString(NAME_TAG);
		String imageUri = jObject.getString(IMAGEURI_TAG);
		NovelDescription description = NovelDescription.fromJson(jObject
				.getJSONObject(DESCRIPTION_TAG));
		List<ListRowData> volumes = new ArrayList<ListRowData>();
		JSONArray items = jObject.getJSONArray(VOLUME_TAG);
		for (int i = 0; i < items.length(); i++) {
			ListRowData volume = ListRowData.fromJson(items.getJSONObject(i));
			volumes.add(volume);
		}

		return new NovelIndex(name, imageUri, description, volumes);
	}

	public JSONObject toJson() {
		JSONObject jObject = new JSONObject();

		try {
			jObject.put(NAME_TAG, mName);
			jObject.put(IMAGEURI_TAG, mImageUri);
			jObject.put(DESCRIPTION_TAG, mDescription.toJsonObject());

			JSONArray volumes = new JSONArray();
			for (ListRowData fingerPrint : mVolumes) {
				volumes.put(fingerPrint.toJson());
			}
			
			jObject.put(VOLUME_TAG, volumes);

		} catch (JSONException e) {

		}

		return jObject;
	}
}
