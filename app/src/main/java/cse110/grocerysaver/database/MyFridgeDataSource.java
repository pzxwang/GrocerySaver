package cse110.grocerysaver.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class MyFridgeDataSource {

    private SQLiteDatabase mDatabase;
    private MyFridgeHelper mMyFridgeHelper;
    private Context mContext;

    public MyFridgeDataSource(Context context) {
        mContext = context;
        mMyFridgeHelper = new MyFridgeHelper(mContext);
    }

    public void open() throws SQLException {
        mDatabase = mMyFridgeHelper.getWritableDatabase();
    }

    public void close() {
        mDatabase.close();
    }

    public void insertRow(String name, String notes, long addDate, long expDate, String itemId) {
        ContentValues values = new ContentValues();
        
        values.put(MyFridgeHelper.COLUMN_NAME, name);
        values.put(MyFridgeHelper.COLUMN_NOTES, notes);
        values.put(MyFridgeHelper.COLUMN_ADD, addDate);
        values.put(MyFridgeHelper.COLUMN_EXPDATE, expDate);
        values.put(MyFridgeHelper.COLUMN_ID, itemId);

        mDatabase.insert(MyFridgeHelper.TABLE_MYFRIDGE, null, values);
    }

    public Cursor selectAllAmount() {
        Cursor cursor = mDatabase.query(
                MyFridgeHelper.TABLE_MYFRIDGE,
                new String[] {MyFridgeHelper.COLUMN_ID, MyFridgeHelper.COLUMN_NAME,
                        MyFridgeHelper.COLUMN_NOTES, MyFridgeHelper.COLUMN_ADD,
                        MyFridgeHelper.COLUMN_EXPDATE},
                null,
                null,
                null,
                null,
                null
        );
        return cursor;
    }

    public void deleteItem(String itemId) {
        String whereClause = "_ID=?";
        String[] whereArgs = new String[] {itemId};
        int del = mDatabase.delete(MyFridgeHelper.TABLE_MYFRIDGE, whereClause, whereArgs);
    }
    public void deleteAll() {
        mDatabase.delete(
                MyFridgeHelper.TABLE_MYFRIDGE,
                null, // where clause
                null // where params
        );
    }


}

