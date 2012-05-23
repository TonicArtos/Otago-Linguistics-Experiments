package nz.ac.otago.linguistics.sgre;

import java.io.IOException;

import android.util.JsonWriter;

/**
 * A specific account of user interaction when reading a word.
 * 
 * @author Tonic Artos
 */
public class SeekEvent implements JSONData {
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
	 * x position of the slider relative to the sentence. Range is: -margin <= xPos < sentence length + margin.
	 */
	public int x;
	/**
	 * Change in x position since last seek event.
	 */
	public int xDelta;
	/**
	 * Whether the event was before or after the sentence. -1, 0, 1 for ante, in, and post.
	 */
	public int relativePosition;
	
	protected int charIndex;
	protected int charIndexDelta;
	protected String character;
	
	protected int wordIndex;
	protected int wordIndexDelta;
	protected String word;

	@Override
	public void toJSON(JsonWriter out) throws IOException {
		out.beginObject();
		out.name("event_time").value(time);
		out.name("event_time_delta").value(tDelta);
		out.name("x").value(x);
		out.name("x_delta").value(xDelta);
		out.name("relative_position").value(relativePosition);
		
		out.name("char_index").value(charIndex);
		out.name("char_index_delta").value(charIndexDelta);
		out.name("character").value(character);
		
		out.name("word_index").value(wordIndex);
		out.name("word_index_delta").value(wordIndexDelta);
		out.name("word").value(word);
		out.endObject();
	}
}