package nz.ac.otago.linguistics.obsproj1;

import java.util.Vector;


/**
 * A result type which stores user interaction while reading sentences.
 * 
 * @author Tonic Artos
 */
public class SentenceResult implements ExperimentActivity.Result {
	/**
	 * @serial
	 */
	protected String sentence;
	/**
	 * @serial
	 */
	protected int sentenceIndex;
	/**
	 * @serial
	 */
	protected Vector<Click> clicks = new Vector<Click>();
	
	@Override
	public String toString() {
		String s = "";
		s += "Sentence ID, " + sentenceIndex + "\n";
		for (Click click : clicks) {
			s += click.getWord();
		}
		s += "\n";
		for (Click click : clicks) {
			s += click.getTimeString();
		}
		s += "\n";
		return s;
	}
}