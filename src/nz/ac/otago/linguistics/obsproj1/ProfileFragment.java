package nz.ac.otago.linguistics.obsproj1;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

public class ProfileFragment extends Fragment {
	protected ExperimentActivity main;
	
	private View.OnClickListener onContinueClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (inputComplete()) {
				// Show the tutorial panel.
				TutorialFragment response = TutorialFragment.newInstance(main);
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
	
	public static ProfileFragment newInstance(ExperimentActivity main) {
		ProfileFragment f = new ProfileFragment();
		f.main = main;
		return f;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.landing_page, container, false);
		//TODO: Reserve an ID.
		((Button) v.findViewById(R.id.button_continue)).setOnClickListener(onContinueClickListener);
		return v;
	}

	protected boolean inputComplete() {
		RadioButton engYes = ((RadioButton)getView().findViewById(R.id.radio_english_yes));
		RadioButton engNo= ((RadioButton)getView().findViewById(R.id.radio_english_no));
		if (engYes.isChecked() == engNo.isChecked()) {
			return false;
		}

		RadioButton rhYes = ((RadioButton)getView().findViewById(R.id.radio_right_handed_yes));
		RadioButton rhNo= ((RadioButton)getView().findViewById(R.id.radio_right_handed_no));
		if (rhYes.isChecked() == rhNo.isChecked()) {
			return false;
		}
		
		result = new ProfileResult();
		result.englishFirst = engYes.isChecked();
		result.rightHanded = rhYes.isChecked();
		main.addResult(result);
		
		return true;
	}
}