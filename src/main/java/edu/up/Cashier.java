package edu.up;

import java.util.Scanner;

public class Cashier {
    public static void cashierMenu(Scanner userInput){
        while (true) {
            System.out.println("\nCashier Menu:");
            System.out.println("(1) Place Order");
            System.out.println("(2) Exit to Main Menu");
            System.out.print("Enter choice: ");
            String choice = userInput.nextLine();

            switch (choice) {
                case "1":
                    CashierServices.placeOrder(userInput);
                    break;
                case "2":
                    return;
                default:
                    System.out.println("Invalid choice. Try again.\n");
            }
        }
    }

}
