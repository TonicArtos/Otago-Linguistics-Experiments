package nz.ac.otago.linguistics.spre;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class SentenceFragment extends Fragment {
	private static final String KEY_INDEX = "index";

	protected ExperimentActivity main;
	private HideAndSeekTextView sentenceView;
	private long timestamp;
	protected SentenceResult result;
	private int mode;

	private View.OnClickListener advClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			long nt = System.currentTimeMillis();			
			// Store click information.
			Click click = new Click();
			click.millis = nt - timestamp;
			click.word = sentenceView.getWord();
			click.wordIndex = sentenceView.getWordIndex();
			result.clicks.add(click);
			
			timestamp = nt;
			
			if (sentenceView.step()) {
				main.addResult(result);
				if (!showQuestion()) {
					nextSentence();
				}
			}
		}
	};

	private int[] questions;

	
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

	protected void nextSentence() {
		if (mode == ExperimentActivity.MODE_PRACTICE) {
			main.showNextPracticeSentence();
		}else {
			main.showNextSentence();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Start building result to store after the whole sentence has been read.
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
		
		// Setup widgets.
		sentenceView = (HideAndSeekTextView) v.findViewById(R.id.sentence_text);
		sentenceView.setText(result.sentence);
		((Button) v.findViewById(R.id.adv_button)).setOnClickListener(advClickListener);
		
		timestamp = System.currentTimeMillis();
		
		return v;
	}

	/**
	 * Show the question for this sentence.
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
