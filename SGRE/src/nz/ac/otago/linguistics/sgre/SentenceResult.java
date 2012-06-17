package nz.ac.otago.linguistics.sgre;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import android.text.TextUtils;
import android.util.JsonReader;
import android.util.JsonWriter;

/**
 * A result type which stores user interaction while reading sentences.
 * 
 * @author Tonic Artos
 */
public class SentenceResult implements JSONData, CSVData {
	public int sentenceId;
	public String sentence;
	public int condition1;
	public int condition2;
	private ArrayList<SeekEvent> seekEvents = new ArrayList<SeekEvent>();
	private ArrayList<WordEvent> wordEvents = new ArrayList<WordEvent>();
	private ArrayList<CharEvent> charEvents = new ArrayList<CharEvent>();

	private QuestionResult questionResult;
	private String condition1text;
	private String condition2text;
	private static final String[] condition1Text = new String[] { "Filler", "Relative Clause", "Adverb", "Coordination", "Tutorial" };
	private static final String[] condition2Text = new String[] { "Filler", "High Attachment", "Low Attachment", "Comma", "No Comma", "Tutorial" };

	public static void printHeaders(PrintWriter w) {
		w.print("sentence_id");
		w.print(",");
		w.print("sentence");
		w.print(",");
		w.print("condition_1");
		w.print(",");
		w.print("condition_2");
		w.print(",");
		QuestionResult.printHeaders(w);
	}

	public static SentenceResult readSentence(JsonReader reader) throws IOException {
		SentenceResult sentence = new SentenceResult();
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (TextUtils.equals(name, "sentence_id")) {
				sentence.sentenceId = reader.nextInt();
			} else if (TextUtils.equals(name, "sentence")) {
				sentence.sentence = reader.nextString();
			} else if (TextUtils.equals(name, "condition_1")) {
				sentence.condition1text = reader.nextString();
			} else if (TextUtils.equals(name, "condition_2")) {
				sentence.condition2text = reader.nextString();
			} else if (TextUtils.equals(name, "word_events")) {
				sentence.wordEvents = readWordEvents(reader);
			} else if (TextUtils.equals(name, "char_events")) {
				sentence.charEvents = readCharEvents(reader);
			} else if (TextUtils.equals(name, "seek_events")) {
				sentence.seekEvents = readSeekEvents(reader);
			} else if (TextUtils.equals(name, "question_result")) {
				sentence.questionResult = QuestionResult.readQuestion(reader);
			}
		}
		reader.endObject();
		return sentence;
	}

	private static ArrayList<SeekEvent> readSeekEvents(JsonReader reader) throws IOException {
		ArrayList<SeekEvent> seekEventData = new ArrayList<SeekEvent>();
		reader.beginArray();
		while (reader.hasNext()) {
			seekEventData.add(SeekEvent.readSeekEvent(reader));
		}
		reader.endArray();
		return seekEventData;
	}

	private static ArrayList<CharEvent> readCharEvents(JsonReader reader) throws IOException {
		ArrayList<CharEvent> charEventData = new ArrayList<CharEvent>();
		reader.beginArray();
		while (reader.hasNext()) {
			charEventData.add(CharEvent.readCharEvent(reader));
		}
		reader.endArray();
		return charEventData;
	}

	private static ArrayList<WordEvent> readWordEvents(JsonReader reader) throws IOException {
		ArrayList<WordEvent> wordEventData = new ArrayList<WordEvent>();
		reader.beginArray();
		while (reader.hasNext()) {
			wordEventData.add(WordEvent.readWordEvent(reader));
		}
		reader.endArray();
		return wordEventData;
	}

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

	public void setQuestionResult(QuestionResult result) {
		questionResult = result;
	}

	public ArrayList<SeekEvent> getSeekEvents() {
		return seekEvents;
	}

	public int getNumSeekEvents() {
		return seekEvents.size();
	}

	public void addSeekEvent(SeekEvent e) {
		seekEvents.add(e);
	}

	public SeekEvent getLastSeekEvent() {
		return seekEvents.get(seekEvents.size() - 1);
	}

	public ArrayList<WordEvent> getWordEvents() {
		return wordEvents;
	}

	public int getNumWordEvents() {
		return wordEvents.size();
	}

	public void addWordEvent(WordEvent e) {
		wordEvents.add(e);
	}

	public WordEvent getLastWordEvent() {
		return wordEvents.get(wordEvents.size() - 1);
	}

	public ArrayList<CharEvent> getCharEvents() {
		return charEvents;
	}

	public int getNumCharEvents() {
		return charEvents.size();
	}

	public void addCharEvent(CharEvent e) {
		charEvents.add(e);
	}

	public CharEvent getLastCharEvent() {
		return charEvents.get(charEvents.size() - 1);
	}

	@Override
	public void toCSV(PrintWriter w) {
		w.print(sentenceId);
		w.print(",");
		w.print("\"" + sentence + "\"");
		w.print(",");
		w.print(condition1text);
		w.print(",");
		w.print(condition2text);
		w.print(",");
		if (questionResult != null) {
			questionResult.toCSV(w);
		} else {
			QuestionResult.printPlaceholderValues(w);
		}
	}
}