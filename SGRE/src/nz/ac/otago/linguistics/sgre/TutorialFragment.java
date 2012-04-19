package nz.ac.otago.linguistics.sgre;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * The tutorial panel runs the subject through a simple tutorial.
 * 
 * @author Tonic Artos
 * 
 */
public class TutorialFragment extends Fragment {
	public static final int MODE_PART_1 = 0;
	public static final int MODE_PART_2 = 1;

	protected ExperimentActivity main;
	protected int mode;
	protected TextView textView;
	protected int tutIndex;
	protected String[] tutParts;

	private View.OnClickListener advClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (!nextTutText()) {
				if (mode == MODE_PART_1) {
					// start tutorial sentences and questions
					main.showNextPracticeSentence();
				} else {
					// Start the experiment.
					main.showNextSentence();
				}
			}
		}
	};

	public static TutorialFragment newInstance(ExperimentActivity main, int mode) {
		TutorialFragment f = new TutorialFragment();
		f.main = main;
		f.mode = mode;
		return f;
	}

	protected boolean nextTutText() {
		tutIndex += 1;
		if (tutIndex < tutParts.length) {
			textView.setText(tutParts[tutIndex]);
			return true;
		}
		return false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.tutorial_page, container, false);

		tutIndex = 0;
		if (mode == MODE_PART_1) {
			tutParts = getResources().getStringArray(R.array.tutorial_part_1);
		} else {
			tutParts = getResources().getStringArray(R.array.tutorial_part_2);
		}

		textView = (TextView) v.findViewById(R.id.text);
		textView.setText(tutParts[tutIndex]);

		((Button) v.findViewById(R.id.adv_button)).setOnClickListener(advClickListener);

		return v;
	}
}
