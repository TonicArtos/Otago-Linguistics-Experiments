package nz.ac.otago.linguistics.sgre;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.JsonReader;
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
		Cursor c = db.getReadableDatabase().query(ExperimentData.TABLE, new String[] { ExperimentData.KEY_ROWID, ExperimentData.KEY_DATA_LOCATION }, null, null, null, null, null);
		if (!c.moveToFirst()) {
			return; // Nothing to do.
		}
		do {
			String filename = c.getString(c.getColumnIndex(ExperimentData.KEY_DATA_LOCATION));
			deleteFile(filename);
		} while (c.moveToNext());
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
		new DataExporter().execute();
	}

	private void setupSeekEventsCSVFile(PrintWriter w) {
		ProfileResult.printHeaders(w);
		w.print(",");
		SentenceResult.printHeaders(w);
		w.print(",");
		SeekEvent.printHeaders(w);
		w.println();
	}

	private void setupCharEventsCSVFile(PrintWriter w) {
		ProfileResult.printHeaders(w);
		w.print(",");
		SentenceResult.printHeaders(w);
		w.print(",");
		CharEvent.printHeaders(w);
		w.println();
	}

	private void setupWordEventsCSVFile(PrintWriter w) {
		ProfileResult.printHeaders(w);
		w.print(",");
		SentenceResult.printHeaders(w);
		w.print(",");
		WordEvent.printHeaders(w);
		w.println();
	}

	private ExperimentResult parseJSON(FileReader fr) throws IOException {
		JsonReader reader = new JsonReader(fr);
		try {
			return readExperiment(reader);
		} finally {
			reader.close();
		}
	}

	private ExperimentResult readExperiment(JsonReader reader) throws IOException {
		ExperimentResult exp = new ExperimentResult();
		exp.profile = new ProfileResult();
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (TextUtils.equals(name, "session_id")) {
				exp.profile.sessionId = reader.nextLong();
			} else if (TextUtils.equals(name, "data_set")) {
				exp.profile.dataSet = reader.nextString();
			} else if (TextUtils.equals(name, "english_first_language")) {
				exp.profile.englishFirstLanguage = reader.nextString();
			} else if (TextUtils.equals(name, "right_handed")) {
				exp.profile.rightHandedS = reader.nextString();
			} else if (TextUtils.equals(name, "age")) {
				exp.profile.age = reader.nextInt();
			} else if (TextUtils.equals(name, "gender")) {
				exp.profile.gender = reader.nextString();
			} else if (TextUtils.equals(name, "rows")) {
				exp.sentenceData = readSentenceData(reader);
			}
		}
		reader.endObject();
		return exp;
	}

	private List<SentenceResult> readSentenceData(JsonReader reader) throws IOException {
		List<SentenceResult> sentenceData = new ArrayList<SentenceResult>();
		reader.beginArray();
		while (reader.hasNext()) {
			sentenceData.add(SentenceResult.readSentence(reader));
		}
		reader.endArray();
		return sentenceData;
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		updateRecordCountDisplay();
	}

	// temp class to read json data into and then output the three different CSV
	// versions of the experiment.
	private class ExperimentResult {
		protected ProfileResult profile;
		protected List<SentenceResult> sentenceData;

		protected int writeSeekEventsCSV(PrintWriter w, ProgressPublisher p, int step) {
			for (SentenceResult sentence : sentenceData) {
				for (SeekEvent event : sentence.getSeekEvents()) {
					profile.toCSV(w);
					w.print(",");
					sentence.toCSV(w);
					w.print(",");
					event.toCSV(w);
					w.println();
					
					step++;
					p.publish(step);
				}
			}
			return step;
		}

		protected int writeWordEventsCSV(PrintWriter w, ProgressPublisher p, int step) {
			for (SentenceResult sentence : sentenceData) {
				for (WordEvent event : sentence.getWordEvents()) {
					profile.toCSV(w);
					w.print(",");
					sentence.toCSV(w);
					w.print(",");
					event.toCSV(w);
					w.println();
					
					step++;
					p.publish(step);
				}
			}
			return step;
		}

		protected int writeCharEvents(PrintWriter w, ProgressPublisher p, int step) {
			for (SentenceResult sentence : sentenceData) {
				for (CharEvent event : sentence.getCharEvents()) {
					profile.toCSV(w);
					w.print(",");
					sentence.toCSV(w);
					w.print(",");
					event.toCSV(w);
					w.println();
					
					step++;
					p.publish(step);
				}
			}
			return step;
		}
	}
	
	private class DataExporter extends AsyncTask<Void, Integer, Void> {
		private ProgressDialogFragment dialogFragment;

		@Override
		protected Void doInBackground(Void... params) {
			DatabaseHelper db = new DatabaseHelper(getApplicationContext());
			Cursor c = db.getReadableDatabase().query(ExperimentData.TABLE, new String[] { ExperimentData.KEY_ROWID, ExperimentData.KEY_DATA_LOCATION }, null, null, null, null, null);
			if (!c.moveToFirst()) {
				return null; // Nothing to do.
			}
			int maxSteps = c.getCount() * 104 * 3;
			
			
			final File path = Environment.getExternalStoragePublicDirectory("SGRE");
			Time now = new Time();
			now.setToNow();
			final File wordfile = new File(path, now.format("%F %H-%M-%S") + " wordEvents.csv");
			final File charfile = new File(path, now.format("%F %H-%M-%S") + " charEvents.csv");
			final File seekfile = new File(path, now.format("%F %H-%M-%S") + " seekEvents.csv");
			// file.setReadable(true, false);

			try {
				// Setup output file path.
				path.mkdirs();
				PrintWriter wordWriter = new PrintWriter(new FileWriter(wordfile));
				setupWordEventsCSVFile(wordWriter);

				PrintWriter charWriter = new PrintWriter(new FileWriter(charfile));
				setupCharEventsCSVFile(charWriter);

				PrintWriter seekWriter = new PrintWriter(new FileWriter(seekfile));
				setupSeekEventsCSVFile(seekWriter);
				int step = 0; 
				do { // For each row, read data from private file and join in JSON
						// array to file.
					String filename = c.getString(c.getColumnIndex(ExperimentData.KEY_DATA_LOCATION));
					FileInputStream dataFile = openFileInput(filename);
					FileReader fr = new FileReader(dataFile.getFD());

					ExperimentResult exp = parseJSON(fr);
					fr.close();
					dataFile.close();

					step = exp.writeWordEventsCSV(wordWriter, new ProgressPublisher() {
						public void publish(int progress) {
							publishProgress(progress);
						}
					}, step);
					
					step = exp.writeCharEvents(charWriter, new ProgressPublisher() {
						public void publish(int progress) {
							publishProgress(progress);
						}
					}, step);
					
					step = exp.writeSeekEventsCSV(seekWriter, new ProgressPublisher() {
						public void publish(int progress) {
							publishProgress(progress);
						}
					}, step);
				} while (c.moveToNext());

				// End JSON array and finish writing to disk.
				wordWriter.println();
				charWriter.println();
				seekWriter.println();

				wordWriter.close();
				charWriter.close();
				seekWriter.close();
			} catch (IOException e) {
				Log.w("ExternalStorage", "Error exporting results.", e);
				Toast.makeText(getApplicationContext(), "An error was encountered during data export", Toast.LENGTH_LONG).show();
			}

			MediaScannerConnection.scanFile(getApplicationContext(), new String[] { wordfile.getAbsolutePath(), charfile.getAbsolutePath(), seekfile.getAbsolutePath() }, null, new MediaScannerConnection.OnScanCompletedListener() {
				@Override
				public void onScanCompleted(final String path, final Uri uri) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// Show some response to user that data was exported.
							Toast.makeText(getApplicationContext(), "Data exported", Toast.LENGTH_LONG).show();
						}
					});
				}
			});
			c.close();
			db.close();
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			if (values.length > 0 && dialogFragment.getDialog() != null) {
				if (values.length == 2) {
					((ProgressDialogFragment) dialogFragment).setMax(values[1]);
				}
				((ProgressDialogFragment) dialogFragment).setProgress(values[0]);
			}
		}
		
		@Override
		protected void onPostExecute(Void result) {
			DialogFragment dialog = (DialogFragment) getFragmentManager().findFragmentByTag("dialog");
			dialog.dismiss();
			Log.d("asdfa", "asdfasdf");
		}
		
		@Override
		protected void onPreExecute() {
		    FragmentTransaction ft = getFragmentManager().beginTransaction();
		    dialogFragment = ProgressDialogFragment.newInstance(R.string.dialog_export_title, "Working hard", false);
		    dialogFragment.show(ft, "dialog");
		}
	}
	
	private static class ProgressDialogFragment extends DialogFragment {
		static ProgressDialogFragment newInstance(int title, CharSequence message, boolean indeterminate) {
			ProgressDialogFragment f = new ProgressDialogFragment();
			Bundle args = new Bundle();
	        args.putInt("title", title);
	        args.putCharSequence("message", message);
	        args.putBoolean("indeterminate", indeterminate);
	        f.setArguments(args);
	        return f;
		}
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			Bundle args = getArguments();
			int title = args.getInt("title");
			CharSequence message = args.getCharSequence("message");
//			boolean indeterminate = args.getBoolean("indeterminate");
			
		    final ProgressDialog dialog = new ProgressDialog(getActivity());
		    dialog.setTitle(title);
		    dialog.setMessage(message);
		    dialog.setProgressNumberFormat("");
		    dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		    setCancelable(false);
		    dialog.setCanceledOnTouchOutside(false);
		    return dialog;
		}
		
		public void setMax(int max) {
			((ProgressDialog) getDialog()).setMax(max);
		}
		
		public void setProgress(int progress) {
			((ProgressDialog) getDialog()).setProgress(progress);
		}
	}
	
	private interface ProgressPublisher {
		public void publish(int progress);
	}
}
