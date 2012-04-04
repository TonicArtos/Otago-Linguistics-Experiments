package nz.ac.otago.linguistics.obsproj1;

public class ExperimentData extends DbModel {
    @SuppressWarnings("unused")
    private static final String TAG = "ac.nz.otago.liguistics.obsproj2.ExperimentData";

    public static final String TABLE = "ExperimentData";
    
    public static final String KEY_DATA = "data";

    public static final String TABLE_CREATE = "create table " + TABLE +
                                              " (_id integer primary key autoincrement, " +
                                              "data text not null" +
                                              ");";
}
