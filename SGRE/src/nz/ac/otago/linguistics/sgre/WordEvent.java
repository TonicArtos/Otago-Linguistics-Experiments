package nz.ac.otago.linguistics.sgre;

import java.io.IOException;

import android.util.JsonWriter;

/**
 * A specific account of user interaction when reading a word.
 * 
 * @author Tonic Artos
 */
public class WordEvent implements JSONData {
	/**
	 * Milliseconds the event occurred at.
	 */
	public long time;
	/**
	 * Milliseconds since the last word change. First word change is milliseconds since the sentence was first shown. 
	 */
	public long tDelta;
	/**
	 * Milliseconds spent on this word.
	 */
	public long timeSpent;
	/**
	 * Whether the event was before or after the sentence. -1, 0, 1 for ante, in, and post.
	 */
	public int relativePosition;
	
	public int wordIndex;
	public int wordIndexDelta;
	public String word;
	
	@Override
	public void toJSON(JsonWriter out) throws IOException {
		out.beginObject();
		out.name("word_index").value(wordIndex);
		out.name("word_index_delta").value(wordIndexDelta);
		out.name("relative_position").value(relativePosition);
		out.name("word").value(word);
		out.name("event_time").value(time);
		out.name("time_since_last_event").value(tDelta);
		out.name("time_spent_on_word").value(timeSpent);
		out.endObject();
	}
}