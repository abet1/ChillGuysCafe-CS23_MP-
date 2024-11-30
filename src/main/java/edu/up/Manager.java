package edu.up;

import java.util.Scanner;

public class Manager {
    public static void managerMenu(Scanner userInput){
        while (true) {
            System.out.println("\nManager Menu:");
            System.out.println("(1) Add Item");
            System.out.println("(2) Encode Item from a file (csv file)");
            System.out.println("(3) Modify Item");
            System.out.println("(4) Delete Item");
            System.out.println("(5) Exit to Main Menu");
            System.out.print("Enter choice: ");
            String choice = userInput.nextLine();
            try{
                int managerSelection = Integer.parseInt(choice);
            }catch(Exception e){
                System.out.println("Invalid choice. Please input the number of your choice");
                continue;
            }
            switch (Integer.parseInt(choice)) {
                case 1:
                    ManagerServices.addItem(userInput);
                    break;
                case 2:
                    ManagerServices.encodeItem(userInput);
                    break;
                case 3:
                    ManagerServices.modifyItem(userInput);
                    break;
                case 4:
                    ManagerServices.deleteItem(userInput);
                    break;
                case 5:
                    System.out.println("Exiting manager menu. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
}
