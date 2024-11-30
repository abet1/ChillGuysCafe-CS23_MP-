package edu.up;

import java.util.Scanner;

public class BasicItemsAndSaleSystem {

    public static void main(String[] args) {
        Scanner userInput = new Scanner(System.in);
        while(true) {
            System.out.println("Select user:");
            System.out.println("(1) Manager");
            System.out.println("(2) Cashier");
            System.out.println("(3) Exit");
            String choice = userInput.nextLine();
            try{
                int roleSelection = Integer.parseInt(choice);
            }catch(Exception e){
                System.out.println("Invalid choice. Please input the number of your choice");
                continue;
            }
            switch (Integer.parseInt(choice)) {
                case 1:
                    Manager.managerMenu(userInput);
                    break;
                case 2:
                    Cashier.cashierMenu(userInput);
                    break;
                case 3:
                    System.out.println("Exiting system. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }

        }
    }
}