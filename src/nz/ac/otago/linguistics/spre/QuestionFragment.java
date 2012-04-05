package nz.ac.otago.linguistics.spre;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class QuestionFragment extends Fragment {
	private ExperimentActivity main;
	protected QuestionResult result;

	private View.OnClickListener yesClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			handleAnswer(QuestionResult.ANSWERED_YES);
		}
	};

	private View.OnClickListener noClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			handleAnswer(QuestionResult.ANSWERED_NO);
		}
	};
	private long timestamp;

	public static QuestionFragment newInstance(ExperimentActivity main, int index) {
		QuestionFragment f = new QuestionFragment();
		f.main = main;

		// Set index as an argument
		Bundle args = new Bundle();
		args.putInt("index", index);
		f.setArguments(args);

		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		int index = getArguments().getInt("index", 0);
		String question = getResources().getStringArray(R.array.questions)[index];

		// Start building our question result which we will store later.
		result = new QuestionResult();
		result.questionId = index;
		result.question = question;

		View v = inflater.inflate(R.layout.question_page, container, false);
		
		((TextView) v.findViewById(R.id.question_text)).setText(question);

		// Find buttons.
		Button yesButton = (Button) v.findViewById(R.id.yes_button);
		Button noButton = (Button) v.findViewById(R.id.no_button);

		// Colourise yes/no buttons.
		yesButton.getBackground().setColorFilter(0xFF008800, Mode.MULTIPLY);
		noButton.getBackground().setColorFilter(0xFF880000, Mode.MULTIPLY);

		// Bind actions to buttons.
		yesButton.setOnClickListener(yesClickListener);
		noButton.setOnClickListener(noClickListener);

		timestamp = System.currentTimeMillis();
		
		return v;
	}

	/**
	 * Check the answer to the current question. Provides feedback to the user
	 * based on the result of their answer and continues to the next question.
	 * 
	 * @param answeredYes
	 *            True if the user answered yes to the question.
	 */
	private void handleAnswer(int answer) {
		// Finish building response and save it.
		result.answer = answer;
		result.correctAnswer = getResources().getIntArray(R.array.answers)[result.questionId] == answer;
		result.responseTime = System.currentTimeMillis() - timestamp;
		main.addResult(result);
		
		// Show the question response panel.
		QuestionResponseFragment response = QuestionResponseFragment.newInstance(main, result.correctAnswer);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(android.R.id.content, response);
		ft.setTransition(FragmentTransaction.TRANSIT_NONE);
		ft.commit();
	}
}
