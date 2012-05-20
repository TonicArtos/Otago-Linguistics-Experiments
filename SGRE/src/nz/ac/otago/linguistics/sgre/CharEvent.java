package nz.ac.otago.linguistics.sgre;

/**
 * A specific account of user interaction when reading a word.
 * 
 * @author Tonic Artos
 */
public class CharEvent {
	/**
	 * Milliseconds the event occurred at.
	 */
	public long millis;
	/**
	 * Milliseconds since the last word change. First word change is millis since the sentence was first shown. 
	 */
	public long delta;
	public int charIndex;
	public String character;
}