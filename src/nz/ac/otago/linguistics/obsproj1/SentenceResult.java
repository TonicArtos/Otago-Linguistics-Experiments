package nz.ac.otago.linguistics.obsproj1;

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
	
	@Override
	public String toString() {
		String s = "";
		s += "Sentence ID, " + (sentenceIndex + 1) + "\n";
		String conditionText;
		switch (condition1) {
		case 1:
			conditionText = "Relative Clause";
			break;
		case 2:
			conditionText = "Adverb";
			break;
		case 3:
			conditionText = "Coordination";
			break;
		default:
			conditionText = "Filler";
			break;
		}
		s += "Condition1," + conditionText + "\n";
		switch (condition2) {
		case 1:
			conditionText = "High";
			break;
		case 2:
			conditionText = "Low";
			break;
		default:
			conditionText = "Filler";
			break;
		}
		s += "Condition2," + conditionText + "\n";
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