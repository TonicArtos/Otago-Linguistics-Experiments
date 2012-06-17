package nz.ac.otago.linguistics.sgre;

import java.io.IOException;
import java.io.PrintWriter;

import android.text.TextUtils;
import android.util.JsonReader;
import android.util.JsonWriter;

/**
 * A specific account of user interaction when reading a word.
 * 
 * @author Tonic Artos
 */
public class CharEvent implements JSONData, CSVData {
	/**
	 * Milliseconds the event occurred at.
	 */
	public long time;
	/**
	 * Milliseconds since the last character change. First character change will
	 * be milliseconds since the sentence was first shown.
	 */
	public long tDelta;
	/**
	 * Milliseconds spent on this character.
	 */
	public long timeSpent;
	/**
	 * Whether the event was before or after the sentence. -1, 0, 1 for ante,
	 * in, and post.
	 */
	public int relativePosition;

	public int charIndex;
	public int charIndexDelta;
	public String character;

	public static void printHeaders(PrintWriter w) {
		w.print("char_index");
		w.print(",");
		w.print("char_index_delta");
		w.print(",");
		w.print("relative_position");
		w.print(",");
		w.print("character");
		w.print(",");
		w.print("event_time");
		w.print(",");
		w.print("time_since_last_event");
		w.print(",");
		w.print("time_spent_on_char");
	}

	@Override
	public void toJSON(JsonWriter out) throws IOException {
		out.beginObject();
		out.name("char_index").value(charIndex + 1);
		out.name("char_index_delta").value(charIndexDelta);
		out.name("relative_position").value(relativePosition);
		out.name("character").value(character);
		out.name("event_time").value(time);
		out.name("time_since_last_event").value(tDelta);
		out.name("time_spent_on_char").value(timeSpent);
		out.endObject();
	}

	public static CharEvent readCharEvent(JsonReader reader) throws IOException {
		CharEvent ce = new CharEvent();
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (TextUtils.equals(name, "char_index")) {
				ce.charIndex = reader.nextInt();
			} else if (TextUtils.equals(name, "char_index_delta")) {
				ce.charIndexDelta = reader.nextInt();
			} else if (TextUtils.equals(name, "relative_position")) {
				ce.relativePosition = reader.nextInt();
			} else if (TextUtils.equals(name, "character")) {
				ce.character = reader.nextString();
			} else if (TextUtils.equals(name, "event_time")) {
				ce.time = reader.nextLong();
			} else if (TextUtils.equals(name, "time_since_last_event")) {
				ce.tDelta = reader.nextLong();
			} else if (TextUtils.equals(name, "time_spent_on_char")) {
				ce.timeSpent = reader.nextLong();
			}
		}
		reader.endObject();
		return ce;
	}

	@Override
	public void toCSV(PrintWriter w) {
		w.print(charIndex);
		w.print(",");
		w.print(charIndexDelta);
		w.print(",");
		w.print(relativePosition);
		w.print(",");
		w.print("\"" + character + "\"");
		w.print(",");
		w.print(time);
		w.print(",");
		w.print(tDelta);
		w.print(",");
		w.print(timeSpent);
	}
}