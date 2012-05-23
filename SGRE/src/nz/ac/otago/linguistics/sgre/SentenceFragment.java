package nz.ac.otago.linguistics.sgre;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
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
	protected long baseSeekTime;

	private HideAndSeekTextView.OnWordChangeListener wordChangeListener = new HideAndSeekTextView.OnWordChangeListener() {
		@Override
		public void OnWordChange(int wordIndex, String word, long eventTime, long timeSinceLastChange, int relativePosition) {
			// Filter out spurious changes from events outside of the sentence.
			// Only want one of these each time we move outside of the sentence,
			// starting or moving to.
			if (wordIndex == -1 && result.wordEvents.size() > 0 && result.wordEvents.lastElement().wordIndex == -1) {
				return;
			}

			// Build an event and log it.
			WordEvent e = new WordEvent();
			e.wordIndex = wordIndex + 1;
			if (result.wordEvents.size() > 0) {
				e.wordIndexDelta = wordIndex - result.wordEvents.lastElement().wordIndex;
				result.wordEvents.lastElement().timeSpent = timeSinceLastChange;
			}
			e.word = word;
			e.time = eventTime;
			e.tDelta = timeSinceLastChange;
			e.relativePosition = relativePosition;
			result.wordEvents.add(e);
		}
	};

	private HideAndSeekTextView.OnCharChangeListener charChangeListener = new HideAndSeekTextView.OnCharChangeListener() {
		@Override
		public void OnCharChange(int charIndex, String character, long eventTime, long timeSinceLastChange, int relativePosition) {
			// Filter out spurious changes from events outside of the sentence.
			// Only want one of these each time we move outside of the sentence,
			// starting or moving to.
			if (charIndex == -1 && result.charEvents.size() > 0 && result.charEvents.lastElement().charIndex == -1) {
				return;
			}

			// Build an event and log it.
			CharEvent e = new CharEvent();
			e.charIndex = charIndex + 1;
			if (result.charEvents.size() > 0) {
				e.charIndexDelta = charIndex - result.charEvents.lastElement().charIndex;
				result.charEvents.lastElement().timeSpent = timeSinceLastChange;
			}
			e.character = character;
			e.time = eventTime;
			e.tDelta = timeSinceLastChange;
			e.relativePosition = relativePosition;
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
			e.time = System.currentTimeMillis();
			if (result.seekEvents.size() > 0) {
				e.tDelta = e.time - result.seekEvents.lastElement().time;
			} else {
				e.tDelta = e.time - baseSeekTime;
			}

			int fingerXPos = progress - seekBarMargin;
			if (fingerXPos < 0) {
				e.relativePosition = SeekEvent.ANTE_SENTENTIUM;
			} else if (fingerXPos >= seekBarMax - 2 * seekBarMargin) {
				e.relativePosition = SeekEvent.POST_SENTENTIUM;
			} else {
				e.relativePosition = SeekEvent.IN_SENTENTIUM;
			}

			e.x = fingerXPos;
			e.word = sentenceView.getWord();
			e.character = sentenceView.getChar();
			e.wordIndex = sentenceView.getWordIndex() + 1;
			e.charIndex = sentenceView.getCharIndex() + 1;
			if (result.seekEvents.size() > 0) {
				e.xDelta = e.x - result.seekEvents.lastElement().x;
				e.wordIndexDelta = e.wordIndex - result.seekEvents.lastElement().wordIndex;
				e.charIndexDelta = e.charIndex - result.seekEvents.lastElement().charIndex;
			}

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
			if (result.seekEvents.lastElement().x == seekBarMax - seekBarMargin) {
				main.addSentenceResult(result);
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
		result.sentenceId = getArguments().getInt(KEY_INDEX, 0);

		if (mode == ExperimentActivity.MODE_PRACTICE) {
			result.sentence = getResources().getStringArray(R.array.practice_sentences)[result.sentenceId];
			questions = getResources().getIntArray(R.array.practice_questions);
			result.condition1 = 4;
			result.condition2 = 5;
		} else if (mode == ExperimentActivity.MODE_EXPERIMENT1) {
			result.sentence = getResources().getStringArray(R.array.list1_sentences)[result.sentenceId];
			questions = getResources().getIntArray(R.array.list1_questions);
			result.condition1 = getResources().getIntArray(R.array.list1_conditions1)[result.sentenceId];
			result.condition2 = getResources().getIntArray(R.array.list1_conditions2)[result.sentenceId];
		} else {
			result.sentence = getResources().getStringArray(R.array.list2_sentences)[result.sentenceId];
			questions = getResources().getIntArray(R.array.list2_questions);
			result.condition1 = getResources().getIntArray(R.array.list2_conditions1)[result.sentenceId];
			result.condition2 = getResources().getIntArray(R.array.list2_conditions2)[result.sentenceId];
		}

		View v = inflater.inflate(R.layout.sentence_page, container, false);

		// Sentence
		sentenceView = (HideAndSeekTextView) v.findViewById(R.id.sentence_text);
		sentenceView.setText(result.sentence);

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

		baseSeekTime = System.currentTimeMillis();

		return v;
	}

	/**
	 * Show the question for this sentence.
	 * 
	 * @return False if there is not a question for this sentence.
	 */
	protected boolean showQuestion() {
		boolean isQuestion = result.sentenceId < questions.length;
		if (isQuestion) {
			Fragment question = QuestionFragment.newInstance(main, result.sentenceId, mode);
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(android.R.id.content, question);
			ft.setTransition(FragmentTransaction.TRANSIT_NONE);
			ft.commit();
		}
		return isQuestion;
	}
}
