package nz.ac.otago.linguistics.sgre;

import java.util.Vector;

/**
 * A result type which stores user interaction while reading sentences.
 * 
 * @author Tonic Artos
 */
public class SentenceResult implements ExperimentActivity.Result {
	protected String sentence;
	protected int sentenceIndex;
	public int condition1;
	public int condition2;
	protected Vector<Click> clicks = new Vector<Click>();
	private static final String[] condition1Text = new String[] { "Filler", "Relative Clause", "Adverb", "Coordination", "Tutorial" };
	public static final String[] condition2Text = new String[] { "Filler", "High Attachment", "Low Attachment", "Comma", "No Comma", "Tutorial" };

	@Override
	public String toString() {
		String s = "";
		s += "\nSentence ID, " + (sentenceIndex + 1) + "\n";
		;
		// Normalise index and fetch matching strings
		s += "Condition1," + condition1Text[(condition1 < 0 || condition1Text.length <= condition1) ? 0 : condition1] + "\n";
		s += "Condition2," + condition2Text[(condition2 < 0 || condition2Text.length <= condition2) ? 0 : condition2] + "\n";
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