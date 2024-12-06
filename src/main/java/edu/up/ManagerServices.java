package edu.up;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ManagerServices {
    public static void addItem(Scanner userInput){
        System.out.println("\nAdd Item");
        System.out.print("Enter Name: ");
        String name = userInput.nextLine();
        System.out.print("Enter Item Type: ");
        String itemType = userInput.nextLine();
        System.out.print("Enter Category: ");
        String category = userInput.nextLine();
        System.out.print("Enter Sizes and their corresponding prices (ex. Small=99, Medium=149, Large=199):  ");
        String sizePrice = userInput.nextLine();
        System.out.print("Enter customization and their corresponding prices (ex. Soy Milk=45, Oat Milk=40) ");
        String customization = userInput.nextLine();

        String itemCode = itemCodeGenerator(category, name);
        SQLDriver.sqlAddMenuItem(itemCode, name, itemType ,category, sizePrice, customization);
    }

    public static void encodeItem(Scanner userInput){
        System.out.println("\nEncode Item");
        System.out.print("Enter the filename to encode items(txt file): ");
        String filename = userInput.nextLine();
        List<Item> items = SQLDriver.sqlLoadMenuItems();

        try{
            File file = new File(filename);
            Scanner fileReader = new Scanner(file);


            while(fileReader.hasNextLine()){
                String line = fileReader.nextLine();
                String [] itemInformation = line.split(",");
                if(itemInformation.length == 6){
                    String itemCode = itemInformation[0];
                    String itemName = itemInformation[1];
                    String itemType = itemInformation[2];
                    String itemCategory = itemInformation[3];
                    String itemSize = itemInformation[4];
                    String customizations = itemInformation[5];

                    if (itemCode == null || itemCode.isBlank() || isDuplicateItemCode(items, itemCode)){
                        itemCode = itemCodeGenerator(itemCategory, itemName);
                    }

                    SQLDriver.sqlAddMenuItem(itemCode, itemName, itemType, itemCategory, itemSize, customizations);
                    System.out.println("Item " + itemCode +":"+itemName+ " added to the database");
                }else{
                    System.out.println("Invalid format in file line: " + line);
                }
            }

        }catch(FileNotFoundException e){
            System.out.println("File not found" + e.getMessage());
        }
    }

    private static boolean isDuplicateItemCode(List<Item> items, String itemCode){
        for(Item item : items){
            if(itemCode.equalsIgnoreCase(item.getItemCode())){
                return true;
            }else{
                return false;
            }
        }
        return false;
    }

    public static void modifyItem(Scanner userInput){
        Item itemToModify = null;
        System.out.println("\nModify Item");
        System.out.print("Enter the item code of the item you want to modify: ");
        String itemCode = userInput.nextLine().trim();
        itemToModify = SQLDriver.sqlFindMenuItemByItemCode(itemCode);

        if (itemToModify == null) {
            System.out.println("Item with code" + itemCode + " does not exist in the menu.");
            return;
        }

        System.out.println("\n Current details of item " + itemToModify.getItemCode());
        System.out.println("Name: " + itemToModify.getName());
        System.out.println("Category: " + itemToModify.getCategory());
        System.out.println("Size and Price: " + itemToModify.getSizePrice());
        System.out.println("Customization: " + itemToModify.getCustomization());

        String choice = "0";
        while(!choice.equals("3")) {
            System.out.println("What do you want to modify?");
            System.out.println("1. Size and Price");
            System.out.println("2. Customization");
            System.out.println("3. Exit");
            choice = userInput.nextLine().trim();


            switch (choice) {
                case "1":
                    System.out.print("Enter the new size and prices of the item: ");
                    String newSizePrice = userInput.nextLine();
                    itemToModify.setSizePrice(newSizePrice);
                    break;
                case "2":
                    System.out.print("Enter the new customizations of the item: ");
                    String newCustomization = userInput.nextLine();
                    itemToModify.setCustomization(newCustomization);
                    break;
                case "3":
                    System.out.print("Exiting modify item menu. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
        String sizePrice=itemToModify.getSizePrice();
        String customizations=itemToModify.getCustomization();

        SQLDriver.sqlModifyMenuItem(itemCode, sizePrice, customizations);


    }

    public static void deleteItem(Scanner userInput){
        Item itemToDelete = null;
        System.out.println("\nDelete Item");
        System.out.print("Enter the item code of the item you want to delete: ");
        String itemCode = userInput.nextLine().trim();

        itemToDelete = SQLDriver.sqlFindMenuItemByItemCode(itemCode);

        if (itemToDelete == null) {
            System.out.println("Item with code" + itemCode + " does not exist in the menu.");
            return;
        }

        System.out.println("\n Current details of item " + itemToDelete);
        System.out.println("Name: " + itemToDelete.getName());
        System.out.println("Category: " + itemToDelete.getCategory());
        System.out.println("Size and Price: " + itemToDelete.getSizePrice());
        System.out.println("Customization: " + itemToDelete.getCustomization());

        System.out.println("Are you sure you want to delete this item? (yes/no)");
        String choice = userInput.nextLine().trim().toLowerCase();
        switch (choice) {
            case "yes":
                SQLDriver.sqlDeleteMenuItem(itemCode);
                break;
            case "no":
                System.out.println("Item with code" + itemCode + " will not be deleted. Goodbye!");
                return;
            default:
                System.out.println("Invalid choice. Try again.");
        }
    }

    public static String itemCodeGenerator(String category, String name){
        String categoryCode = category.substring(0, 1).toUpperCase() + category.substring(category.length()-1).toUpperCase();

        String nameCode = name.length() > 4 ? name.substring(0, 4).toUpperCase(): name.toUpperCase();

        List<Item> categoryitems = SQLDriver.sqlFindMenuItemsByCategory(category);
        List<Integer> usedCodeNumbers = new ArrayList<>();

        for(Item item : categoryitems){
            String [] parts = item.getItemCode().split("-");
            if(parts.length == 3){
                try{
                    usedCodeNumbers.add(Integer.parseInt(parts[2]));
                }catch(NumberFormatException e){
                    System.out.println("Invalid item code: " + item.getItemCode());
                }
            }
        }

        int availableNumber = 1;
        while(usedCodeNumbers.contains(availableNumber)){
            availableNumber++;
        }
        String itemNumber = String.format("%03d", availableNumber);
        return categoryCode +"-"+nameCode +"-"+ itemNumber;
    }
}
