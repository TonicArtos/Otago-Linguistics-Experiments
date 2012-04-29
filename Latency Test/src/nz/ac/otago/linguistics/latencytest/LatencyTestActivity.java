package nz.ac.otago.linguistics.latencytest;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class LatencyTestActivity extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ((Button) findViewById(R.id.button1)).setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		Log.d("OnClick Registered", String.valueOf(SystemClock.uptimeMillis()));
	}
}