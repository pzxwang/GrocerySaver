package cse110.grocerysaver.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyFridgeHelper extends SQLiteOpenHelper {

    // Name the database and its columns
    public static final String TABLE_MYFRIDGE = "MyFridge";
    public static final String COLUMN_ID = "_ID";
    public static final String COLUMN_NAME = "NAME";
    public static final String COLUMN_NOTES = "NOTES";
    public static final String COLUMN_ADD = "ADDED_DATE";
    public static final String COLUMN_EXPDATE = "EXP_DATE";

    private static final String DB_NAME = "myfridge.db";
    private static final int DB_VERSION = 1;
/*    private static final String DB_CREATE =
            "CREATE TABLE " + TABLE_MYFRIDGE + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME+ " TEXT, " +
                    COLUMN_NOTES + " TEXT, " +
                    COLUMN_ADD + " REAL, " +
                    COLUMN_EXPDATE + " REAL);";*/
    private static final String DB_CREATE =
            "CREATE TABLE " + TABLE_MYFRIDGE + " (" +
                    COLUMN_ID + " TEXT, " +
                    COLUMN_NAME+ " REAL, " +
                    COLUMN_NOTES + " TEXT, " +
                    COLUMN_ADD + " REAL, " +
                    COLUMN_EXPDATE + " REAL);";

    public MyFridgeHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // Creates MyFridge Database
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
