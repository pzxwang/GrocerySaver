package cse110.grocerysaver.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cse110.grocerysaver.database.DatabaseContract.FridgeItem;
import cse110.grocerysaver.database.DatabaseContract.FoodItem;
import cse110.grocerysaver.database.DatabaseContract.Favorite;

/**
 * Created by Philip on 2/24/17.
 *
 * This class is for handling database creation and upgrades. It can also be used to interact with
 * the database, but do not use this. This is a lower-level class, see class DataProvider.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "GrocerySaver.db";
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_TABLE_FRIDGE_ITEM =
            "CREATE TABLE " + FridgeItem.TABLE + " (" +
                    FridgeItem._ID + " INTEGER PRIMARY KEY," +
                    FridgeItem.COLUMN_NAME + " TEXT," +
                    FridgeItem.COLUMN_DATE_ADDED + " INTEGER," +
                    FridgeItem.COLUMN_SHELF_LIFE + " INTEGER," +
                    FridgeItem.COLUMN_NOTES + " TEXT)";

    private static final String SQL_CREATE_TABLE_FOOD_ITEM =
            "CREATE TABLE " + FoodItem.TABLE + " (" +
                    FoodItem._ID + " INTEGER PRIMARY KEY," +
                    FoodItem.COLUMN_NAME + " TEXT," +
                    FoodItem.COLUMN_SHELF_LIFE + " INTEGER)";

    private static final String SQL_CREATE_TABLE_FAVORITE =
            "CREATE TABLE " + Favorite.TABLE + " (" +
                    Favorite._ID + " INTEGER PRIMARY KEY," +
                    Favorite.COLUMN_NAME + " TEXT," +
                    Favorite.COLUMN_SHELF_LIFE + " INTEGER," +
                    Favorite.COLUMN_NOTES + " TEXT)";

    private static final String SQL_DROP_TABLE_FRIDGE_ITEM =
            "DROP TABLE IF EXISTS " + FridgeItem.TABLE;

    private static final String SQL_DROP_TABLE_FOOD_ITEM =
            "DROP TABLE IF EXISTS " + FoodItem.TABLE;

    private static final String SQL_DROP_TABLE_FAVORITE =
            "DROP TABLE IF EXISTS " + Favorite.TABLE;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_FRIDGE_ITEM);
        db.execSQL(SQL_CREATE_TABLE_FAVORITE);
        db.execSQL(SQL_CREATE_TABLE_FOOD_ITEM);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(SQL_DROP_TABLE_FRIDGE_ITEM);
        db.execSQL(SQL_DROP_TABLE_FOOD_ITEM);
        db.execSQL(SQL_DROP_TABLE_FAVORITE);
        onCreate(db);
    }
}
