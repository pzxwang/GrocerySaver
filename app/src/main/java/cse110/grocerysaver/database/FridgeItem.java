package cse110.grocerysaver.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import java.util.Calendar;
import android.net.Uri;
import android.provider.ContactsContract;


/**
 * Created by Philip on 2/25/17.
 */

public class FridgeItem extends Persistable {

    private String name;
    private Long dateAdded;
    private Long expirationDate;
    private String notes;

    public FridgeItem() {};

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

    public String getNotes() {
        return notes;
    }

    public long getShelfLife() {
        return expirationDate - dateAdded;
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
