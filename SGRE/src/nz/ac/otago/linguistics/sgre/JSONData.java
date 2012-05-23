package nz.ac.otago.linguistics.sgre;

import java.io.IOException;

import android.util.JsonWriter;

public interface JSONData {
	public void toJSON(JsonWriter out) throws IOException;
}
