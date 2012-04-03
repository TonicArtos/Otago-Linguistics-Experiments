package nz.ac.otago.linguistics.obsproj1;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.util.Vector;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Launch activity. From here the experiment administrator can start an
 * experiment session or export the results.
 * 
 * @author Tonic Artos
 */
public class AdministratorActivity extends Activity {
	private View.OnClickListener runExperimentClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
	        Intent intent = new Intent(getApplicationContext(), ExperimentActivity.class);
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		((Button) findViewById(R.id.button_run_experiment)).setOnClickListener(runExperimentClickListener);
		((Button) findViewById(R.id.button_export_data)).setOnClickListener(exportDataClickListener);
	}

	protected void exportData() {
		DatabaseHelper db = new DatabaseHelper(getApplicationContext());
		Cursor c = db.getReadableDatabase().query(ExperimentData.TABLE, new String[] {ExperimentData.KEY_ROWID, ExperimentData.KEY_DATA}, null, null, null, null, null);
		File path = Environment.getExternalStoragePublicDirectory("SPRE");
		File file = new File(path, "experimentdata.csv");
		try {
			path.mkdirs();
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			c.moveToFirst();
			do {
				out.write("Session ID, " + c.getInt(c.getColumnIndex(ExperimentData.KEY_ROWID)) + "\n");
				ByteArrayInputStream bais = new ByteArrayInputStream(c.getBlob(c.getColumnIndex(ExperimentData.KEY_DATA)));
				ObjectInputStream oin = new ObjectInputStream(bais);
				Object obj = oin.readObject();
				if (obj instanceof Vector) {
					Vector<ExperimentActivity.Result> results = (Vector<ExperimentActivity.Result>) obj;
					for (ExperimentActivity.Result result : results) {
						result.write(out);
					}
				}
				
			} while (c.moveToNext());
		} catch (IOException e) {
			Log.w("ExternalStorage", "Error writing " + file, e);
			Toast.makeText(this, "An error was encountered", Toast.LENGTH_LONG).show();
		} catch (ClassNotFoundException e) {
			Log.w("ExternalStorage", "Error recalling data " + e.getMessage(), e);
			Toast.makeText(this, "An error was encountered", Toast.LENGTH_LONG).show();
		}
		db.close();
	}
}
