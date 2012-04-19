package nz.ac.otago.linguistics.spre;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;

public class QuestionResponseFragment extends Fragment {
	private static final String KEY_CORRECT_ANSWER = "correctAnswer";

	protected ExperimentActivity main;
	private int mode;

	public static QuestionResponseFragment newInstance(ExperimentActivity main, boolean correctAnswer, int mode) {
		QuestionResponseFragment f = new QuestionResponseFragment();
		f.main = main;
		f.mode = mode;
		// Set index as an argument
		Bundle args = new Bundle();
		args.putBoolean(KEY_CORRECT_ANSWER, correctAnswer);
		f.setArguments(args);

		return f;
	}

	private View.OnClickListener advListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mode == ExperimentActivity.MODE_PRACTICE) {
				main.showNextPracticeSentence();
			} else {
				main.showNextSentence();
			}
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.text_page, container, false);
		String responseText;
		if (getArguments().getBoolean(KEY_CORRECT_ANSWER)) {
			responseText = getResources().getString(R.string.good_response);
		} else {
			responseText = getResources().getString(R.string.poor_response);
		}
		((TextView) v.findViewById(R.id.text)).setText(responseText);
		((Button) v.findViewById(R.id.adv_button)).setOnClickListener(advListener );
		return v;
		
	}
}
