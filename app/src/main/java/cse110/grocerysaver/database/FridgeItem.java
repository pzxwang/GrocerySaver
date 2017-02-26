package cse110.grocerysaver.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import java.util.Calendar;
import android.net.Uri;


/**
 * Created by Philip on 2/25/17.
 *
 * Use this class to perform simple CRUD operations on fridge items. For example, to insert
 * a fridge item to the database, from an activity:
 *
 *     FridgeItem item = new FridgeItem(this, "Apple", 0, 26280000, "Keeps the doctor away.");
 *     item.insert();
 *
 * Here, "this" is the activity. To query for records in the fridgeItem table see
 * FridgeItem.query().
 */

public class FridgeItem {
    private static Uri TABLE = ProviderContract.uriForTable(DatabaseContract.FridgeItem.TABLE);
    private static long NEW_RECORD_CODE = -2;
    private static String ID_SELECTION = DatabaseContract.FridgeItem._ID + " = ?";

    private static String[] COLUMNS = {
            DatabaseContract.FridgeItem._ID,
            DatabaseContract.FridgeItem.COLUMN_NAME,
            DatabaseContract.FridgeItem.COLUMN_DATE_ADDED,
            DatabaseContract.FridgeItem.COLUMN_EXPIRATION_DATE
    };

    private long id = NEW_RECORD_CODE;
    private String name;
    private long dateAdded;
    private long expirationDate;
    private String notes;

    private ContentResolver resolver;

    public static Cursor query(Context context, String selection, String[] args, String order) {
        ContentResolver resolver = context.getContentResolver();
        return resolver.query(TABLE, COLUMNS, selection, args, order);
    }

    public static FridgeItem findByID(Context context, long id) {
        Cursor cursor = query(context, ID_SELECTION, new String[]{ String.valueOf(id) }, null);
        if (cursor.getCount() == 0) {
            return null;
        }

        cursor.moveToFirst();
        FridgeItem fridgeItem = new FridgeItem(context, cursor);
        cursor.close();

        return fridgeItem;
    }

    public FridgeItem(Context context, Cursor cursor) {
        this.resolver = context.getContentResolver();

        for (String column : cursor.getColumnNames()) {
            switch (column) {
                case DatabaseContract.FridgeItem._ID:
                    id = cursor.getLong(cursor.getColumnIndex(DatabaseContract.FridgeItem._ID));
                    break;
                case DatabaseContract.FridgeItem.COLUMN_NAME:
                    name = cursor.getString(cursor.getColumnIndex(DatabaseContract.FridgeItem.COLUMN_NAME));
                    break;
                case DatabaseContract.FridgeItem.COLUMN_DATE_ADDED:
                    dateAdded = cursor.getLong(cursor.getColumnIndex(DatabaseContract.FridgeItem.COLUMN_DATE_ADDED));
                    break;
                case DatabaseContract.FridgeItem.COLUMN_EXPIRATION_DATE:
                    expirationDate = cursor.getLong(cursor.getColumnIndex(DatabaseContract.FridgeItem.COLUMN_EXPIRATION_DATE));
                    break;
                case DatabaseContract.FridgeItem.COLUMN_NOTES:
                    notes = cursor.getString(cursor.getColumnIndex(DatabaseContract.FridgeItem.COLUMN_NOTES));
                    break;
            }
        }
    }

    public FridgeItem(Context context) {
        this.resolver = context.getContentResolver();
    }

    public FridgeItem(String name, Calendar dateAdded, Calendar expirationDate, String notes) {
        long ats = dateAdded.getTimeInMillis();
        long ets = expirationDate.getTimeInMillis();

        this.name = name;
        this.dateAdded = ats;
        this.expirationDate = ets;
        this.notes = notes;
    }

    private ContentValues putContentValues() {
        ContentValues values = new ContentValues();

        values.put(DatabaseContract.FridgeItem.COLUMN_NAME, name);
        values.put(DatabaseContract.FridgeItem.COLUMN_DATE_ADDED, dateAdded);
        values.put(DatabaseContract.FridgeItem.COLUMN_EXPIRATION_DATE, expirationDate);
        values.put(DatabaseContract.FridgeItem.COLUMN_NOTES, notes);

        return values;
    }

    private String id() {
        return String.valueOf(id);
    }

    public boolean insert() {
        if (id != NEW_RECORD_CODE) {
            return false;
        }

        id = Long.valueOf(resolver.insert(TABLE, putContentValues()).getLastPathSegment());

        return id != -1;
    }

    public boolean update() {
        if (id == NEW_RECORD_CODE) {
            insert();
        }

        String[] args = new String[] { id() };
        int count = resolver.update(TABLE, putContentValues(), ID_SELECTION, args);

        return count == 1;
    }
    
    public boolean delete() {
        if (id == NEW_RECORD_CODE) {
            return false;
        }

        String[] args = new String[] { id() };
        int count = resolver.delete(TABLE, ID_SELECTION, args);

        return count == 1;
    }

    public long getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Calendar getDateAdded() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(dateAdded);
        return c;
    }

    public Calendar getExpirationDate() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(expirationDate);
        return c;
    }

    public String notes() {
        if (notes == null) {
            String[] columns = new String[] { DatabaseContract.FridgeItem.COLUMN_NOTES };
            String[] args = new String[] { id() };
            Cursor cursor = resolver.query(TABLE, columns, ID_SELECTION, args, null);

            notes = cursor.getString(cursor.getColumnIndex(DatabaseContract.FridgeItem.COLUMN_NOTES));
            cursor.close();;
        }

        return notes;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDateAdded(Calendar dateAdded) {
        this.dateAdded = dateAdded.getTimeInMillis();
    }

    public void setExpirationDate(Calendar expirationDate) {
        this.expirationDate = expirationDate.getTimeInMillis();
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
