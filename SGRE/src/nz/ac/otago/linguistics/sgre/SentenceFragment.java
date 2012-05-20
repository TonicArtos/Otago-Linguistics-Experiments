package nz.ac.otago.linguistics.sgre;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class SentenceFragment extends Fragment {
	private static final String KEY_INDEX = "index";

	public static Fragment newInstance(ExperimentActivity main, int index, int mode) {
		SentenceFragment f = new SentenceFragment();
		f.main = main;
		f.mode = mode;

		// Set index as an argument
		Bundle args = new Bundle();
		args.putInt(KEY_INDEX, index);
		f.setArguments(args);

		return f;
	}

	protected ExperimentActivity main;
	private HideAndSeekTextView sentenceView;
	protected SentenceResult result;
	private int mode;

	private int[] questions;
	private SeekBar seekBarView;
	private int seekBarMargin;
	private int seekBarMax;

	private HideAndSeekTextView.OnWordChangeListener wordChangeListener = new HideAndSeekTextView.OnWordChangeListener() {
		@Override
		public void OnWordChange(int wordIndex, String word, long eventTime, long timeSinceLastChange) {
			// Build an event and log it.
			WordEvent e = new WordEvent();
			e.wordIndex = wordIndex;
			e.word = word;
			e.millis = eventTime;
			e.delta = timeSinceLastChange;
			result.wordEvents.add(e);
		}
	};

	private HideAndSeekTextView.OnCharChangeListener charChangeListener = new HideAndSeekTextView.OnCharChangeListener() {
		@Override
		public void OnCharChange(int charIndex, String character, long eventTime, long timeSinceLastChange) {
			// Build an event and log it.
			CharEvent e = new CharEvent();
			e.charIndex = charIndex;
			e.character = character;
			e.millis = eventTime;
			e.delta = timeSinceLastChange;
			result.charEvents.add(e);
		}
	};

	private OnSeekBarChangeListener seekChangeListener = new OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			// Update SentenceView with new finger position.
			sentenceView.giveFingerPosition((int) (progress + seekBar.getX()));

			// Build an event and log it.
			SeekEvent e = new SeekEvent();
			e.millis = System.currentTimeMillis();
			e.word = sentenceView.getWord();
			e.character = sentenceView.getChar();
			e.xPos = (int) (progress - seekBarMargin);
			result.seekEvents.add(e);
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// If the finger is lifted when the SeekBar slider is at Max then
			// the user is done. Save our results and go to the question or next
			// sentence (as required).
			if (result.seekEvents.lastElement().xPos == seekBarMax - seekBarMargin) {
				main.addResult(result);
				if (!showQuestion()) {
					nextSentence();
				}
			}
		}
	};

	protected void nextSentence() {
		if (mode == ExperimentActivity.MODE_PRACTICE) {
			main.showNextPracticeSentence();
		} else {
			main.showNextSentence();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Start building result to store after the whole sentence has been
		// read.
		result = new SentenceResult();
		result.sentenceIndex = getArguments().getInt(KEY_INDEX, 0);

		if (mode == ExperimentActivity.MODE_PRACTICE) {
			result.sentence = getResources().getStringArray(R.array.practice_sentences)[result.sentenceIndex];
			questions = getResources().getIntArray(R.array.practice_questions);
			result.condition1 = 4;
			result.condition2 = 5;
		} else if (mode == ExperimentActivity.MODE_EXPERIMENT1) {
			result.sentence = getResources().getStringArray(R.array.list1_sentences)[result.sentenceIndex];
			questions = getResources().getIntArray(R.array.list1_questions);
			result.condition1 = getResources().getIntArray(R.array.list1_conditions1)[result.sentenceIndex];
			result.condition2 = getResources().getIntArray(R.array.list1_conditions2)[result.sentenceIndex];
		} else {
			result.sentence = getResources().getStringArray(R.array.list2_sentences)[result.sentenceIndex];
			questions = getResources().getIntArray(R.array.list2_questions);
			result.condition1 = getResources().getIntArray(R.array.list2_conditions1)[result.sentenceIndex];
			result.condition2 = getResources().getIntArray(R.array.list2_conditions2)[result.sentenceIndex];
		}

		View v = inflater.inflate(R.layout.sentence_page, container, false);

		// Sentence
		sentenceView = (HideAndSeekTextView) v.findViewById(R.id.sentence_text);
		sentenceView.setText(result.sentence);
		sentenceView.addParentLayout(v.findViewById(R.id.sentence_frame));

		sentenceView.setOnCharChangeListener(charChangeListener);
		sentenceView.setOnWordChangeListener(wordChangeListener);

		// SeekBar
		seekBarView = ((SeekBar) v.findViewById(R.id.seek_bar));
		LayoutParams seekBarLayout = seekBarView.getLayoutParams();
		seekBarMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());
		seekBarMax = sentenceView.getTextWidth() + 2 * seekBarMargin;
		seekBarLayout.width = seekBarMax;
		seekBarView.setLayoutParams(seekBarLayout);
		seekBarView.setMax(seekBarMax);

		seekBarView.setOnSeekBarChangeListener(seekChangeListener);

		return v;
	}

	/**
	 * Show the question for this sentence.
	 * 
	 * @return False if there is not a question for this sentence.
	 */
	protected boolean showQuestion() {
		boolean isQuestion = result.sentenceIndex < questions.length;
		if (isQuestion) {
			Fragment question = QuestionFragment.newInstance(main, result.sentenceIndex, mode);
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(android.R.id.content, question);
			ft.setTransition(FragmentTransaction.TRANSIT_NONE);
			ft.commit();
		}
		return isQuestion;
	}
}
