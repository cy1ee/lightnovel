package tw.com.lightnovel.core;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import tw.com.lightnovel.apis.ITranslator;
import tw.com.lightnovel.apis.NullTranslator;
import tw.com.lightnovel.apis.ZhTranslator;
import tw.com.lightnovel.apis.Zhcoder;
import tw.com.lightnovel.classes.ListRowData;
import tw.com.lightnovel.classes.Volume;
import tw.com.lightnovel.database.BookCollectionDatabase;
import tw.com.lightnovel.database.BookmarkDatabase;
import tw.com.lightnovel.database.VolumeDatabase;
import tw.com.lightnovel.logging.ConsoleLogger;
import tw.com.lightnovel.logging.ILogger;
import tw.com.thinkso.novelreaderapp.R;
import android.content.Context;
import android.os.Environment;

public class ApplicationContext {
	private Zhcoder mZhCoder;
	private HashMap<String, Volume> mVolumesCache;
	private VolumeDatabase mVolumeDatabase;
	private BookmarkDatabase mBookmarkDatabase;
	private BookCollectionDatabase mNovelRecordDatabase;
	private ITranslator mTranslator;
	private File mBaseFolder;

	public static ILogger Logger = new ConsoleLogger();

	private static ApplicationContext mInstance;

	public static ApplicationContext getInstance() {
		if (mInstance == null) {
			mInstance = new ApplicationContext();
		}

		return mInstance;
	}

	private ApplicationContext() {
		Context context = SubApplication.getContext();
		mZhCoder = new Zhcoder(context.getResources().openRawResource(
				R.raw.hcutf8));
		mVolumesCache = new HashMap<String, Volume>();
		mVolumeDatabase = new VolumeDatabase(context);
		mNovelRecordDatabase = new BookCollectionDatabase(context);
		mBookmarkDatabase = new BookmarkDatabase(context);
		mBaseFolder = Environment
				.getExternalStoragePublicDirectory("lightnovel");
		mBaseFolder.mkdirs();

		setTranslator();
	}

	public File getBaseFolder() {
		return mBaseFolder;
	}

	private void setTranslator() {
		Locale locale = Locale.getDefault();

		if (locale.getCountry().equalsIgnoreCase("TW")) {
			mTranslator = new ZhTranslator(mZhCoder);
		} else {
			mTranslator = new NullTranslator();
		}
	}

	public ITranslator getTranslator() {
		return mTranslator;
	}

	public Zhcoder getZhCoder() {
		return mZhCoder;
	}

	public boolean containsVolume(String volumeId) {
		return mVolumesCache.containsKey(volumeId);
	}

	public Volume getVolume(String volumeId) {
		Volume volume = null;

		if (containsVolume(volumeId)) {
			volume = mVolumesCache.get(volumeId);
		} else {
			volume = mVolumeDatabase.fetchVolume(volumeId);
		}

		return volume;
	}

	public void cacheVolume(String volumeId, Volume volume) {
		if (mVolumesCache.containsKey(volumeId)) {
			mVolumesCache.remove(volumeId);
		}

		mVolumesCache.put(volumeId, volume);
	}

	public BookmarkDatabase getBookmarkDatabase() {
		return mBookmarkDatabase;
	}

	public VolumeDatabase getVolumeDatabase() {
		return mVolumeDatabase;
	}

	public void addVolume(String volumeId, Volume volume) {
		if (mVolumesCache.containsKey(volumeId)) {
			mVolumesCache.remove(volumeId);
		}

		mVolumesCache.put(volumeId, volume);
	}

	public void addNovelRecord(ListRowData novelRecord) {
		mNovelRecordDatabase.save(novelRecord);
	}

	public List<ListRowData> getNovelRecords() {
		return mNovelRecordDatabase.loadRecord();
	}

	public void deleteNovelRecord(String id) {
		mNovelRecordDatabase.delete(id);
	}
}
