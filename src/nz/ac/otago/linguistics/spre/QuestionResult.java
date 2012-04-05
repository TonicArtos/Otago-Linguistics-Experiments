package nz.ac.otago.linguistics.spre;

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
	protected int answer;
	protected String question;

	@Override
	public String toString() {
		String s = "";
		s += "Question ID, " + questionId + "\n";
		s += "Response, " + responseTime + ", " + (correctAnswer ? "correct" : "incorrect") + ", " + (answer == ANSWERED_YES ? "yes" : "no") + "\n";
		return s;
	}
}