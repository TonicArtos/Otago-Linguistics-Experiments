package nz.ac.otago.linguistics.obsproj1;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * A result for an answered question.
 * 
 * @author Tonic Artos
 */
public class QuestionResult implements ExperimentActivity.Result {
	private static final long serialVersionUID = 4244677802950067450L;
	public static final int ANSWERED_NO = 0;
	public static final int ANSWERED_YES = 1;

	/**
	 * @serial
	 */
	protected boolean correctAnswer;
	/**
	 * @serial
	 */
	protected int questionId;
	/**
	 * @serial
	 */
	protected int responseTime;
	/**
	 * @serial
	 */
	protected int answer;
	/**
	 * @serial
	 */
	protected String question;
	@Override
	public void write(BufferedWriter out) throws IOException {
		out.write("QuestionID, " + questionId + "\n");
		out.write("Response, " + responseTime + ", " + (correctAnswer? "correct": "incorrect") + "\n");
	}
}