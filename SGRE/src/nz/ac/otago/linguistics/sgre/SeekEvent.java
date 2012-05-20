package nz.ac.otago.linguistics.sgre;

/**
 * A specific account of user interaction when reading a word.
 * 
 * @author Tonic Artos
 */
public class SeekEvent {
	public static final int ANTE = -1;
	public static final int POST = 1;
	
	protected int wordIndex;
	/**
	 * Milliseconds the event occurred at.
	 */
	protected long millis;
	protected String word;
	protected String character;
	/**
	 * Whether the event was before or after the sentence.
	 */
	public int relativeLocation;
	/**
	 * x position of the slider relative to the sentence. Range is: -margin <= xPos < sentence length + margin.
	 */
	public int xPos;

	public String getWord() {
		return word + ", ";
	}

	public String getTimeString() {
		return millis + ", ";
	}
}