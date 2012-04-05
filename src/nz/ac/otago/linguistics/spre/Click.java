package nz.ac.otago.linguistics.spre;

/**
 * A specific account of user interaction when reading a word.
 * 
 * @author Tonic Artos
 */
public class Click {
	protected int wordIndex;
	/**
	 * Milliseconds till the button was pressed. This is counted from the start of the word being displayed.
	 */
	protected long millis;
	protected String word;
	

	public String getWord() {
		return word + ", ";
	}

	public String getTimeString() {
		return millis + ", ";
	}
}