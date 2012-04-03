package nz.ac.otago.linguistics.obsproj1;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;

/**
 * A specific account of user interaction when reading a word.
 * 
 * @author Tonic Artos
 */
public class Click implements Serializable {
	private static final long serialVersionUID = 6236299802595009232L;
	/**
	 * @serial
	 */
	protected int wordIndex;
	/**
	 * Milliseconds till the button was pressed. This is counted from the start of the word being displayed.
	 * @serial
	 */
	protected long millis;
	/**
	 * @serial
	 */
	protected String word;
	
	public void writeWord(BufferedWriter out) throws IOException {
		out.write(word + ", ");
	}
	
	public void writeTime(BufferedWriter out) throws IOException {
		out.write(millis + ", ");
	}
}