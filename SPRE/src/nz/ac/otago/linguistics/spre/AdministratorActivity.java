package nz.ac.otago.linguistics.spre;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Launch activity. From here the experiment administrator can start an
 * experiment session or export the results.
 * 
 * @author Tonic Artos
 */
public class AdministratorActivity extends Activity {
	private View.OnClickListener runExperiment1ClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(getApplicationContext(), ExperimentActivity.class);
			intent.putExtra(ExperimentActivity.KEY_MODE, ExperimentActivity.MODE_EXPERIMENT1);
			startActivity(intent);
		}
	};
	private View.OnClickListener runExperiment2ClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(getApplicationContext(), ExperimentActivity.class);
			intent.putExtra(ExperimentActivity.KEY_MODE, ExperimentActivity.MODE_EXPERIMENT2);
			startActivity(intent);
		}
	};
	private View.OnClickListener exportDataClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				exportData();
			} else {
				Toast.makeText(getApplicationContext(), "External storage is unavailable at the moment. Please try again later.", Toast.LENGTH_LONG);
			}
		}
	};
	private View.OnLongClickListener clearDataLongClickListener = new View.OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			DialogFragment f = ClearDataDialogFragment.newInstance(new ClearDataDialogFragment.OnInputListener() {
				@Override
				public void onPositiveClick() {
					clearData();
				}

				@Override
				public void onNegativeClick() {
				}
			});
			f.show(getFragmentManager(), "dialog");
			return true;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// We want to make sure the database is updated before we use it
		// anywhere.
		DatabaseHelper db = new DatabaseHelper(this);
		db.getWritableDatabase();
		db.close();

		((Button) findViewById(R.id.button_run_experiment1)).setOnClickListener(runExperiment1ClickListener);
		((Button) findViewById(R.id.button_run_experiment2)).setOnClickListener(runExperiment2ClickListener);
		((Button) findViewById(R.id.button_export_data)).setOnClickListener(exportDataClickListener);
		((Button) findViewById(R.id.button_clear_data)).setOnLongClickListener(clearDataLongClickListener);
		updateRecordCountDisplay();

	}

	private void updateRecordCountDisplay() {
		DatabaseHelper db = new DatabaseHelper(this);
		Cursor c = db.getReadableDatabase().query(ExperimentData.TABLE, new String[] { ExperimentData.KEY_ROWID }, null, null, null, null, null);
		if (c.getCount() == 0) {
			findViewById(R.id.text_num_records).setVisibility(View.INVISIBLE);
		} else {
			findViewById(R.id.text_num_records).setVisibility(View.VISIBLE);
			((TextView) findViewById(R.id.text_num_records)).setText(c.getCount() + " Records");
		}
		c.close();
		db.close();
	}

	protected void clearData() {
		DatabaseHelper db = new DatabaseHelper(getApplicationContext());
		int nr = 0;
		try {
			nr = db.clearData();
		} catch (SQLiteException e) {
			Log.e("ClearData", "Error " + e.getMessage());
			Toast.makeText(getApplicationContext(), "An error was encountered while clearing data", Toast.LENGTH_LONG).show();
		} finally {
			db.close();
		}
		Toast.makeText(getApplicationContext(), nr + " records cleared", Toast.LENGTH_LONG).show();
		updateRecordCountDisplay();
	}

	protected void exportData() {
		DatabaseHelper db = new DatabaseHelper(getApplicationContext());
		Cursor c = db.getReadableDatabase().query(ExperimentData.TABLE,
				new String[] { ExperimentData.KEY_ROWID, ExperimentData.KEY_DATA, ExperimentData.KEY_DATA_SET }, null, null, null, null, null);
		if (!c.moveToFirst()) {
			// nothing to write out.
			return;
		}
		File path = Environment.getExternalStoragePublicDirectory("SPRE");
		File file = new File(path, "experimentdata.csv");
		// file.setReadable(true, false);

		try {
			path.mkdirs();
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			do {
				out.write("\nSession ID, "
						+ c.getString(c.getColumnIndex(ExperimentData.KEY_DATA_SET))
						+ c.getInt(c.getColumnIndex(ExperimentData.KEY_ROWID))
						+ "\n");
				out.write(c.getString(c.getColumnIndex(ExperimentData.KEY_DATA)));
			} while (c.moveToNext());
			out.flush();
			out.close();
		} catch (IOException e) {
			Log.w("ExternalStorage", "Error writing " + file, e);
			Toast.makeText(this, "An error was encountered", Toast.LENGTH_LONG).show();
		}
		MediaScannerConnection.scanFile(getApplicationContext(), new String[] { file.getAbsolutePath() }, null,
				new MediaScannerConnection.OnScanCompletedListener() {
					@Override
					public void onScanCompleted(final String path, final Uri uri) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// Toast.makeText(getApplicationContext(), path
								// + " " + uri, Toast.LENGTH_LONG).show();
								Toast.makeText(getApplicationContext(), "Data exported to SPRE/experimentdata.csv", Toast.LENGTH_LONG).show();
							}
						});
					}
				});
		c.close();
		db.close();

	}

	@Override
	protected void onRestart() {
		super.onRestart();
		updateRecordCountDisplay();
	}
}
