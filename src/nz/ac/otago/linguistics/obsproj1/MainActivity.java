package nz.ac.otago.linguistics.obsproj1;

import java.util.Random;

import android.app.Activity;
import android.graphics.PorterDuff.Mode;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	private View sentencePanel;
	protected HideAndSeekTextView sentenceView;
	private Button advButton;

	private View questionPanel;
	private TextView questionView;
	private Button yesButton;
	private Button noButton;

	/**
	 * A collection of sentences to show the user.
	 */
	private String[] sentences;

	/**
	 * A collection of questions to ask the user. The indices of the given data
	 * should match up with the sentences.
	 */
	private String[] questions;

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

	private View.OnClickListener yesClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			checkAnswer(true);
		}
	};

	private View.OnClickListener noClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			checkAnswer(false);
		}
	};

	private View.OnClickListener advClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (sentenceView.step()) {
				if (rand.nextBoolean()) {
					showQuestion();
				} else {
					showNextSentence();
				}
			}
		}
	};
	private int currSentence;

	@Override
	protected void onResume() {
		super.onResume();
		final View mainView = findViewById(R.id.main_view);
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
		setContentView(R.layout.main);

		// find sentence widgets
		sentencePanel = findViewById(R.id.sentence_frame);
		sentenceView = (HideAndSeekTextView) findViewById(R.id.sentence_text);
		advButton = (Button) findViewById(R.id.adv_button);

		// find question widgets
		questionPanel = findViewById(R.id.question_frame);
		questionView = (TextView) findViewById(R.id.question_text);
		yesButton = (Button) findViewById(R.id.yes_button);
		noButton = (Button) findViewById(R.id.no_button);

		// colourise yes/no buttons
		yesButton.getBackground().setColorFilter(0xFF008800, Mode.MULTIPLY);
		noButton.getBackground().setColorFilter(0xFF880000, Mode.MULTIPLY);

		// glue together buttons
		yesButton.setOnClickListener(yesClickListener);
		noButton.setOnClickListener(noClickListener);
		advButton.setOnClickListener(advClickListener);

		// get the sentences, we'll eliminate these one by one as each is shown.
		sentences = getResources().getStringArray(R.array.sentences);
		questions = getResources().getStringArray(R.array.questions);
		usedSentences = new boolean[sentences.length]; // init to false

		// initialise remaining state
		rand = new Random();
		currSentence = -1;
		numUsedSentences = 0;

		showNextSentence();
	}

	/**
	 * Show the next sentence to the user. The next sentence is randomly chosen
	 * ignoring used sentences.
	 */
	protected void showNextSentence() {
		// track used sentence
		if (currSentence != -1) {
			usedSentences[currSentence] = true;
			numUsedSentences += 1;
		}
		// swap to the sentence panel
		questionPanel.setVisibility(View.GONE);
		sentencePanel.setVisibility(View.VISIBLE);

		// pick a random sentence from what is left.
		sentenceView.setText(getNextSentence());
	}

	/**
	 * Randomly choose the next sentence to use. Ignores used sentences.
	 * 
	 * @return Selected sentence.
	 */
	private CharSequence getNextSentence() {
		currSentence = rand.nextInt(sentences.length - numUsedSentences);

		// jump past used sentences
		for (int i = 0; i < currSentence; i++) {
			if (usedSentences[i]) {
				currSentence += 1;
			}
		}
		return sentences[currSentence];
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO: There may be a need to do stuff here to protect against the
		// activity life-cycle.
		super.onSaveInstanceState(outState);
	}

	/**
	 * Show the user the corresponding question for the currently selected
	 * sentence.
	 */
	protected void showQuestion() {
		// swap to the question panel
		questionPanel.setVisibility(View.VISIBLE);
		sentencePanel.setVisibility(View.GONE);

		questionView.setText(questions[currSentence]);
	}

	/**
	 * Check the answer to the current question. Provides feedback to the user
	 * based on the result of their answer and continues to the next question.
	 * 
	 * @param answeredYes
	 *            True if the user answered yes to the question.
	 */
	protected void checkAnswer(boolean answeredYes) {
		// TODO: give some sort of feedback
		showNextSentence();
	}
}