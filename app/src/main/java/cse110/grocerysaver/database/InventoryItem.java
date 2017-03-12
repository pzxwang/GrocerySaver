package cse110.grocerysaver.database;

import android.text.format.DateUtils;
import android.util.TimeUtils;

import java.util.Calendar;

/* Used to keep track of every food item ever inputted for autocomplete on text input */
public class InventoryItem extends Persistable {

    private String name;
    private Long shelfLife;
    // private String notes;

    public InventoryItem() {}

    public InventoryItem(String name, long shelfLife, String notes) {
        this.name = name;
        this.shelfLife = shelfLife;
        //this.notes = notes;
    }

    public String getName() {
        return name;
    }

    public long getShelfLife() { return shelfLife; }

    // public String getNotes() {return notes;}

    public void setName(String name) {
        this.name = name;
    }

    public void setShelfLife(long shelfLife) {
        this.shelfLife = shelfLife;
    }

    // public void setNotes(String notes) {this.notes = notes;}

    public Calendar getExpirationDate() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(c.getTimeInMillis() + shelfLife);
        return c;
    }


    // what is displayed in autocomplete arrayadapter
    @Override
    public String toString() {

        long expDays = shelfLife / DateUtils.DAY_IN_MILLIS;
        String expString = " (";

        if (expDays / 365 >= 1) {
            expString += (expDays/365) + " year";
            expString += (expDays/365 > 1) ? "s)" : ")";
        }
        else if (expDays / 30 >= 1) {
            expString += (expDays/30) + " month";
            expString += (expDays/30 > 1) ? "s)" : ")";
        }
        else {
            expString += expDays + " day";
            expString += (expDays > 1) ? "s)" : ")";
        }

        return name + expString;
    }
}