package edu.up;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
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
        Item newItem = new ProductToSell(itemCode, name, itemType ,category, sizePrice, customization);
        List<Item> items = CSVDriver.showItems();
        items.add(newItem);
        CSVDriver.saveItem(items);

        System.out.println("Item " + itemCode + " is added successfully to the menu.");
    }

    public static void encodeItem(Scanner userInput){
        System.out.println("\nEncode Item");
        System.out.print("Enter the filename of encode items(csv file): ");
        String filename = userInput.nextLine();

        try(CSVReader filereader = new CSVReader(new FileReader(filename))){
            List<Item> items = CSVDriver.showItems();
            String[] nextLine;

            while((nextLine = filereader.readNext()) != null){
                if(nextLine.length == 6){
                    String itemCode = nextLine[0];
                    String itemName = nextLine[1];
                    String itemType = nextLine[2];
                    String itemCategory = nextLine[3];
                    String itemSize = nextLine[4];
                    String customization = nextLine[5];

                    if (itemCode == null || itemCode.isBlank() || isDuplicateItemCode(items, itemCode)){
                        itemCode = itemCodeGenerator(itemCategory, itemName);
                    }
                }
            }

        }catch(IOException e){
            System.out.println("Error reading file" + e);
        } catch (CsvValidationException e) {
            System.out.println("Invalid format" + e);
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
        List<Item> items = CSVDriver.showItems();

        for (Item item : items) {
            if (item.getItemCode().equals(itemCode)) {
                itemToModify = item;
                break;
            }
        }
        if (itemToModify == null) {
            System.out.println("Item with code" + itemCode + " does not exist in the menu.");
            return;
        }

        System.out.println("\n Current details of item " + itemToModify.getItemCode());
        System.out.println("Name: " + itemToModify.getName());
        System.out.println("Category: " + itemToModify.getCategory());
        System.out.println("Size and Price: " + itemToModify.getSizePrice());
        System.out.println("Customization: " + itemToModify.getCustomization());

        System.out.println("What do you want to modify?");
        System.out.println("1. Size and Price");
        System.out.println("2. Customization");
        System.out.println("3. Exit");
        String choice = userInput.nextLine().trim();

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

        CSVDriver.saveItem(items);
        System.out.println("Item " + itemToModify.getItemCode() + " has been modified successfully.");



    }

    public static void deleteItem(Scanner userInput){
        Item itemToDelete = null;
        System.out.println("\nDelete Item");
        System.out.print("Enter the item code of the item you want to delete: ");
        String itemCode = userInput.nextLine().trim();
        List<Item> items = CSVDriver.showItems();

        for (Item item : items) {
            if (item.getItemCode().equals(itemCode)) {
                itemToDelete = item;
                break;
            }
        }
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
                items.removeIf(item -> item.getItemCode().equals(itemCode));
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
        List<Item> items = CSVDriver.showItems();
        long count = items.stream().filter(item -> item.getCategory().equalsIgnoreCase(category)).count();
        String itemNumber = String.format("%03d", count+1);
        return categoryCode +"-"+nameCode +"-"+ itemNumber;
    }
}
