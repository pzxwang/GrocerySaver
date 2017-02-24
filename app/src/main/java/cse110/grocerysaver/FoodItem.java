package cse110.grocerysaver;
import android.media.Image;
import java.util.UUID;
import java.util.Calendar;
import java.util.Date;

public class FoodItem {

    private String ID;
    private String foodName;
    private Image foodImage;
    private Date addingDate;
    private String expirationDate;
    private String notes = "";
    private Boolean isInFavorite;
    private Boolean isExpired;

    //instance to get the current date
    private Calendar c = Calendar.getInstance();

    //default constructor
    public FoodItem() {
        this.ID = UUID.randomUUID().toString();
        this.addingDate = c.getTime();
    }

    //copy constructor for fetchMyFridge
    public FoodItem(String name, String id, String notes, int addDate, int expDate) {
        this.foodName = name;
        this.ID = id;
        this.notes = notes;
        //this.addingDate = addDate;
        //this.expirationDate = expDate;
    }

    //constructor without an expiration date
    public FoodItem(String foodName) {
        this.ID = UUID.randomUUID().toString();
        this.addingDate = c.getTime();
        this.foodName = foodName;
    }

    //constructor with an expiration date
    public FoodItem(String foodName, String expirationDate) {
        this.ID = UUID.randomUUID().toString();
        this.addingDate = c.getTime();
        this.foodName = foodName;
        this.expirationDate = expirationDate;
    }

    //constructor with name, image, and expiration date
    public FoodItem(String foodName, Image foodImage, String expirationDate) {
        this.ID = UUID.randomUUID().toString();
        this.addingDate = c.getTime();
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

    public void setExpirationDate(String expirationDate) {
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

    public Date getAddingDate() {
        return addingDate;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public Boolean getInFavorite() {
        return isInFavorite;
    }

    public Boolean getExpired() {
        return isExpired;
    }
}
