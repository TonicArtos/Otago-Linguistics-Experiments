package nz.ac.otago.linguistics.sgre;

import java.io.IOException;

import android.util.JsonWriter;

public class ProfileResult implements JSONData {
	public boolean englishFirst;
	public boolean rightHanded;
	public int age;
	public long sessionId;
	public String gender;
	public String dataSet;

	@Override
	public void toJSON(JsonWriter out) throws IOException {
		out.name("session_id").value(sessionId);
		out.name("data_set").value(dataSet);
		out.name("english_first_language").value((englishFirst ? "yes" : "no"));
		out.name("right_handed").value((rightHanded ? "yes" : "no"));
		out.name("age").value(age);
		out.name("gender").value(gender);
	}
}