package edu.up;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.math.BigDecimal;

public class CashierServices {
    public static void placeOrder(Scanner userChoice){
        List<Transactions> addToCart = new ArrayList<>();
        String itemType;
        double totalPrice = 0.00;

        while(true){

            System.out.println("\nWhat type of item do you want to buy: (drink, food, merchandise)");
            System.out.println("If you don't want to proceed type 'cancel'");
            itemType = userChoice.nextLine().trim().toLowerCase();

            if(itemType.equalsIgnoreCase("cancel")){
                System.out.println("Exiting order process!");
                break;
            }

            if(!itemType.equalsIgnoreCase("drink")&&!itemType.equalsIgnoreCase("food")&&!itemType.equalsIgnoreCase("merchandise")){
                System.out.println("Invalid item type. Please choose Drink, Food, or Merchandise.");
                continue;
            }

            List<Item> itemsInItemType = SQLDriver.sqlFindMenuItemsByItemType(itemType);
            ArrayList<String> categories = new ArrayList<>();
            for (Item item : itemsInItemType) {
                if(!categories.contains(item.getCategory())){
                    categories.add(item.getCategory());
                }
            }

            if(categories.isEmpty()){
                System.out.println("There is no available product for you chosen item type");
                continue;
            }

            System.out.println("Here's the available " + itemType + " categories:");
            for(int i=0;i<categories.size();i++){
                System.out.println("("+(i+1)+"): "+categories.get(i));
            }

            System.out.println("Enter the category you want to view or type cancel to start again:");
            String selectedCategory = userChoice.nextLine().trim();

            if(selectedCategory.equalsIgnoreCase("cancel")){
                System.out.println("Going back to item type selection!");
                continue;
            }

            List<Item> itemsInCategory = SQLDriver.sqlFindMenuItemsByCategory(selectedCategory);
            ArrayList<String> itemNames = new ArrayList<>();
            for(Item item : itemsInCategory){
                if(item.getItemType().equalsIgnoreCase(itemType)){
                    itemNames.add(item.getName());
                }
            }

            if(itemNames.isEmpty()){
                System.out.println("There is no available product for you chosen category");
                continue;
            }

            System.out.println("\nItems in category " + selectedCategory + ":");

            for(Item item : itemsInCategory){
                System.out.println(item.getItemCode() + ":"+item.getName());
            }

            Item itemToOrder = null;
            System.out.println("Enter the last 3 numbers of the item you want to order or type cancel to start again:");
            String itemNumberOrder = userChoice.nextLine().trim();

            if(itemNumberOrder.equalsIgnoreCase("cancel")){
                System.out.println("Going back to item type selection!");
                break;
            }

            for(Item item : itemsInCategory){
                String itemNumber = item.getItemCode().substring(8);
                if(itemNumber.equalsIgnoreCase(itemNumberOrder)){
                    itemToOrder = item;
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

                double sizePrice = calculatePrice(itemToOrder.getSizePrice().trim(), sizeOrder);
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

                if (customizationPrice == -1) {
                    System.out.println("Invalid customization.");
                    continue;
                }

                System.out.println("Enter quantity");
                int quantity;
                try{
                    quantity = userChoice.nextInt();
                    userChoice.nextLine();
                    if(quantity<=0){
                        System.out.println("Quantity must be greater than 0. Please try again. The order is cancelled.");
                        continue;
                    }
                }catch(InputMismatchException e){
                    System.out.println("Invalid quantity. Please enter a valid integer. The order is cancelled.");
                    userChoice.nextLine();
                    continue;
                }


                double itemTotal = (sizePrice+customizationPrice)*quantity;
                totalPrice = totalPrice + itemTotal;
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
                System.out.println("\nDo you want to add more items? (yes/no)");
                String addMoreItems = userChoice.nextLine().toLowerCase().trim();
                if(addMoreItems.equals("yes")){
                    continue;
                }else if(addMoreItems.equals("no")){
                    break;
                }else{
                    System.out.println("Invalid choice. Please try again.");
                    break;
                }
            }else if(yesOrNo.equals("no")){
                continue;
            }else{
                System.out.println("Invalid choice.");
                continue;
            }
        }

        System.out.println("\nDo you want to complete the transaction? (yes/no)");
        String completeTransaction = userChoice.nextLine().toLowerCase().trim();
        if(completeTransaction.equals("yes")){
            if(!addToCart.isEmpty()){
                receiptGenerator(addToCart, totalPrice);
            }else{
                System.out.println("No items added to your cart.");
            }
        }else if(completeTransaction.equals("no")){
            System.out.println("Emptying your cart. Goodbye!");
        }
    }

    public static double calculatePrice(String choices, String userChoice) {
        if(userChoice.equalsIgnoreCase("none")){
            return 0.00;
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
