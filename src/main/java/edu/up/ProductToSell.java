package edu.up;

public class ProductToSell extends Item{

    public ProductToSell(String itemcode, String name, String itemType ,String category, String sizePrice, String customization){
        super(itemcode, name, itemType , category, sizePrice, customization);
    }
    @Override
    public void displayDetails() {
        System.out.printf("Item: %s (%s), Sizes: %s, Customizations: %s\n", getName(), getCategory(), getSizePrice(), getCustomization());
    }
}
