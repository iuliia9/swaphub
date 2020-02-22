package brighton.ac.uk.ic257.swaphub;

public class Item {

    String itemId;
    String itemName;
    String itemCategory;

    public Item(){

    }
    public Item(String itemId, String itemName, String itemCategory) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemCategory = itemCategory;
    }

    public String getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemCategory() {
        return itemCategory;
    }
}
