package nz.ac.otago.linguistics.sgre;

import java.io.IOException;
import java.io.PrintWriter;

import android.util.JsonWriter;

public class ProfileResult implements JSONData, CSVData {
	public boolean englishFirst;
	public boolean rightHanded;
	public int age;
	public long sessionId;
	public String gender;
	public String dataSet;
	public String rightHandedS;
	public String englishFirstLanguage;

	static public void printHeaders(PrintWriter w) {
		w.print("session_id");
		w.print(",");
		w.print("data_set");
		w.print(",");
		w.print("english_first_language");
		w.print(",");
		w.print("right_handed");
		w.print(",");
		w.print("age");
		w.print(",");
		w.print("gender");
	}

	@Override
	public void toJSON(JsonWriter out) throws IOException {
		out.name("session_id").value(sessionId);
		out.name("data_set").value(dataSet);
		out.name("english_first_language").value((englishFirst ? "yes" : "no"));
		out.name("right_handed").value((rightHanded ? "yes" : "no"));
		out.name("age").value(age);
		out.name("gender").value(gender);
	}

	@Override
	public void toCSV(PrintWriter w) {
		w.print(sessionId);
		w.print(",");
		w.print(dataSet);
		w.print(",");
		w.print(englishFirstLanguage);
		w.print(",");
		w.print(rightHandedS);
		w.print(",");
		w.print(age);
		w.print(",");
		w.print(gender);
	}
}