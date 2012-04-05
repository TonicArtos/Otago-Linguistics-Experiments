package nz.ac.otago.linguistics.spre;

public class ProfileResult implements ExperimentActivity.Result {
	protected boolean englishFirst;
	protected boolean rightHanded;

	// protected int age;
	// protected boolean gender;
	@Override
	public String toString() {
		String s = "";
		s += "English First Language, " + (englishFirst ? "yes" : "no") + "\n";
		s += "Right Handed, " + (rightHanded ? "yes" : "no") + "\n";
		// s += "Age, " + age + "\n";
		// s += "Gender, " + (gender? "female": "male") + "\n";
		return s;
	}
}