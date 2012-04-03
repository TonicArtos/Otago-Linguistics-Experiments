package nz.ac.otago.linguistics.obsproj1;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Vector;


/**
 * A result type which stores user interaction while reading sentences.
 * 
 * @author Tonic Artos
 */
public class SentenceResult implements ExperimentActivity.Result {
	private static final long serialVersionUID = -5618372457459106543L;
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
	public void write(BufferedWriter out) throws IOException {
		out.write("SentenceID, " + sentenceIndex + "\n");
		for (Click click : clicks) {
			click.writeWord(out);
		}
		out.write("\n");
		for (Click click : clicks) {
			click.writeTime(out);
		}
		out.write("\n");
	}
}