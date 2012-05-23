package nz.ac.otago.linguistics.sgre;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

public class FingerTracker extends SeekBar {
	public FingerTracker(Context context) {
		super(context);
	}

	public FingerTracker(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FingerTracker(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// Override normal touch behaviour so that the user can only interact if they are touching the thumb.
		//get position.
		// If touch is nearby the progress marker, pass it through.
		return super.onTouchEvent(event);
	}
}
