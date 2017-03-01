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

public class Favorite extends Persistable {
    private String name;
    private Long shelfLife;
    private String notes;

    public enum TimeUnit {
        DAY, WEEK, MONTH
    }

    public Favorite() {}

    public Favorite(String name, long shelfLife, String notes) {
        this.name = name;
        this.shelfLife = shelfLife;
        this.notes = notes;
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

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

