package tw.com.lightnovel.database;

import java.util.ArrayList;
import java.util.List;

import tw.com.lightnovel.classes.ListRowData;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BookCollectionDatabase extends LightNovelDatabase {
	private final String INSERT_SQL = "insert into novel_record (novel_id, name, url, image_url)"
			+ " values ('%s', '%s', '%s', '%s')";

	private final String DELETE_SQL = "delete from novel_record where novel_id = '%s'";
	private final String SELECT_SQL = "select * from novel_record";

	public BookCollectionDatabase(Context context) {
		super(context);
	}

	public void save(ListRowData fingerPrint) {
		delete(fingerPrint.getUri());

		String sql = String.format(INSERT_SQL, fingerPrint.getUri(),
				fingerPrint.getTitle(), fingerPrint.getUri(),
				fingerPrint.getImageUri());
		
		//Log.d("insert sql", sql);

		SQLiteDatabase db = getWritableDatabase();
		db.execSQL(sql);
		db.close();
	}

	public void delete(String novelUrl) {
		String sql = String.format(DELETE_SQL, novelUrl);
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL(sql);
		db.close();
	}

	public List<ListRowData> loadRecord() {
		List<ListRowData> novelRecords = new ArrayList<ListRowData>();

		String sql = SELECT_SQL;
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.rawQuery(sql, null);		

		while (cursor.moveToNext()) {			
			String name = cursor.getString(2);
			String novelUrl = cursor.getString(3);
			String imageUrl = cursor.getString(4);
			
			ListRowData novelRecord = new ListRowData(name, imageUrl, novelUrl);
			novelRecords.add(novelRecord);			
		}
		
		db.close();
		
		return novelRecords;
	}
}
