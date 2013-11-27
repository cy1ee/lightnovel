package tw.com.lightnovel.database;

import java.util.ArrayList;
import java.util.List;

import tw.com.lightnovel.classes.Bookmark;
import tw.com.lightnovel.classes.ListRowData;
import tw.com.lightnovel.classes.VolumeDescription;
import tw.com.lightnovel.classes.Volume;
import tw.com.lightnovel.core.ApplicationContext;
import tw.com.lightnovel.logging.ILogger;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class VolumeDatabase extends LightNovelDatabase {
	private ILogger mLogger = ApplicationContext.Logger
			.forType(VolumeDatabase.class);
	private final String SELECT_SQL = "select * from volume where volume_id = '%s'";
	private final String DELETE_SQL = "delete from volume where volume_id = '%s'";
	private final BookmarkDatabase mBookmarkDatabase;

	public VolumeDatabase(Context context) {
		super(context);

		mBookmarkDatabase = new BookmarkDatabase(context);
	}

	public void save(Volume volume) {
		if (volume == null)
			return;

		delete(volume);

		VolumeDescription description = volume.getDescription();

		ContentValues row = new ContentValues();
		row.put("volume_id", volume.getUri());
		row.put("name", description.getName());
		row.put("author", description.getAuthor());
		row.put("iconograph", description.getIconograph());
		row.put("publish", description.getPublish());
		row.put("view_count", description.getViewCount());
		row.put("update_date_time", description.getUpdateDateTime());
		row.put("description", description.getDescription());
		row.put("chapters", flateChapter(volume.getChapters()));
		row.put("novel_url", volume.getNovelUri());
		row.put("image_url", volume.getImageUri());
		// row.put("reading_id", volume.getReadingId());
		// row.put("reading_position", volume.getReadingPosition());
		SQLiteDatabase db = getWritableDatabase();
		db.insert("volume", null, row);
		db.close();

		mBookmarkDatabase.addBookmark(volume.getBookmark());
	}

	public void delete(Volume volume) {
		String sql = String.format(DELETE_SQL, volume.getUri());
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL(sql);
		db.close();
	}

	public Volume fetchVolume(String id) {
		String sql = String.format(SELECT_SQL, id);
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.rawQuery(sql, null);

		Volume volume = null;

		while (cursor.moveToNext()) {
			String volumeUrl = cursor.getString(1);
			VolumeDescription description = new VolumeDescription(
					cursor.getString(2), cursor.getString(3),
					cursor.getString(4), cursor.getString(5),
					cursor.getString(6), cursor.getString(7),
					cursor.getString(8));

			List<ListRowData> chapters = inflate(cursor.getString(9));
			String novelUrl = cursor.getString(10);
			String imageUrl = cursor.getString(11);
			String readingId = cursor.getString(12);
			int readingPosition = cursor.getInt(13);

			Bookmark bookmark = mBookmarkDatabase.fetchBookmark(volumeUrl);
			if (bookmark == null)
				bookmark = new Bookmark();
			volume = new Volume(description, novelUrl, imageUrl, chapters,
					bookmark);
			volume.setUri(volumeUrl);
			// volume.setReading(readingId);
			// volume.setReadingPosition(readingPosition);
		}

		cursor.close();
		db.close();

		return volume;
	}

	private List<ListRowData> inflate(String content) {
		List<ListRowData> chapters = new ArrayList<ListRowData>();
		mLogger.log(content);
		String[] token = content.split("\\^");

		for (int i = 0; i < token.length; i++) {
			try {
				ListRowData chapter = ListRowData.fromJson(token[i]);
				chapters.add(chapter);
			} catch (Exception e) {
				mLogger.error(e,
						"fail to create finger print from json string ["
								+ token[i] + "]");
			}

		}

		return chapters;
	}

	private String flateChapter(List<ListRowData> chapters) {
		StringBuilder sb = new StringBuilder();

		for (ListRowData chapter : chapters) {
			mLogger.log(chapter.toString());
			sb.append(chapter.toString()).append("^");
		}

		return sb.toString();
	}
}
