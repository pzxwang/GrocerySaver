package cse110.grocerysaver.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.Calendar;

/**
 * Created by Philip on 2/25/17.
 */

public class Favorite {
    private static Uri TABLE = ProviderContract.uriForTable(DatabaseContract.Favorite.TABLE);
    private static long NEW_RECORD_CODE = -2;
    private static String ID_SELECTION = DatabaseContract.FridgeItem._ID + " = ?";

    private static String[] COLUMNS = {
            DatabaseContract.Favorite._ID,
            DatabaseContract.Favorite.COLUMN_NAME,
            DatabaseContract.Favorite.COLUMN_SHELF_LIFE
    };

    private long id = NEW_RECORD_CODE;
    private String name;
    private long shelfLife;
    private String notes;

    private ContentResolver resolver;

    public enum TimeUnit {
        DAY, WEEK, MONTH
    }

    public static Cursor query(Context context, String selection, String[] args, String order) {
        ContentResolver resolver = context.getContentResolver();
        return resolver.query(TABLE, COLUMNS, selection, args, order);
    }

    public static Favorite findByID(Context context, long id) {
        Cursor cursor = query(context, ID_SELECTION, new String[]{ String.valueOf(id) }, null);
        if (cursor.getCount() == 0) {
            return null;
        }

        cursor.moveToFirst();
        Favorite favorite = new Favorite(context, cursor);
        cursor.close();

        return favorite;
    }

    public Favorite(Context context, Cursor cursor) {
        this.resolver = context.getContentResolver();

        for (String column : cursor.getColumnNames()) {
            switch (column) {
                case DatabaseContract.Favorite._ID:
                    id = cursor.getLong(cursor.getColumnIndex(DatabaseContract.Favorite._ID));
                    break;
                case DatabaseContract.Favorite.COLUMN_NAME:
                    name = cursor.getString(cursor.getColumnIndex(DatabaseContract.Favorite.COLUMN_NAME));
                    break;
                case DatabaseContract.Favorite.COLUMN_SHELF_LIFE:
                    shelfLife = cursor.getLong(cursor.getColumnIndex(DatabaseContract.Favorite.COLUMN_SHELF_LIFE));
                    break;
            }
        }
    }

    public Favorite(String name, long shelfLife, String notes) {
        this.name = name;
        this.shelfLife = shelfLife;
        this.notes = notes;
    }

    public Favorite(Context context) {
        this.resolver = context.getContentResolver();
    }

    private ContentValues putContentValues() {
        ContentValues values = new ContentValues();

        values.put(DatabaseContract.Favorite.COLUMN_NAME, name);
        values.put(DatabaseContract.Favorite.COLUMN_SHELF_LIFE, shelfLife);
        values.put(DatabaseContract.Favorite.COLUMN_NOTES, notes);

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

    public int getShelfLife(TimeUnit unit) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(shelfLife);

        switch(unit) {
            case DAY:
                return c.get(Calendar.DAY_OF_MONTH) - 1;
            case MONTH:
                return c.get(Calendar.MONTH);
            case WEEK:
                return (c.get(Calendar.DAY_OF_MONTH) - 1) / 7;
        }

        return -1;
    }

    public String getNotes() {
        if (notes == null) {
            String[] columns = new String[] { DatabaseContract.Favorite.COLUMN_NOTES };
            String[] args = new String[] { id() };
            Cursor cursor = resolver.query(TABLE, columns, ID_SELECTION, args, null);

            notes = cursor.getString(cursor.getColumnIndex(DatabaseContract.Favorite.COLUMN_NOTES));
            cursor.close();;
        }

        return notes;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setShelfLife(int interval, TimeUnit unit) {
        Calendar c = Calendar.getInstance();
        switch (unit) {
            case DAY:
                c.set(Calendar.HOUR, interval);
                break;
            case WEEK:
                c.set(Calendar.WEEK_OF_YEAR, interval);
                break;
            case MONTH:
                c.set(Calendar.MONTH, interval);
                break;
        }
        shelfLife = c.getTimeInMillis();
    }

    public void setShelfLife(long shelfLife) {
        this.shelfLife = shelfLife;
    }
}
