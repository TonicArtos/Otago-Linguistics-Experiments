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
public class WordEvent implements JSONData, CSVData {
	/**
	 * Milliseconds the event occurred at.
	 */
	public long time;
	/**
	 * Milliseconds since the last word change. First word change is
	 * milliseconds since the sentence was first shown.
	 */
	public long tDelta;
	/**
	 * Milliseconds spent on this word.
	 */
	public long timeSpent;
	/**
	 * Whether the event was before or after the sentence. -1, 0, 1 for ante,
	 * in, and post.
	 */
	public int relativePosition;

	public int wordIndex;
	public int wordIndexDelta;
	public String word;
	public int pxWidth;
	private int charsInWord;

	@Override
	public void toJSON(JsonWriter out) throws IOException {
		out.beginObject();
		out.name("word_index").value(wordIndex + 1);
		out.name("word_index_delta").value(wordIndexDelta);
		out.name("relative_position").value(relativePosition);
		out.name("word").value(word);
		out.name("chars_in_word").value(word.length());
		out.name("pixels_in_word").value(pxWidth);
		out.name("event_time").value(time);
		out.name("time_since_last_event").value(tDelta);
		out.name("time_spent_on_word").value(timeSpent);
		out.endObject();
	}

	public static void printHeaders(PrintWriter w) {
		w.print("word_index");
		w.print(",");
		w.print("word_index_delta");
		w.print(",");
		w.print("relative_position");
		w.print(",");
		w.print("word");
		w.print(",");
		w.print("chars_in_word");
		w.print(",");
		w.print("pixels_in_word");
		w.print(",");
		w.print("event_time");
		w.print(",");
		w.print("time_since_last_event");
		w.print(",");
		w.print("time_spent_on_word");
	}

	public static WordEvent readWordEvent(JsonReader reader) throws IOException {
		WordEvent we = new WordEvent();
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (TextUtils.equals(name, "word_index")) {
				we.wordIndex = reader.nextInt();
			} else if (TextUtils.equals(name, "word_index_delta")) {
				we.wordIndexDelta = reader.nextInt();
			} else if (TextUtils.equals(name, "relative_position")) {
				we.relativePosition = reader.nextInt();
			} else if (TextUtils.equals(name, "word")) {
				we.word = reader.nextString();
			} else if (TextUtils.equals(name, "chars_in_word")) {
				we.charsInWord = reader.nextInt();
			} else if (TextUtils.equals(name, "pixels_in_word")) {
				we.pxWidth = reader.nextInt();
			} else if (TextUtils.equals(name, "event_time")) {
				we.time = reader.nextLong();
			} else if (TextUtils.equals(name, "time_since_last_event")) {
				we.tDelta = reader.nextLong();
			} else if (TextUtils.equals(name, "time_spent_on_word")) {
				we.timeSpent = reader.nextLong();
			}
		}
		reader.endObject();
		return we;
	}

	@Override
	public void toCSV(PrintWriter w) {
		w.print(wordIndex);
		w.print(",");
		w.print(wordIndexDelta);
		w.print(",");
		w.print(relativePosition);
		w.print(",");
		w.print("\"" + word + "\"");
		w.print(",");
		w.print(charsInWord);
		w.print(",");
		w.print(pxWidth);
		w.print(",");
		w.print(time);
		w.print(",");
		w.print(tDelta);
		w.print(",");
		w.print(timeSpent);
	}
}