package nz.ac.otago.linguistics.sgre;

import java.io.IOException;
import java.io.PrintWriter;

import android.text.TextUtils;
import android.util.JsonReader;
import android.util.JsonWriter;

/**
 * A result for an answered question.
 * 
 * @author Tonic Artos
 */
public class QuestionResult implements JSONData, CSVData {
	public static final int ANSWERED_NO = 0;
	public static final int ANSWERED_YES = 1;

	protected boolean correctAnswer;
	protected int questionId;
	protected long responseTime;
	protected int response;
	protected String question;
	private String userAnswer = "n/a";
	private String wasCorrect = "n/a";


	public static void printHeaders(PrintWriter w) {
		w.print("q_id");
		w.print(",");
		w.print("q_response_time");
		w.print(",");
		w.print("q_user_answer");
		w.print(",");
		w.print("q_was_correct");
	}

	public static void printPlaceholderValues(PrintWriter w) {
		w.print("0");
		w.print(",");
		w.print("0");
		w.print(",");
		w.print("n/a");
		w.print(",");
		w.print("n/a");
		w.print(",");
	}
	
	public static QuestionResult readQuestion(JsonReader reader) throws IOException {
		QuestionResult qr = new QuestionResult();
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (TextUtils.equals(name, "question_id")) {
				qr.questionId = reader.nextInt();
			} else if (TextUtils.equals(name, "response_time")) {
				qr.responseTime = reader.nextLong();
			}else if (TextUtils.equals(name, "user_answer")) {
				qr.userAnswer = reader.nextString();
			}else if (TextUtils.equals(name, "was_correct")) {
				qr.wasCorrect = reader.nextString();
			}
		}
		reader.endObject();
		return qr;
	}
	
	@Override
	public void toJSON(JsonWriter out) throws IOException {
		out.beginObject();
		out.name("question_id").value(questionId + 1);
		out.name("response_time").value(responseTime);
		out.name("user_answer").value((response == ANSWERED_YES ? "yes" : "no"));
		out.name("was_correct").value((correctAnswer ? "correct" : "incorrect"));
		out.endObject();
	}

	@Override
	public void toCSV(PrintWriter w) {
		w.print(questionId);
		w.print(",");
		w.print(responseTime);
		w.print(",");
		w.print(userAnswer);
		w.print(",");
		w.print(wasCorrect);
	}
}