package nz.ac.otago.linguistics.sgre;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Vector;

import android.util.JsonWriter;

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
	protected Vector<SeekEvent> seekEvents = new Vector<SeekEvent>();
	protected Vector<WordEvent> wordEvents = new Vector<WordEvent>();
	protected Vector<CharEvent> charEvents = new Vector<CharEvent>();
	private static final String[] condition1Text = new String[] { "Filler", "Relative Clause", "Adverb", "Coordination", "Tutorial" };
	public static final String[] condition2Text = new String[] { "Filler", "High Attachment", "Low Attachment", "Comma", "No Comma", "Tutorial" };

	@Override
	public void toJSON(JsonWriter w) throws IOException {
		w.beginObject();
		w.name("sentence_id").value(sentenceIndex + 1);
		w.name("condition_1").value(condition1Text[(condition1 < 0 || condition1Text.length <= condition1) ? 0 : condition1]);
		w.name("condition_2").value(condition2Text[(condition2 < 0 || condition2Text.length <= condition2) ? 0 : condition2]);
		w.name("word_indices").beginArray();
		for (WordEvent word : wordEvents) {
			w.value(word.word);
		}
		w.endArray();
		w.name("words").beginArray();
		for (WordEvent word : wordEvents) {
			w.value(word.word);
		}
		w.endArray();
		w.name("word_times").beginArray();
		for (WordEvent word : wordEvents) {
			w.value(word.word);
		}
		w.endArray();
		w.name("word_times").beginArray();
		for (WordEvent word : wordEvents) {
			w.value(word.word);
		}
		w.endArray();
		w.endObject();
	}
}