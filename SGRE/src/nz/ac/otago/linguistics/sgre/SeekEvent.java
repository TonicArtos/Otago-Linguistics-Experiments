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
public class SeekEvent implements JSONData, CSVData {
	public static final int ANTE_SENTENTIUM = -1;
	public static final int IN_SENTENTIUM = 0;
	public static final int POST_SENTENTIUM = 1;

	/**
	 * Milliseconds the event occurred at.
	 */
	protected long time;
	/**
	 * Milliseconds since last seek time.
	 */
	public long tDelta;
	/**
	 * x position of the slider relative to the sentence. Range is: -margin <=
	 * xPos < sentence length + margin.
	 */
	public int x;
	/**
	 * Change in x position since last seek event.
	 */
	public int xDelta;
	/**
	 * Whether the event was before or after the sentence. -1, 0, 1 for ante,
	 * in, and post.
	 */
	public int relativePosition;

	protected int charIndex;
	protected int charIndexDelta;
	protected String character;

	protected int wordIndex;
	protected int wordIndexDelta;
	protected String word;

	public static void printHeaders(PrintWriter w) {
		w.print("event_time");
		w.print(",");
		w.print("event_time_delta");
		w.print(",");
		w.print("x");
		w.print(",");
		w.print("x_delta");
		w.print(",");
		w.print("relative_position");
		w.print(",");

		w.print("char_index");
		w.print(",");
		w.print("char_index_delta");
		w.print(",");
		w.print("character");
		w.print(",");

		w.print("word_index");
		w.print(",");
		w.print("word_index_delta");
		w.print(",");
		w.print("word");
	}

	public static SeekEvent readSeekEvent(JsonReader reader) throws IOException {
		SeekEvent se = new SeekEvent();
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (TextUtils.equals(name, "event_time")) {
				se.time = reader.nextLong();
			} else if (TextUtils.equals(name, "event_time_delta")) {
				se.tDelta = reader.nextLong();
			} else if (TextUtils.equals(name, "x")) {
				se.x = reader.nextInt();
			} else if (TextUtils.equals(name, "x_delta")) {
				se.xDelta = reader.nextInt();
			} else if (TextUtils.equals(name, "relative_position")) {
				se.relativePosition = reader.nextInt();

			} else if (TextUtils.equals(name, "char_index")) {
				se.charIndex = reader.nextInt();
			} else if (TextUtils.equals(name, "char_index_delta")) {
				se.charIndexDelta = reader.nextInt();
			} else if (TextUtils.equals(name, "character")) {
				se.character = reader.nextString();

			} else if (TextUtils.equals(name, "word_index")) {
				se.wordIndex = reader.nextInt();
			} else if (TextUtils.equals(name, "word_index_delta")) {
				se.wordIndexDelta = reader.nextInt();
			} else if (TextUtils.equals(name, "word")) {
				se.word = reader.nextString();
			}
		}
		reader.endObject();
		return se;
	}

	@Override
	public void toJSON(JsonWriter out) throws IOException {
		out.beginObject();
		out.name("event_time").value(time);
		out.name("event_time_delta").value(tDelta);
		out.name("x").value(x);
		out.name("x_delta").value(xDelta);
		out.name("relative_position").value(relativePosition);

		out.name("char_index").value(charIndex + 1);
		out.name("char_index_delta").value(charIndexDelta);
		out.name("character").value(character);

		out.name("word_index").value(wordIndex + 1);
		out.name("word_index_delta").value(wordIndexDelta);
		out.name("word").value(word);
		out.endObject();
	}

	@Override
	public void toCSV(PrintWriter w) {
		w.print(time);
		w.print(",");
		w.print(tDelta);
		w.print(",");
		w.print(x);
		w.print(",");
		w.print(xDelta);
		w.print(",");
		w.print(relativePosition);
		w.print(",");

		w.print(charIndex);
		w.print(",");
		w.print(charIndexDelta);
		w.print(",");
		w.print(character);
		w.print(",");

		w.print(wordIndex);
		w.print(",");
		w.print(wordIndexDelta);
		w.print(",");
		w.print("\"" + word + "\"");
	}
}