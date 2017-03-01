package cse110.grocerysaver.database;

import android.provider.BaseColumns;

import cse110.grocerysaver.GrocerySaverMain;

/**
 * Created by Philip on 2/24/17.
 *
 * Class DatabaseContract is a namespace for all the constants in the database schema such as
 * table and column names. Use the constants when interacting with the database.
 *
 * To interact with the database, see class DataProvider.
 */

public final class DatabaseContract {
    private DatabaseContract() {};

    public static class FridgeItem implements BaseColumns {
        private FridgeItem() {};

        public static final String TABLE = "fridgeItem";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_NOTES = "notes";
        public static final String COLUMN_DATE_ADDED = "dateAdded";
        public static final String COLUMN_EXPIRATION_DATE = "expirationDate";
    }

    public static class Favorite implements BaseColumns {
        private Favorite() {};

        public static final String TABLE = "favorite";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_NOTES = "notes";
        public static final String COLUMN_SHELF_LIFE = "shelfLife";
    }

    /* TODO: Populate with produce and their shelf life... */
    public static class FoodItem implements BaseColumns {
        private  FoodItem() {};

        public static final String TABLE = "foodItem";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SHELF_LIFE = "shelfLife";
    }
}
