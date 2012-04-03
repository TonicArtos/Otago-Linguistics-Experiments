package nz.ac.otago.linguistics.obsproj1;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.PorterDuff.Mode;
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
	protected static final int MODE_PART_1 = 0;
	protected static final int MODE_SENTENCE_DEMO = 1;
	protected static final int MODE_PART_2 = 2;

	protected ExperimentActivity main;
	protected int mode;
	protected TextView textView;
	protected HideAndSeekTextView sentenceView;
	protected int tutIndex;
	protected String[] tutParts;

	private View.OnClickListener advClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mode == MODE_SENTENCE_DEMO) {
				// Do word stepping
				if (sentenceView.step()) {
					sentenceView.setVisibility(View.INVISIBLE);
					
					// First page of the 2nd part of the tutorial must be a question page 
					textView.setText(tutParts[tutIndex]);
					getView().findViewById(R.id.buttons).setVisibility(View.VISIBLE);
					getView().findViewById(R.id.adv_button).setVisibility(View.GONE);
					mode = MODE_PART_2;
				}
			} else if (!nextTutText()) {
				// Start the experiment.
				main.showNextSentence();
			}
		}
	};

	public static TutorialFragment newInstance(ExperimentActivity main) {
		TutorialFragment f = new TutorialFragment();
		f.main = main;
		return f;
	}

	protected boolean nextTutText() {
		tutIndex += 1;
		if (tutIndex < tutParts.length) {
			textView.setText(tutParts[tutIndex]);
			
			if (mode == MODE_PART_2 && tutIndex == 1) {
				// turn off question part after first page of part 2
				getView().findViewById(R.id.buttons).setVisibility(View.GONE);
				getView().findViewById(R.id.adv_button).setVisibility(View.VISIBLE);
			}
		} else if (mode == MODE_PART_1) {
			// Change to demo sentence reading component.
			mode = MODE_SENTENCE_DEMO;
			sentenceView.setVisibility(View.VISIBLE);
			textView.setText(getResources().getString(R.string.explain_word_revealing));

			// Setup for part 2 after doing a demo of the sentence reading.
			tutIndex = 0;
			tutParts = getResources().getStringArray(R.array.tutorial_part_2);
		} else if (mode == MODE_PART_2) { 
			return false;
		}
		return true;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.tutorial_page, container, false);

		tutIndex = 0;
		tutParts = getResources().getStringArray(R.array.tutorial_part_1);
		
		textView = (TextView) v.findViewById(R.id.text);
		textView.setText(tutParts[tutIndex]);
		
		sentenceView = (HideAndSeekTextView) v.findViewById(R.id.demo_sentence);
		sentenceView.setVisibility(View.INVISIBLE);

		// Find buttons.
		Button yesButton = (Button) v.findViewById(R.id.yes_button);
		Button noButton = (Button) v.findViewById(R.id.no_button);

		// Colourise yes/no buttons.
		yesButton.getBackground().setColorFilter(0xFF008800, Mode.MULTIPLY);
		noButton.getBackground().setColorFilter(0xFF880000, Mode.MULTIPLY);
		
		yesButton.setOnClickListener(advClickListener);
		noButton.setOnClickListener(advClickListener);
		
		((Button) v.findViewById(R.id.adv_button)).setOnClickListener(advClickListener);

		v.findViewById(R.id.buttons).setVisibility(View.GONE);

		mode = MODE_PART_1;
		return v;
	}
}
