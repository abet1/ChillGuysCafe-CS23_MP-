package edu.up;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVDriver {
    private static final String MENU_FILE = "menu.csv";
    private static final String TRANSACTIONS_FILE = "transactions.csv";


    public static void fileCreatorIfNoFile(String fileName) {
        java.io.File file = new java.io.File(fileName);
        try{
            if(!file.exists()){
                file.createNewFile();
            }
        }catch(java.io.IOException e){
            System.out.println("Error creating file" + fileName +e.getMessage());
        }
    }
    public static List<Item> showItems(){
        fileCreatorIfNoFile(MENU_FILE);
        List<Item> items = new ArrayList<>();
        try (CSVReader fileReader = new CSVReader(new FileReader(MENU_FILE))) {
            String[] nextLine;
            while ((nextLine = fileReader.readNext()) != null) {
                if (nextLine.length == 6) {
                    items.add(new ProductToSell(nextLine[0], nextLine[1], nextLine[2], nextLine[3], nextLine[4], nextLine[5]));
                }
            }
        } catch (IOException e) {
            System.out.println("\nError reading menu file: " + e.getMessage());
        } catch (CsvValidationException e) {
            System.out.println("\nError validating CSV format: " + e.getMessage());
        }
        return items;
    }

    public static void saveItem(List<Item> items) {
        fileCreatorIfNoFile(MENU_FILE);
        try(CSVWriter filewriter = new CSVWriter(new FileWriter(MENU_FILE))) {
            for (Item item : items) {
                filewriter.writeNext(new String[]{
                        item.getItemCode(),
                        item.getName(),
                        item.getItemType(),
                        item.getCategory(),
                        item.getSizePrice(),
                        item.getCustomization()
                });
                System.out.println("Item " + item.getItemCode() + " is added successfully to the menu.");
            }
        } catch (IOException e){
            System.out.println("\nError writing into menu file: " + e.getMessage());
        }
    }

    public static void saveTransactions(List<Transactions> transactions, double totalPrice){
        fileCreatorIfNoFile(TRANSACTIONS_FILE);
        try(CSVWriter filewriter = new CSVWriter(new FileWriter(TRANSACTIONS_FILE))) {
            for (Transactions transaction : transactions) {
                filewriter.writeNext(new String[]{
                        transaction.getItemCode(),
                        transaction.getName(),
                        transaction.getItemType(),
                        transaction.getCategory(),
                        transaction.getSizePrice(),
                        transaction.getCustomization(),
                        String.valueOf(transaction.getQuantity()),
                        String.format("%.2f", transaction.getTotalPrice()),
                });
            }
        } catch (IOException e){
            System.out.println("\nError saving transaction: " + e.getMessage());
        }
    }
}
