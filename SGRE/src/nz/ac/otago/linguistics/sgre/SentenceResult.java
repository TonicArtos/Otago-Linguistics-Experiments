package nz.ac.otago.linguistics.sgre;

import java.io.IOException;
import java.util.Vector;

import android.util.JsonWriter;

/**
 * A result type which stores user interaction while reading sentences.
 * 
 * @author Tonic Artos
 */
public class SentenceResult implements JSONData {
	public int sentenceId;
	public String sentence;
	public int condition1;
	public int condition2;
	public Vector<SeekEvent> seekEvents = new Vector<SeekEvent>();
	public Vector<WordEvent> wordEvents = new Vector<WordEvent>();
	public Vector<CharEvent> charEvents = new Vector<CharEvent>();
	
	private QuestionResult questionResult;
	private static final String[] condition1Text = new String[] { "Filler", "Relative Clause", "Adverb", "Coordination", "Tutorial" };
	private static final String[] condition2Text = new String[] { "Filler", "High Attachment", "Low Attachment", "Comma", "No Comma", "Tutorial" };

	@Override
	public void toJSON(JsonWriter out) throws IOException {
		out.beginObject();
		
		out.name("sentence_id").value(sentenceId + 1);
		out.name("sentence").value(sentence);
		out.name("condition_1").value(condition1Text[(condition1 < 0 || condition1Text.length <= condition1) ? 0 : condition1]);
		out.name("condition_2").value(condition2Text[(condition2 < 0 || condition2Text.length <= condition2) ? 0 : condition2]);
		
		out.name("word_events").beginArray();
		for (WordEvent we : wordEvents) {
			we.toJSON(out);
		}
		out.endArray();
		
		out.name("char_events").beginArray();
		for (CharEvent ce : charEvents) {
			ce.toJSON(out);
		}
		out.endArray();
		
		out.name("seek_events").beginArray();
		for (SeekEvent se : seekEvents) {
			se.toJSON(out);
		}
		out.endArray();
		
		if (questionResult != null) {
			out.name("question_result");
			questionResult.toJSON(out);
		}
		
		out.endObject();
	}

	public void addQuestionResult(QuestionResult result) {
		questionResult = result;
	}
}