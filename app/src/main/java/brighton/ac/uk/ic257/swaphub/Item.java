package brighton.ac.uk.ic257.swaphub;

public class Item {

    String itemId;
    String itemName;
    String itemCategory;
    String itemDescription;
    String itemSwapFor;
    String sellerName;
    String sellerPhone;

    public Item(){

    }
    public Item(String itemId, String itemName, String itemCategory, String itemDescription,
                String itemSwapFor, String sellerName, String sellerPhone) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemCategory = itemCategory;
        this.itemDescription = itemDescription;
        this.itemSwapFor = itemSwapFor;
        this.sellerName = sellerName;
        this.sellerPhone = sellerPhone;
    }

    public String getItemId() {
        return itemId;
    }

    public String getItemDescription() { return itemDescription; }

    public String getItemName() {
        return itemName;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public String getItemSwapFor() { return itemSwapFor; }

    public String getSellerName() { return sellerName; }

    public String getSellerPhone() { return sellerPhone; }

    public void setItemName(String itemName) { this.itemName = itemName; }

    public void setItemId(String itemId) { this.itemId = itemId; }

    public void setItemCategory(String itemCategory) { this.itemCategory = itemCategory; }

    public void setItemDescription(String itemDescription) { this.itemDescription = itemDescription; }

    public void setItemSwapFor(String itemSwapFor) { this.itemSwapFor = itemSwapFor; }

    public void setSellerName(String sellerName) { this.sellerName = sellerName; }

    public void setSellerPhone(String sellerPhone) { this.sellerPhone = sellerPhone; }
}