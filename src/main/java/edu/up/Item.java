package edu.up;

public abstract class Item {
    private String itemCode;
    private String name;
    private String itemType;
    private String category;
    private String sizePrice;
    private String customization;
    private int quantity;
    private int sold;

    public Item(String itemCode, String name, String itemType,String category, String sizePrice, String customization) {
        this.itemCode = itemCode;
        this.name = name;
        this.itemType = itemType;
        this.category = category;
        this.sizePrice = sizePrice;
        this.customization = customization;

    }
    public String getItemCode() {
        return itemCode;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getSizePrice() {
        return sizePrice;
    }

    public String getCustomization() {
        return customization;
    }

    public String getItemType() {
        return itemType;
    }

    public void setSizePrice(String sizePrice) {
        this.sizePrice = sizePrice;
    }

    public void setCustomization(String customization) {
        this.customization = customization;
    }


    public abstract void displayDetails();
}





