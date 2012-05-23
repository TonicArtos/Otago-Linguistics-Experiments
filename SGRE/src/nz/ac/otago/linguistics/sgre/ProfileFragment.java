package nz.ac.otago.linguistics.sgre;


import com.michaelnovakjr.numberpicker.NumberPicker;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileFragment extends Fragment {
	protected ExperimentActivity main;

	private View.OnClickListener onContinueClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (inputComplete()) {
				// Show the tutorial panel.
				TutorialFragment response = TutorialFragment.newInstance(main, TutorialFragment.MODE_PART_1);
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.replace(android.R.id.content, response);
				ft.setTransition(FragmentTransaction.TRANSIT_NONE);
				ft.commit();
			} else {
				Toast.makeText(getActivity(), R.string.text_complain_incomplete_info, Toast.LENGTH_SHORT).show();
			}
		}
	};

	private ProfileResult result;

	private int mode;

	public static ProfileFragment newInstance(ExperimentActivity main, int mode) {
		ProfileFragment f = new ProfileFragment();
		f.main = main;
		f.mode = mode;
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.landing_page, container, false);
		// Assume this session ID will be saved as last ID + 1.
		((TextView) v.findViewById(R.id.text_subject_id)).setText("ID: "
				+ String.format("%s%04d", (mode == ExperimentActivity.MODE_EXPERIMENT1) ? "a" : "b", main.sessionId));
		((Button) v.findViewById(R.id.button_continue)).setOnClickListener(onContinueClickListener);
		return v;
	}

	protected boolean inputComplete() {
		RadioButton engYes = ((RadioButton) getView().findViewById(R.id.radio_english_yes));
		RadioButton engNo = ((RadioButton) getView().findViewById(R.id.radio_english_no));
		if (engYes.isChecked() == engNo.isChecked()) {
			return false;
		}

		RadioButton rhYes = ((RadioButton) getView().findViewById(R.id.radio_right_handed_yes));
		RadioButton rhNo = ((RadioButton) getView().findViewById(R.id.radio_right_handed_no));
		if (rhYes.isChecked() == rhNo.isChecked()) {
			return false;
		}

		result = new ProfileResult();
		result.sessionId = main.sessionId;
		result.dataSet = (mode == ExperimentActivity.MODE_EXPERIMENT1) ? "a" : "b";
		result.englishFirst = engYes.isChecked();
		result.rightHanded = rhYes.isChecked();
		result.age = ((NumberPicker) getView().findViewById(R.id.input_age)).getCurrent();
		result.gender = ((RadioButton) getView().findViewById(R.id.radio_gender_female)).isChecked() ? "Female" : "Male";
		main.addProfileResult(result);

		return true;
	}
}
