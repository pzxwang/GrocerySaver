package cse110.grocerysaver.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.Provider;
import java.util.ArrayList;
import java.util.HashMap;

import static android.provider.BaseColumns._ID;

/**
 *
 * PersistableManager is a thin wrapper for DataProvider. Use it for simple CRUD operations.
 * The following must be satisfied by a class that wants to represent a record from a table
 * in the database in order for PersistableManager to manage it:
 *
 * 1.   It must extend abstract class Persistable.
 * 2.   There must be a one-to-one mapping between a table's columns and instance variables.
 * 3.   The instance variables must be boxed.
 * 4.   The class must have the same name as the table class in DatabaseContract.
 *
 * An example is the class FridgeItem. It extends Persistable. It has the same class name as the
 * table class in DatabaseContract (i.e. FridgeItem and DatabaseContract.FridgeItem). It's boxed
 * instance variables maps to the DatabaseContract.FridgeItem columns.
 */

public class PersistableManager {

    /* Use for queries. */
    public final static String ASC = "ASC";
    public final static String DESC = "DESC";

    /* Context is needed to interact with the content provider. */
    private Context context = null;

    /* Maps Persistable classes to an Entity. */
    private final static HashMap<Class, Entity> persistableMap = new HashMap<>();

    /* Class Entity is used internally to map table columns (defined by class DatabaseContract)
     * to Persistable fields, Cursor methods, and ContentValues methods. */
    private static class Entity {
        private String tableName = null;

        private HashMap<String, Field> columnFieldMap = new HashMap<>();
        private HashMap<String, Method> columnGetMethodMap = new HashMap<>();
        private HashMap<String, Method> columnPutMethodMap = new HashMap<>();

        public Entity() {};
    }

