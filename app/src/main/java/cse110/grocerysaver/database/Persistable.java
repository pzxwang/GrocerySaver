package cse110.grocerysaver.database;

import android.content.ContentValues;

/**
 * Created by Philip on 2/27/17.
 *
 * Persistable must be subclassed by a class that wants to represent a record from a table in
 * DatabaseContract to be able to be managed by PersistableManager.
 */

public abstract class Persistable {
    public final static long NEW_RECORD = -2;

    private Long _id = NEW_RECORD;

    public final Long getID() {
        return _id;
    }
}
