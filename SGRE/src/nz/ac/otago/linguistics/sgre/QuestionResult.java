package nz.ac.otago.linguistics.sgre;

import java.io.IOException;

import android.util.JsonWriter;

/**
 * A result for an answered question.
 * 
 * @author Tonic Artos
 */
public class QuestionResult implements ExperimentActivity.Result {
	public static final int ANSWERED_NO = 0;
	public static final int ANSWERED_YES = 1;

	protected boolean correctAnswer;
	protected int questionId;
	protected long responseTime;
	protected int response;
	protected String question;

	@Override
	public String toString() {
		String s = "";
		s += "Question ID, " + (questionId + 1) + "\n";
		s += "Response, " + responseTime + ", " + (correctAnswer ? "correct" : "incorrect") + ", " + (response == ANSWERED_YES ? "yes" : "no") + "\n";
		return s;
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
}