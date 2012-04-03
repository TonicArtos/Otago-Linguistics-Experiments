package nz.ac.otago.linguistics.obsproj1;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Random;
import java.util.Vector;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

/**
 * Conduct an experiment for a single subject.
 * 
 * @author Tonic Artos
 */
public class ExperimentActivity extends Activity {
	/**
	 * A result that can save its data to a given database.
	 * 
	 * @author Tonic Artos
	 */
	public interface Result extends Serializable {

		void write(BufferedWriter out) throws IOException;
	}

	/**
	 * Indices match up with sentences. A true value means the matching sentence
	 * has been used.
	 */
	private boolean[] usedSentences;

	/**
	 * The Number of used sentences.
	 */
	private int numUsedSentences;

	protected Random rand;

	private Vector<Result> results;
	private int sentencesSinceBreak;
	private int totalNumSentences;

	@Override
	protected void onResume() {
		super.onResume();
		final View mainView = findViewById(android.R.id.content);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			mainView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
		} else {
			mainView.setSystemUiVisibility(View.STATUS_BAR_HIDDEN);
		}
	}

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.landing_page);

		results = new Vector<ExperimentActivity.Result>();

		rand = new Random();
		numUsedSentences = 0;
		totalNumSentences = getResources().getStringArray(R.array.sentences).length;
		usedSentences = new boolean[totalNumSentences]; // All elements
														// initialise to false

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(android.R.id.content, ProfileFragment.newInstance(this));
		ft.setTransition(FragmentTransaction.TRANSIT_NONE);
		ft.commit();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO: There may be a need to do stuff here to protect against the
		// activity life-cycle.
		super.onSaveInstanceState(outState);
	}

	/**
	 * Add a result to the total collection for this session.
	 * 
	 * @param result
	 */
	public void addResult(Result result) {
		results.add(result);
	}

	/**
	 * Show another sentence to the user. Every 12 sentences a break page will
	 * be shown instead. After showing all sentences an exit page will be shown
	 * and the experiment will end.
	 */
	public void showNextSentence() {
		// Finish when we have shown all sentences.
		if (numUsedSentences - totalNumSentences == 0) {
			storeResults();
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(android.R.id.content, PauseFragment.newInstance(this, PauseFragment.MODE_EXIT));
			ft.setTransition(FragmentTransaction.TRANSIT_NONE);
			ft.commit();
			return;
		}

		// Give the user a break every 12 sentences.
		if (sentencesSinceBreak >= 12) {
			sentencesSinceBreak = 0;
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(android.R.id.content, PauseFragment.newInstance(this, PauseFragment.MODE_BREAK));
			ft.setTransition(FragmentTransaction.TRANSIT_NONE);
			ft.commit();
			return;
		}

		// Select a valid random sentence index.
		int selected = rand.nextInt(totalNumSentences - numUsedSentences);
		Log.d("totalNumSentences", String.valueOf(totalNumSentences));
		Log.d("numUsedSentences", String.valueOf(numUsedSentences));
		Log.d("selected", String.valueOf(selected));
		// Jump past used sentences.
		for (int i = 0; i <= selected; i++) {
			Log.d("used", String.valueOf(usedSentences[i]));
			if (usedSentences[i]) {
				selected += 1;
			}
		}
		Log.d("final selected", String.valueOf(selected));

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(android.R.id.content, SentenceFragment.newInstance(this, selected));
		ft.setTransition(FragmentTransaction.TRANSIT_NONE);
		ft.commit();

		sentencesSinceBreak += 1;
		usedSentences[selected] = true;
		numUsedSentences += 1;
	}

	/**
	 * Save the experiment results into the database.
	 */
	private void storeResults() {
		// FIXME
	}
	
	@Override
	public void finish() {
		super.finish();

        DatabaseHelper db = new DatabaseHelper(this);
        ContentValues values = new ContentValues();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null; 
        try {
                oos = new ObjectOutputStream(baos);
                oos.writeObject(results);
        } catch (IOException e) {
                Log.e("SPRE-DataStorage", e.getMessage(), e);
                Toast.makeText(this, "There was an error storing the data - " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
                try {
                        oos.close();
                } catch (IOException e) {}
        }
        byte[] serialisedResults = baos.toByteArray(); 
        
        values.put(ExperimentData.KEY_DATA, serialisedResults);
        
        db.getWritableDatabase().insert(ExperimentData.TABLE, null, values);
        db.close();
	}
}