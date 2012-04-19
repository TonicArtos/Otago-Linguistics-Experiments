package nz.ac.otago.linguistics.spre;

public class ExperimentData extends DbModel {
	@SuppressWarnings("unused")
	private static final String TAG = "ac.nz.otago.liguistics.obsproj2.ExperimentData";

	public static final String TABLE = "ExperimentData";

	public static final String KEY_DATA = "data";
	public static final String KEY_DATA_SET = "data_set";

	public static final String TABLE_CREATE = "create table "
			+ TABLE
			+ " (_id integer primary key autoincrement, "
			+ "data_set text not null, "
			+ "data text not null"
			+ ");";
}
