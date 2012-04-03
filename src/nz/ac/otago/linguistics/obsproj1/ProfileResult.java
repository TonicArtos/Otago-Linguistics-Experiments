package nz.ac.otago.linguistics.obsproj1;

import java.io.BufferedWriter;
import java.io.IOException;

public class ProfileResult implements ExperimentActivity.Result {
	/**
	 * @serial
	 */
	private static final long serialVersionUID = -2020818602505052382L;
	/**
	 * @serial
	 */
	protected boolean englishFirst;
	/**
	 * @serial
	 */
	protected boolean rightHanded;
//	/**
//	 * @serial
//	 */
//	protected int age;
//	/**
//	 * @serial
//	 */
//	protected boolean gender;
	@Override
	public void write(BufferedWriter out) throws IOException {
		out.write("English-L1, " + (englishFirst? "yes": "no") + "\n");
		out.write("OTHER2, " + (rightHanded? "yes": "no") + "\n");
//		out.write("Age, " + age + "\n");
//		out.write("Gender, " + (gender? "female": "male") + "\n");
	}
}