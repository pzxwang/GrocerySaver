package cse110.grocerysaver;
import android.media.Image;
import java.util.UUID;
import java.util.Calendar;
import java.util.Date;

public class FoodItem {

    private String ID;
    private String foodName;
    private Image foodImage;
    private long addingDate;
    private long expirationDate;
    private String notes = "";
    private Boolean isInFavorite;
    private Boolean isExpired;

    //default constructor
    public FoodItem() {
        this.ID = UUID.randomUUID().toString();
        this.addingDate = System.currentTimeMillis();
    }

    //copy constructor for fetchMyFridge
    public FoodItem(String name, String id, String notes, long addDate, long expDate) {
        this.foodName = name;
        this.ID = id;
        this.notes = notes;
        this.addingDate = addDate;
        this.expirationDate = expDate;
    }

    //constructor without an expiration date
    public FoodItem(String foodName) {
        this.ID = UUID.randomUUID().toString();
        this.addingDate = System.currentTimeMillis();
        this.foodName = foodName;
    }

    //constructor with an expiration date
    public FoodItem(String foodName, long expirationDate, String notes) {
        this.ID = UUID.randomUUID().toString();
        this.addingDate = System.currentTimeMillis();
        this.foodName = foodName;
        this.expirationDate = expirationDate;
        this.notes = notes;
    }

    //constructor with name, image, and expiration date
    public FoodItem(String foodName, Image foodImage, long expirationDate) {
        this.ID = UUID.randomUUID().toString();
        this.addingDate = System.currentTimeMillis();
        this.foodName = foodName;
        this.foodImage = foodImage;
        this.expirationDate = expirationDate;
    }

    //setters
    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public void setFoodImage(Image foodImage) {
        this.foodImage = foodImage;
    }

    public void setExpirationDate(long expirationDate) {
        this.expirationDate = expirationDate;
    }

    public void setInFavorite(Boolean inFavorite) {
        isInFavorite = inFavorite;
    }

    public void setExpired(Boolean expired) {
        isExpired = expired;
    }


    //getters
    public String getID() {
        return ID;
    }

    public String getFoodName() {
        return foodName;
    }

    public Image getFoodImage() {
        return foodImage;
    }

    public long getAddingDate() {
        return addingDate;
    }

    public long getExpirationDate() {
        return expirationDate;
    }

    public Boolean getInFavorite() {
        return isInFavorite;
    }

    public Boolean getExpired() {
        return isExpired;
    }
}
