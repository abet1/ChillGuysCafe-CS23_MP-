package edu.up;

import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CashierServices {
    public static void placeOrder(Scanner userChoice){
        List<Item> items = SQLDriver.sqlLoadMenuItems();
        List<Transactions> addToCart = new ArrayList<>();
        double totalPrice = 0.00;

        while(true){

            System.out.println("\nWhat type of item do you want to buy: (drink, food, merchandise)");
            System.out.println("If you don't want to proceed type 'done'");
            String itemType = userChoice.nextLine().trim();

            if(itemType.equalsIgnoreCase("done")){
                System.out.println("Exiting order process!");
                break;
            }

            if(!itemType.equalsIgnoreCase("drink")&&!itemType.equalsIgnoreCase("food")&&!itemType.equalsIgnoreCase("merchandise")){
                System.out.println("Invalid item type. Please choose Drink, Food, or Merchandise.");
                continue;
            }

            ArrayList<String> categories = new ArrayList<>();
            for (Item item : items) {
                if(item.getItemType().equalsIgnoreCase(itemType)){
                    if(!categories.contains(item.getCategory())){
                        categories.add(item.getCategory());
                    }
                }
            }

            if(categories.isEmpty()){
                System.out.println("There is no available product for you chosen item type");
                continue;
            }

            System.out.println("Here's the available " + itemType + " categories:");
            for(String category : categories){
                System.out.println(category);
            }

            System.out.println("Enter the category you want to view");
            String selectedCategory = userChoice.nextLine().trim();


            ArrayList<String> itemNames = new ArrayList<>();
            for(Item item : items){
                if(item.getItemType().equalsIgnoreCase(itemType)&&item.getName().equalsIgnoreCase(selectedCategory)){
                    itemNames.add(item.getName());
                }
            }
            System.out.println("\nItems in category " + selectedCategory + ":");

            Item itemToOrder = null;
            System.out.println("Enter the item code of order or type done to finish:");
            String itemCodeOrder = userChoice.nextLine();

            if(itemCodeOrder.equalsIgnoreCase("done")){
                break;
            }

            for(Item item : items){
                if(item.getItemCode().equalsIgnoreCase(itemCodeOrder)){
                    itemToOrder = item;
                    break;
                }
            }

            if(itemToOrder == null){
                System.out.println("Item not found");
                continue;
            }

            System.out.println("You selected " + itemToOrder.getName() + ". Are you sure? (yes/no)");
            String yesOrNo = userChoice.nextLine().toLowerCase().trim();
            if(yesOrNo.equals("yes")){
                System.out.println("Sizes and Price: " + itemToOrder.getSizePrice());
                System.out.println("Enter the size you want to buy:");
                String sizeOrder = userChoice.nextLine().toLowerCase().trim();

                double sizePrice = calculatePrice(itemToOrder.getSizePrice(), sizeOrder);
                if (sizePrice == -1) {
                    System.out.println("Invalid size.");
                    continue;
                }

                System.out.println("Customizations: "+ itemToOrder.getCustomization());
                System.out.println("Enter the customization you want: ('None' for no customization, 'Others' for custom customization)");
                String customization = userChoice.nextLine().toLowerCase().trim();
                double customizationPrice = 0.00;
                if(customization.equals("others")){
                    System.out.println("Enter the custom customization you want: ");
                    customization = userChoice.nextLine().toLowerCase().trim();
                    customizationPrice = 25.00;
                }else {
                    customizationPrice = calculatePrice(itemToOrder.getCustomization(), customization);
                }

                System.out.println("Enter quantity");
                int quantity = userChoice.nextInt();
                userChoice.nextLine();

                double itemTotal = (sizePrice + customizationPrice)*quantity;
                totalPrice += itemTotal;

                addToCart.add(new Transactions(
                        itemToOrder.getItemCode(),
                        itemToOrder.getName(),
                        itemToOrder.getItemType(),
                        itemToOrder.getCategory(),
                        sizeOrder,
                        customization,
                        quantity,
                        itemTotal
                ));

                System.out.printf("Added %d x %s to your cart. Your subtotal: %.2f\n", quantity, itemToOrder.getName(), itemTotal);
            }else if(yesOrNo.equals("no")){
                continue;
            }else{
                System.out.println("Invalid choice.");
                continue;
            }
        }

        if(!addToCart.isEmpty()){
            receiptGenerator(addToCart, totalPrice);
        }else{
            System.out.println("No items added to your cart.");
        }
    }

    public static double calculatePrice(String choices, String userChoice) {
        if(choices.equalsIgnoreCase("none")){
            return 0.0;
        }

        String[] choicesArray = choices.split(",");
        for(String choice : choicesArray){
            String[] priceArray = choice.split("=");
                if(priceArray.length ==2 && priceArray[0].trim().equalsIgnoreCase(userChoice.trim())){
                    return Double.parseDouble(priceArray[1].trim());
                }
        }
        return -1;
    }

    public static void receiptGenerator(List<Transactions> addToCart, double totalPrice) {
        System.out.println("\n---------- RECEIPT ----------");
        for(Transactions transaction : addToCart){
            transaction.displayDetails();
        }
        System.out.println("------------------------");
        System.out.printf("Total Price: %.2f\n", totalPrice);
        System.out.println("Thank you, come again!!");
    }

}
