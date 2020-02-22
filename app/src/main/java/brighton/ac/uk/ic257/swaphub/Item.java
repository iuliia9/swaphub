package brighton.ac.uk.ic257.swaphub;

public class Item {

    String itemId;
    String itemName;
    String itemGenre;

    public Item(){

    }
    public Item(String itemId, String itemName, String itemGenre) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemGenre = itemGenre;
    }

    public String getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemGenre() {
        return itemGenre;
    }
}
