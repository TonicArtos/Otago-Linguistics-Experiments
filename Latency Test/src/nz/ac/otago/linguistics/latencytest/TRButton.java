package nz.ac.otago.linguistics.latencytest;

import android.content.Context;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.text.format.Time;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;

public class TRButton extends Button {
	public TRButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public TRButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TRButton(Context context) {
		super(context);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.d("Button Touched", String.valueOf(event.getEventTime()));
		boolean b = super.onTouchEvent(event);
		return b;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		Log.d("Start Draw Button", String.valueOf(SystemClock.uptimeMillis()));
		super.onDraw(canvas);
		Log.d("Finish Draw Button", String.valueOf(SystemClock.uptimeMillis()));
	}

}
