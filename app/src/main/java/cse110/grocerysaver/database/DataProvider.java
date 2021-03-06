package cse110.grocerysaver.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 *
 * This class is used for interacting with the database. Do not directly create an instance of this
 * class. It is registered as a content provider and Android automatically manages it for us.
 *
 * To indirectly use this class, from an activity use getContentResolver().
 *
 * See class DatabaseContract for all the database schema constant definitions. For simple CRUD
 * operations, see PersistableManager.
 */

public class DataProvider extends ContentProvider {
    static private final int FRIDGE_ITEM = 1;
    static private final int FAVORITE = 2;
    static private final int INVENTORY_ITEM = 3;

    static private final int FRIDGE_ITEM_ID = 4;
    static private final int FAVORITE_ID = 5;
    static private final int FOOD_ITEM_ID = 6;
    static private final int INVENTORY_ITEM_ID = 7;

    static private final String TYPE_ITEM = "vnd.android.cursor.item/";
    static private final String TYPE_DIR = "vnd.android.cursor.dir/";

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(ProviderContract.AUTHORITY, DatabaseContract.FridgeItem.TABLE, FRIDGE_ITEM);
        uriMatcher.addURI(ProviderContract.AUTHORITY, DatabaseContract.Favorite.TABLE, FAVORITE);
        uriMatcher.addURI(ProviderContract.AUTHORITY, DatabaseContract.InventoryItem.TABLE, INVENTORY_ITEM);
        uriMatcher.addURI(ProviderContract.AUTHORITY, DatabaseContract.FridgeItem.TABLE + "/#", FRIDGE_ITEM_ID);
        uriMatcher.addURI(ProviderContract.AUTHORITY, DatabaseContract.Favorite.TABLE + "/#", FAVORITE_ID);
        uriMatcher.addURI(ProviderContract.AUTHORITY, DatabaseContract.InventoryItem.TABLE + "/#", INVENTORY_ITEM_ID);
    }

    private DatabaseHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Nullable
    private String matchTable(Uri uri) {
        String table = null;
        switch (uriMatcher.match(uri)) {
            case FRIDGE_ITEM:
                table = DatabaseContract.FridgeItem.TABLE;
                break;
            case FAVORITE:
                table = DatabaseContract.Favorite.TABLE;
                break;
            case INVENTORY_ITEM:
                table = DatabaseContract.InventoryItem.TABLE;
                break;
        }
        return table;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] columns, String selection, String[] args, String order) {
        String table = matchTable(uri);

        if (table == null) {
            return null;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(table, columns, selection, args, null, null, order);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case FRIDGE_ITEM:
                return TYPE_DIR + ProviderContract.AUTHORITY + "." + DatabaseContract.FridgeItem.TABLE;
            case FAVORITE:
                return TYPE_DIR + ProviderContract.AUTHORITY + "." + DatabaseContract.Favorite.TABLE;
            case INVENTORY_ITEM:
                return TYPE_DIR + ProviderContract.AUTHORITY + "." + DatabaseContract.InventoryItem.TABLE;

            case FRIDGE_ITEM_ID:
                return TYPE_ITEM + ProviderContract.AUTHORITY + "." + DatabaseContract.FridgeItem.TABLE;
            case FAVORITE_ID:
                return TYPE_ITEM + ProviderContract.AUTHORITY + "." + DatabaseContract.Favorite.TABLE;
            case INVENTORY_ITEM_ID:
                return TYPE_ITEM + ProviderContract.AUTHORITY + "." + DatabaseContract.InventoryItem.TABLE;
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String table = matchTable(uri);
        long id = db.insert(table, null, values);

        uri = ContentUris.withAppendedId(uri, id);
        if (id > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] args) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String table = matchTable(uri);
        int rowsDeleted = db.delete(table, selection, args);

        getContext().getContentResolver().notifyChange(uri, null);

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] args) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String table = matchTable(uri);
        int rowsAffected = db.update(table, values, selection, args);

        getContext().getContentResolver().notifyChange(uri, null);

        return rowsAffected;
    }
}
