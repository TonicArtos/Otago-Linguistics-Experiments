package nz.ac.otago.linguistics.sgre;

import java.io.IOException;

import android.util.JsonWriter;

/**
 * A specific account of user interaction when reading a word.
 * 
 * @author Tonic Artos
 */
public class CharEvent implements JSONData {
	/**
	 * Milliseconds the event occurred at.
	 */
	public long time;
	/**
	 * Milliseconds since the last character change. First character change will be milliseconds since the sentence was first shown. 
	 */
	public long tDelta;
	/**
	 * Milliseconds spent on this character.
	 */
	public long timeSpent;
	/**
	 * Whether the event was before or after the sentence. -1, 0, 1 for ante, in, and post.
	 */
	public int relativePosition;
	
	public int charIndex;
	public int charIndexDelta;
	public String character;
	
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
}