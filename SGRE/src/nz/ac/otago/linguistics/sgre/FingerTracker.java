package nz.ac.otago.linguistics.sgre;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

public class FingerTracker extends SeekBar {
	private boolean isGoodTouch;
	private boolean filtered;

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
		// Override normal touch behaviour so that the user can only interact if
		// they are touching the thumb.
		// get position.
		// If touch is nearby the progress marker, pass it through.
		int thumbPixelPos = (int)  getProgress();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            	if ((event.getX() - 25) < thumbPixelPos && thumbPixelPos < (event.getX() + 25)) {
            		isGoodTouch = true;
            		return super.onTouchEvent(event);
            	}
            	break;
            case MotionEvent.ACTION_MOVE:
            	if (isGoodTouch) {
            		if (getProgress() == getMax()) {
            			getProgressDrawable().setColorFilter(0xFF00FF00, Mode.MULTIPLY);
            			filtered = true;
            		} else if (filtered) {
            			getProgressDrawable().setColorFilter(null);
            			filtered = false;
            		}
            		return super.onTouchEvent(event);
            	}
            	break;
            case MotionEvent.ACTION_UP:
            	if (isGoodTouch) {
            		isGoodTouch = false;
            		return super.onTouchEvent(event);
            	}
            	break;
            case MotionEvent.ACTION_CANCEL:
        		return super.onTouchEvent(event);
        }
		return true;
	}
}