    /* entity() maps Persistable classes to an Entity. It creates a new Entity if it doesn't map
     * to one and caches the entity for future requests. */
    private static Entity entity(Class persistable) {
        String name = persistable.getSimpleName();
        if (persistableMap.containsKey(name)) {
            return persistableMap.get(name);
        }

        Entity entity = new Entity();

        /* Find the table class corresponding to the persistable class in the database contract. */
        Class tableContract = null;
        for (Class<?> t : DatabaseContract.class.getDeclaredClasses()) {
            if (name.equals(t.getSimpleName())) {
                tableContract = t;
            }
        }

        try {
            entity.tableName = (String) tableContract.getDeclaredField("TABLE").get(null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        /* Get the column names from the table class by looking at the values of its static fields
         * that begins with "COLUMN_".
         */
        for (Field column : tableContract.getDeclaredFields()) {
            boolean isStatic = Modifier.isStatic(column.getModifiers());
            boolean isPrefixColumn = column.getName().startsWith("COLUMN_");
            if (isStatic && isPrefixColumn) {
                try {
                    String c = (String) column.get(null);
                    Field f = persistable.getDeclaredField(c);
                    Method m = null;
                    Method n = null;

                    entity.columnFieldMap.put(c, f);

                    switch (f.getType().getSimpleName()) {
                        case "Short":
                            m = Cursor.class.getMethod("getShort", int.class);
                            n = ContentValues.class.getMethod("put", String.class, Short.class);
                            break;
                        case "Integer":
                            m = Cursor.class.getMethod("getInt", int.class);
                            n = ContentValues.class.getMethod("put", String.class, Integer.class);
                            break;
                        case "Long":
                            m = Cursor.class.getMethod("getLong", int.class);
                            n = ContentValues.class.getMethod("put", String.class, Long.class);
                            break;
                        case "Float":
                            m = Cursor.class.getMethod("getFloat", int.class);
                            n = ContentValues.class.getMethod("put", String.class, Float.class);
                            break;
                        case "String":
                            m = Cursor.class.getMethod("getString", int.class);
                            n = ContentValues.class.getMethod("put", String.class, String.class);
                            break;
                        case "byte[]":
                            m = Cursor.class.getMethod("getBlob", int.class);
                            n = ContentValues.class.getMethod("put", String.class, byte[].class);
                            break;
                    }

                    entity.columnGetMethodMap.put(c, m);
                    entity.columnPutMethodMap.put(c, n);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }

        /* Since the table classes subclass BaseColumns, we have to manually add the ID columns. */
        try {
            entity.columnFieldMap.put(_ID, persistable.getSuperclass().getDeclaredField(_ID));
            entity.columnGetMethodMap.put(_ID, Cursor.class.getMethod("getLong", int.class));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        persistableMap.put(persistable, entity);

        return entity;
    }

    /* The default constructor must be a given context to perform CRUD operations. */
    public PersistableManager(Context context) {
        this.context = context;
    }

    /* setPersistableID() sets the ID field of an instance of a Persistable. */
    private void setPersistableID(Persistable p, long id) {
        Entity entity = entity(p.getClass());
        Field f = entity.columnFieldMap.get(_ID);

        f.setAccessible(true);
        try {
            f.set(p, id);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        f.setAccessible(false);
    }

    /* initializedPersistable() returns an initialized instance of a Persistable with the values of
     * the given cursor. */
    public Persistable initializedPersistable(Class persistable, Cursor cursor) {
        Entity entity = entity(persistable);

        Persistable p = null;
        try {
            p = (Persistable) persistable.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        for (String column : cursor.getColumnNames()) {
            int index = cursor.getColumnIndex(column);
            Field f = entity.columnFieldMap.get(column);
            Method m = entity.columnGetMethodMap.get(column);

            try {
                Object r = m.invoke(cursor, index);
                f.setAccessible(true);
                f.set(p, r);
                f.setAccessible(false);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return p;
    }

    public Cursor cursor(Class persistable, String[] col, String sel, String[] args, String order) {
        Entity entity = entity(persistable);
        Uri uri = ProviderContract.uriForTable(entity.tableName);

        return context.getContentResolver().query(uri, col, sel, args, order);
    }

    public ArrayList<Persistable> query(Class persistable, String[] col, String sel, String[] args, String order) {
        ArrayList<Persistable> result = new ArrayList<>();
        Cursor cursor = cursor(persistable, col, sel, args, order);

        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++)
        {
            Persistable p = initializedPersistable(persistable, cursor);
            result.add(p);
            cursor.moveToNext();
        }
        cursor.close();
        return result;
    }

    public int delete(Class persistable, String sel, String[] args) {
        Entity entity = entity(persistable);
        Uri uri = ProviderContract.uriForTable(entity.tableName);

        return context.getContentResolver().delete(uri, sel, args);
    }

    public Persistable findByID(Class persistable, long id) {
        Entity entity = entity(persistable);

        int columnsSize = entity.columnGetMethodMap.keySet().size();
        String[] projection = entity.columnFieldMap.keySet().toArray(new String[columnsSize]);
        ArrayList<Persistable> results = query(persistable, projection, _ID + " = ?", new String[] { String.valueOf(id) }, null);

        if (results.size() == 1) {
            return results.get(0);
        }

        return null;
    }

    // query for options to populate autocomplete
    public ArrayList<InventoryItem> populateInventory(Class persistable) {

        ArrayList<Persistable> results = null;
        ArrayList<InventoryItem> list = new ArrayList<InventoryItem>();
        results = query(persistable, new String[] {DatabaseContract.InventoryItem.COLUMN_NAME,
                        DatabaseContract.InventoryItem.COLUMN_SHELF_LIFE}, null, null, null);

        for (Persistable result : results) {
            list.add((InventoryItem) result);
        }
        return list;

    }

    // method to check if a food item is already in inventory table before adding
    public boolean isFoodItemInInventoryDb(Class persistable, String foodName) {
        Entity entity = entity(persistable);

        int columnsSize = entity.columnGetMethodMap.keySet().size();
        String[] projection = entity.columnFieldMap.keySet().toArray(new String[columnsSize]);
        ArrayList<Persistable> results = null;
        results = query(persistable, projection, "name = ?", new String[] {foodName}, null);

        if (results.size() > 0) {
            return true;
        }
        return false;
    }

    public int save(Persistable... plist) {
        int count = 0;

        for (Persistable p : plist) {
            Entity entity = entity(p.getClass());
            Uri uri = ProviderContract.uriForTable(entity.tableName);

            ContentValues values = new ContentValues();
            for (String column : entity.columnPutMethodMap.keySet()) {
                try {
                    Field f = entity.columnFieldMap.get(column);
                    f.setAccessible(true);
                    entity.columnPutMethodMap.get(column).invoke(values, column, f.get(p));
                    f.setAccessible(false);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

            if (p.getID() == Persistable.NEW_RECORD) {
                Uri u = context.getContentResolver().insert(uri, values);
                Long l = Long.valueOf(u.getLastPathSegment());

                if (l != -1) count++;
                setPersistableID(p, Long.valueOf(u.getLastPathSegment()));
            } else {
                count += context.getContentResolver()
                        .update(uri, values, _ID + " = ?", new String[] { String.valueOf(p.getID()) });
            }
        }

        return count;
    }

    public int delete(Persistable... plist) {
        int count = 0;

        for (Persistable p : plist) {
            Entity entity = entity(p.getClass());
            Uri uri = ProviderContract.uriForTable(entity.tableName);

            if (p.getID() == Persistable.NEW_RECORD) {
                continue;
            } else {
                count += context.getContentResolver()
                        .delete(uri, _ID + " = ?", new String[] { String.valueOf(p.getID()) });
                setPersistableID(p, Persistable.NEW_RECORD);
            }
        }

        return count;
    }


}
