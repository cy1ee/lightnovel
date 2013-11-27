package tw.com.lightnovel.database;

import tw.com.lightnovel.classes.Bookmark;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BookmarkDatabase extends LightNovelDatabase {
	private final String SELECT_SQL = "select * from bookmark where volume_id = '%s'";
	private final String DELETE_SQL = "delete from bookmark where volume_id = '%s'";

	public BookmarkDatabase(Context context) {
		super(context);
	}

	public Bookmark fetchBookmark(String volumeId) {
		if (volumeId == null)
			return new Bookmark();
		
		String sql = String.format(SELECT_SQL, volumeId);
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.rawQuery(sql, null);
		Bookmark mark = null;

		while (cursor.moveToNext()) {
			String volume = cursor.getString(1);
			String chapter = cursor.getString(2);
			int lineAt = cursor.getInt(3);
			
			mark = new Bookmark(volume, chapter, lineAt);
		}

		cursor.close();
		db.close();
		
		return mark;
	}

	public void addBookmark(Bookmark mark) {
		if (mark == null)
			return;

		deleteBookmark(mark.getVolume());

		ContentValues row = new ContentValues();
		row.put("volume_id", mark.getVolume());
		row.put("chapter_id", mark.getChapter());
		row.put("line_no", mark.getLine());

		SQLiteDatabase db = getWritableDatabase();
		db.insert("bookmark", null, row);
		db.close();
	}

	public void deleteBookmark(String volumeId) {
		if (volumeId == null)
			return;

		String sql = String.format(DELETE_SQL, volumeId);
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL(sql);
		db.close();
	}
}
