package tw.com.lightnovel.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public abstract class LightNovelDatabase extends SQLiteOpenHelper {
	private static final String DatabaseName = "light_novel";

	public LightNovelDatabase(Context context) {
		super(context, DatabaseName, null, 2);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE volume " + " (_id INTEGER PRIMARY KEY, "
				+ " volume_id TEXT, " + " name TEXT, " + " author TEXT, "
				+ " iconograph TEXT, " + " publish TEXT, "
				+ " view_count TEXT, " + " update_date_time TEXT, "
				+ " description TEXT, " + " chapters TEXT, "
				+ " novel_url TEXT," + " image_url TEXT, "
				+ " reading_id TEXT, " + " reading_position INTEGER)");

		db.execSQL("CREATE TABLE novel_record " + " (_id INTEGER PRIMARY KEY,"
				+ " novel_id TEXT, name TEXT, url TEXT, image_url TEXT)");

		db.execSQL("CREATE TABLE bookmark "
				+ " (_id INTEGER PRIMARY KEY, volume_id TEXT, "
				+ " chapter_id TEXT, line_no INTEGER)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS volume");
		db.execSQL("DROP TABLE IF EXISTS novel_record");
		db.execSQL("DROP TABLE IF EXISTS bookmark");
		onCreate(db);
	}
}
