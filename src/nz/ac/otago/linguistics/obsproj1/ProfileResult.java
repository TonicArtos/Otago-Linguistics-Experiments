package nz.ac.otago.linguistics.obsproj1;

public class ProfileResult implements ExperimentActivity.Result {
	protected boolean englishFirst;
	protected boolean rightHanded;

	// protected int age;
	// protected boolean gender;
	@Override
	public String toString() {
		String s = "";
		s += "English-L1, " + (englishFirst ? "yes" : "no") + "\n";
		s += "OTHER2, " + (rightHanded ? "yes" : "no") + "\n";
		// s += "Age, " + age + "\n";
		// s += "Gender, " + (gender? "female": "male") + "\n";
		return s;
	}
}