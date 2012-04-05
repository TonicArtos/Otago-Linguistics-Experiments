package nz.ac.otago.linguistics.spre;

import java.util.Random;
import java.util.Vector;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

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
	public interface Result {
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

	protected long sessionId;

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
		
		// Work out this session ID;
		DatabaseHelper db = new DatabaseHelper(getApplicationContext());
		Cursor c = db.getReadableDatabase().query(ExperimentData.TABLE, new String[] {ExperimentData.KEY_ROWID}, null, null, null, null, null);
		if (!c.moveToLast()) {
			sessionId = 1;
		} else {
			sessionId = c.getLong(c.getColumnIndex(ExperimentData.KEY_ROWID));
		}
		c.close();
		db.close();

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
			ft.replace(android.R.id.content, PauseFragment.newInstance(this, PauseFragment.MODE_EXIT, 0));
			ft.setTransition(FragmentTransaction.TRANSIT_NONE);
			ft.commit();
			return;
		}

		// Give the user a break every 12 sentences.
		if (sentencesSinceBreak >= 12) {
			sentencesSinceBreak = 0;
			// Work out number of blocks remaining.
			int numBlocksRemaining = (totalNumSentences - numUsedSentences) / 12;
			
			// Create break page.
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(android.R.id.content, PauseFragment.newInstance(this, PauseFragment.MODE_BREAK, numBlocksRemaining));
			ft.setTransition(FragmentTransaction.TRANSIT_NONE);
			ft.commit();
			
			return;
		}

		// Select a valid random sentence index.
		int selected = rand.nextInt(totalNumSentences - numUsedSentences);
		// Jump past used sentences.
		for (int i = 0; i <= selected; i++) {
			if (usedSentences[i]) {
				selected += 1;
			}
		}

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
        DatabaseHelper db = new DatabaseHelper(this);
        ContentValues values = new ContentValues();

        String s = "";
        for (Result r : results) {
        	s += r.toString();
        }
        
        values.put(ExperimentData.KEY_DATA, s);
        
        db.getWritableDatabase().insert(ExperimentData.TABLE, null, values);
        db.close();
	}
}