package cse110.grocerysaver.database;

/* Used to keep track of every food item ever inputted for autocomplete on text input */
public class InventoryItem extends Persistable {

    private String name;

    public InventoryItem() {}

    public InventoryItem(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}