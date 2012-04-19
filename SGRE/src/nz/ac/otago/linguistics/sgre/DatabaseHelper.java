package nz.ac.otago.linguistics.sgre;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "data";
	private static final String TAG = "com.devnaos.shopitalist";

	public DatabaseHelper(Context ctx) {
		super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(ExperimentData.TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		int currVersion = oldVersion;
		Log.w(TAG, "Upgrading database to version " + newVersion);
		while (currVersion < newVersion) {
			Log.w(TAG, "Upgrading database from version " + currVersion + " to version " + (currVersion + 1));
			switch (currVersion) {
			case 1:
				db.execSQL("ALTER TABLE ExperimentData ADD data_set text not null default 'a';");
				break;
			default:
				break;
			}
			currVersion++;
		}
	}

	public int clearData() {
		return getWritableDatabase().delete(ExperimentData.TABLE, null, null);
	}
}
