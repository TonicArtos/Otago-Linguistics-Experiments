package nz.ac.otago.linguistics.spre;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;


public class ClearDataDialogFragment extends DialogFragment {
	private OnInputListener onInputListener;

	public static ClearDataDialogFragment newInstance(OnInputListener onInputListener) {
		ClearDataDialogFragment f = new ClearDataDialogFragment();
		f.onInputListener = onInputListener;
		return f;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		        return new AlertDialog.Builder(getActivity())
        .setIcon(android.R.drawable.ic_menu_delete)
        .setTitle(R.string.text_clear_data)
        .setPositiveButton(android.R.string.yes,
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	onInputListener.onPositiveClick();
                }
            }
        )
        .setNegativeButton(android.R.string.no,
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	onInputListener.onNegativeClick();
                }
            }
        )
        .create();
	}
	
	public interface OnInputListener {
		public abstract void onPositiveClick();
		public abstract void onNegativeClick();
	}
}

