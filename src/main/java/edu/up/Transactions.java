package edu.up;

public class Transactions extends Item {
    private int quantity;
    private double totalPrice;

    public Transactions(String itemCode, String name, String itemType, String category, String sizePrice, String customization, int quantity, double totalPrice) {
        super(itemCode, name, itemType, category, sizePrice, customization);
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    // Implement abstract method
    @Override
    public void displayDetails() {
        System.out.printf("%d x %s (%s) - Customization: %s, Total: %.2f\n",
                quantity,
                getName(),
                getSizePrice(),
                getCustomization().equals("None") ? "No Customization" : getCustomization(),
                totalPrice);
    }
}
