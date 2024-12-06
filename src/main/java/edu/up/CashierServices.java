package edu.up;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class CashierServices {
    public static void placeOrder(Scanner userChoice){
        List<Transactions> addToCart = new ArrayList<>();

        double totalPrice = 0.00;

        while(true){

            String selectedItemType = chooseItemType(userChoice);
            if(selectedItemType == null){
                System.out.println("Order cancelled.Bye!");
                break;
            }

            String selectedCategory = chooseCategory(userChoice, selectedItemType);
            if(selectedCategory == null){
                System.out.println("Order cancelled.Bye!");
                break;
            }

            Item itemToOrder = chooseItem(userChoice, selectedItemType, selectedCategory);
            if(itemToOrder == null){
                System.out.println("Order cancelled.Bye!");
                break;
            }

            String sizeOrder = chooseItemSize(userChoice,itemToOrder);
            double sizePrice = calculatePrice(itemToOrder.getSizePrice().trim(), sizeOrder);
            if (sizePrice == -1) {
                System.out.println("Invalid size.");
                continue;
            }

            double customizationPrice = 0.00;
            String specificCustomizationChoice = "none";
            String customizationCategory = chooseCustomizationCategory(userChoice, itemToOrder);
            if(customizationCategory == null){
                System.out.println("Order cancelled.Bye!");
                break;
            }
            if(customizationCategory.equalsIgnoreCase("none")){
                customizationPrice = 0.00;
            }else {
                specificCustomizationChoice = chooseSpecificCustomization(userChoice, itemToOrder, customizationCategory);
                if (specificCustomizationChoice == null) {
                    System.out.println("Order cancelled.Bye!");
                    break;
                } else if (specificCustomizationChoice.equalsIgnoreCase("none")) {
                    customizationPrice = 0.00;
                    specificCustomizationChoice = "none";
                } else if(specificCustomizationChoice.equalsIgnoreCase("others")) {
                    System.out.println("Enter you custom customization");
                    specificCustomizationChoice = userChoice.nextLine();
                    customizationPrice = 25.00;
                }else {
                    customizationPrice = calculatePrice(itemToOrder.getCustomization(), specificCustomizationChoice);
                }
            }


            int quantity = itemQuantity(userChoice);

            totalPrice = finalizeOrder(itemToOrder, sizeOrder,sizePrice, specificCustomizationChoice, quantity, customizationPrice, addToCart, totalPrice);


            System.out.println("\nDo you want to add more items? (yes/no)");
            String addMoreItems = userChoice.nextLine();
            if (addMoreItems.equalsIgnoreCase("no")) {
                break;
            } else if (!addMoreItems.equalsIgnoreCase("yes") && !addMoreItems.equalsIgnoreCase("no")) {
                System.out.println("Invalid add more item. Please try again.");
                break;
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
                if(priceArray.length == 2){
                    String key = priceArray[0].trim().toLowerCase();
                    String value = priceArray[1].trim().toLowerCase();
                    if(key.equalsIgnoreCase(userChoice)){
                        return Double.parseDouble(value);
                    }
                }else if(priceArray.length == 3){
                    String key = priceArray[1].trim().toLowerCase();
                    String value = priceArray[2].trim().toLowerCase();
                    if(key.equalsIgnoreCase(userChoice)){
                        return Double.parseDouble(value);
                    }
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

    public static String chooseItemType(Scanner userChoice){
        while(true) {
            System.out.println("\nWhat type of item do you want to buy: (drink, food, merchandise)");
            System.out.println("If you don't want to proceed type 'cancel'");
            String itemType = userChoice.nextLine().trim().toLowerCase();

            if (itemType.equalsIgnoreCase("cancel")) {
                System.out.println("Exiting order process!");
                return null;
            }

            if (!itemType.equalsIgnoreCase("drink") && !itemType.equalsIgnoreCase("food") && !itemType.equalsIgnoreCase("merchandise")) {
                System.out.println("Invalid item type. Please choose Drink, Food, or Merchandise.");
            }else{
                return itemType;
            }
        }
    }

    public static String chooseCategory(Scanner userChoice, String itemType){
        List<Item> itemsInItemType = SQLDriver.sqlFindMenuItemsByItemType(itemType);
        ArrayList<String> categories = new ArrayList<>();
        for (Item item : itemsInItemType) {
            if(!categories.contains(item.getCategory())){
                categories.add(item.getCategory());
            }
        }

        if(categories.isEmpty()){
            System.out.println("There is no available product for you chosen item type");
            return null;
        }

        System.out.println("Here's the available " + itemType + " categories:");
        for(int i=0;i<categories.size();i++){
            System.out.println("("+(i+1)+"): "+categories.get(i));
        }

        while(true){
            System.out.println("Enter the category number or type cancel to exit");
            String category = userChoice.nextLine().trim();

            if(category.equalsIgnoreCase("cancel")){
                return null;
            }

            try{
                int choice = Integer.parseInt(category);
                if(choice>=1 && choice<=categories.size()){
                    return categories.get(choice-1);
                }
            }catch(NumberFormatException e){
                System.out.println("Invalid category number. Please try again.");
            }

            System.out.println("Invalid category number. Please try again.");
        }
    }

    public static Item chooseItem(Scanner userChoice, String itemType, String itemCategory){
        List<Item> itemsInCategory = SQLDriver.sqlFindMenuItemsByCategory(itemCategory);
        if(itemsInCategory.isEmpty()){
            System.out.println("There is no available product for you chosen category");
            return null;
        }
        System.out.println("Here's the available " + itemCategory + " items:");
        Item itemToOrder = null;

        for(Item item : itemsInCategory){
            System.out.println(item.getItemCode() + ": " + item.getName());
        }
        while(true){
            System.out.println("Enter the last 3 number of the item code you want to order or type cancel to exit");
            String itemChoice = userChoice.nextLine().trim();

            if(itemChoice.equalsIgnoreCase("cancel")){
                return null;
            }

            for(Item item : itemsInCategory){
                String itemNumber = item.getItemCode().substring(8);
                if(itemNumber.equalsIgnoreCase(itemChoice)){
                    itemToOrder = item;
                }
            }
            if(itemToOrder == null){
                System.out.println("Item not found. Please try again.");
                continue;
            }

            System.out.println("You selected " + itemToOrder.getName() + ". Are you sure? (yes/no)");
            String yesOrNo = userChoice.nextLine().toLowerCase().trim();
            if(yesOrNo.equalsIgnoreCase("yes")){
                return itemToOrder;
            }else if(yesOrNo.equalsIgnoreCase("no")){
                System.out.println("Restarting selecting item!");
            }else{
                System.out.println("Invalid selection. Please try again.");
            }

        }
    }

    public static String chooseItemSize(Scanner userChoice, Item itemToOrder){
        String[] sizePrice = itemToOrder.getSizePrice().split(",");
        List<String> sizes = new ArrayList<>();
        for(String size : sizePrice){
            String[] sizePriceParts = size.trim().split("=");
            if(sizePriceParts.length == 2){
                if(!sizes.contains(sizePriceParts[0].trim())){
                    sizes.add(sizePriceParts[0].trim());
                }
            }
        }

        System.out.println("Sizes and Price: " + itemToOrder.getSizePrice());
        while(true) {
            System.out.println("Enter the size you want to buy (type cancel to exit):");
            String sizeOrder = userChoice.nextLine().toLowerCase().trim();
            if (sizeOrder.equalsIgnoreCase("cancel")) {
                return null;
            }else if (!sizes.contains(sizeOrder)) {
                System.out.println("Invalid size. Please try again.");
            }else{
                return sizeOrder;
            }
        }
    }

    public static String chooseCustomizationCategory(Scanner userChoice, Item itemToOrder){
        String[] customizations = itemToOrder.getCustomization().split(",");
        List<String> customizationsCategory = new ArrayList<>();

        for(String customization : customizations){
           String[] customizationsParts = customization.split("=");
           if(customizationsParts.length == 3){
               if(!customizationsCategory.contains(customizationsParts[0].trim())){
                   customizationsCategory.add(customizationsParts[0].trim());
               }
           }
        }

        if(customizationsCategory.isEmpty()){
            return "none";
        }

        while(true){
            System.out.println("Here's the available " + itemToOrder.getName() + " customizations:");
            for(int i=0;i<customizationsCategory.size();i++){
                System.out.println("("+(i+1)+"): "+customizationsCategory.get(i));
            }
            System.out.println("Enter the number of your choice (type cancel to exit, or type none if you don't want customization): ");

            String customizationsCategoryChoice = userChoice.nextLine().trim();
            if(customizationsCategoryChoice.equalsIgnoreCase("cancel")){
                return null;
            }else if(customizationsCategoryChoice.equalsIgnoreCase("none")){
                return "none";
            }else {

                try {
                    int choice = Integer.parseInt(customizationsCategoryChoice);
                    if (choice >= 1 && choice <= customizationsCategory.size()) {
                        return customizationsCategory.get(choice - 1);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid category number. Please try again.");
                }
            }
        }
    }

    public static String chooseSpecificCustomization(Scanner userChoice, Item itemToOrder, String customizationCategory){
        String[] customizations = itemToOrder.getCustomization().split(",");
        List<String> specificCustomizations = new ArrayList<>();


        for(String customization : customizations){
            String[] customizationsParts = customization.split("=");
            if(customizationsParts.length == 3){
                if(customizationsParts[0].equalsIgnoreCase(customizationCategory)){
                    specificCustomizations.add(customizationsParts[1].trim());
                }

            }
        }

        while(true){
            System.out.println("Here's the available " + itemToOrder.getName() + " " + customizationCategory + " customizations:");

            for(int i = 0; i< specificCustomizations.size(); i++){
                System.out.println("("+(i+1)+"): "+ specificCustomizations.get(i));
            }
            System.out.println("Enter the number of your choice (type cancel to exit, or type none if you don't want customization, or type others for custom customization): ");

            String customizationsChoice = userChoice.nextLine().trim();
            if(customizationsChoice.equalsIgnoreCase("cancel")){
                return null;
            }else if(customizationsChoice.equalsIgnoreCase("none")){
                return "none";
            }else if(customizationsChoice.equalsIgnoreCase("others")){
                return "others";
            }else {
                try {
                    int choice = Integer.parseInt(customizationsChoice);
                    if (choice >= 1 && choice <= specificCustomizations.size()) {
                        return specificCustomizations.get(choice - 1);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid category number. Please try again.");
                }
            }
            System.out.println("Invalid choice.Try again.");
        }
    }
    public static double finalizeOrder(Item itemToOrder, String sizeOrder, double sizePrice, String specificCustomizationChoice, int quantity, double customizationPrice, List<Transactions> addToCart, double totalPrice){
        double itemTotal = (sizePrice+customizationPrice)*quantity;
        totalPrice = totalPrice + itemTotal;
        addToCart.add(new Transactions(
                itemToOrder.getItemCode(),
                itemToOrder.getName(),
                itemToOrder.getItemType(),
                itemToOrder.getCategory(),
                sizeOrder,
                specificCustomizationChoice,
                quantity,
                itemTotal
        ));
        System.out.printf("Added %d x %s to your cart. Your subtotal: %.2f\n", quantity, itemToOrder.getName(), totalPrice);

        return totalPrice;
    }

    public static int itemQuantity(Scanner userChoice){
        while(true) {
            System.out.println("Enter quantity");
            int quantity;
            try {
                quantity = userChoice.nextInt();
                userChoice.nextLine();
                if (quantity <= 0) {
                    System.out.println("Quantity must be greater than 0. Please try again.");
                }
                return quantity;
            } catch (InputMismatchException e) {
                System.out.println("Invalid quantity. Please enter a valid integer.");
                userChoice.nextLine();
            }
        }
    }
}
