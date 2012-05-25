package nz.ac.otago.linguistics.sgre;

public class ExperimentData extends DbModel {
	@SuppressWarnings("unused")
	private static final String TAG = "ac.nz.otago.liguistics.sgre.ExperimentData";

	public static final String TABLE = "ExperimentData";

	public static final String KEY_DATA_LOCATION = "data_location";

	public static final String TABLE_CREATE = "create table "
			+ TABLE
			+ " (_id integer primary key autoincrement, "
			+ "data_location text not null"
			+ ");";
}
