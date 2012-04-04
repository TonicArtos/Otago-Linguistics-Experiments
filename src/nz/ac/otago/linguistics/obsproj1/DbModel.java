package nz.ac.otago.linguistics.obsproj1;

import android.text.format.Time;

public abstract class DbModel {
    public static final String KEY_ROWID = "_id";
    
	/**
	 * Current time stamp.
	 * @return Milliseconds since epoch.
	 */
    public static long now() {
        Time now = new Time();
        now.setToNow();
        return now.toMillis(false);
    }
    
    /**
     * Parse time stamp into Time value.
     * @param millis
     * @return Parsed time.
     */
    public static Time toTime(long millis) {
    	Time time = new Time();
    	time.set(millis);
    	return time;
    }

    public long id = 0;

    public DbModel() {}
}
