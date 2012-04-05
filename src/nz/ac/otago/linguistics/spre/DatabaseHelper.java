package nz.ac.otago.linguistics.spre;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
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
                default:
                    break;
            }
            currVersion++;
        }
    }
}
