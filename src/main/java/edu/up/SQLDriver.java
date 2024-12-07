package edu.up;

import java.sql.*;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;


public class SQLDriver {
    public static void sqlSetUp() {
        try(Connection sqlconnection = SQLConnection.getConnection();Statement stmt = sqlconnection.createStatement()){

            String createMenu = "CREATE TABLE IF NOT EXISTS menu ("+
                    "ItemCode TEXT PRIMARY KEY, "+
                    "Name TEXT NOT NULL, "+
                    "ItemType TEXT NOT NULL,"+
                    "Category TEXT NOT NULL,"+
                    "SizePrice TEXT NOT NULL,"+
                    "Customizations TEXT);";


            stmt.executeUpdate(createMenu);


        }catch(SQLException e){
            System.out.println("Error setting up the database" + e.getMessage());
        }
    }

    public static void sqlAddMenuItem(String itemCode, String name, String itemType, String category, String sizePrice, String customizations) {
        String addSQL = "INSERT INTO menu (itemCode, name, itemType, category, sizePrice, customizations) VALUES (?, ?, ?, ?, ?,?)";

        try(Connection sqlconnection = SQLConnection.getConnection(); PreparedStatement pstmt = sqlconnection.prepareStatement(addSQL)){

            pstmt.setString(1, itemCode);
            pstmt.setString(2, name);
            pstmt.setString(3, itemType);
            pstmt.setString(4, category);
            pstmt.setString(5, sizePrice);
            pstmt.setString(6, customizations);

            pstmt.executeUpdate();
            System.out.println("Item "+ itemCode +":"+ name +" added to the menu successfully");
        }catch(SQLException e){
            System.out.println("Error adding menu item" + e.getMessage());
        }
    }

    public static List<Item> sqlLoadMenuItems(){
        List<Item> items = new ArrayList<Item>();
        String item = "SELECT * FROM menu";
        try(Connection sqlconnection = SQLConnection.getConnection(); Statement stmt = sqlconnection.createStatement(); ResultSet result = stmt.executeQuery(item)){
            while(result.next()){
                items.add(new ProductToSell(result.getString("itemCode"),
                        result.getString("Name"),
                        result.getString("ItemType"),
                        result.getString("Category"),
                        result.getString("SizePrice"),
                        result.getString("Customizations")));
            }
        }catch(SQLException e){
            System.out.println("Error loading menu item" + e.getMessage());
        }
        return items;
    }



    public static void sqlModifyMenuItem(String itemCode, String sizePrice, String customizations) {
        String modifyItem = "UPDATE menu SET SizePrice = ?, Customizations = ? WHERE ItemCode = ?";

        try(Connection connection = SQLConnection.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(modifyItem)){

            pstmt.setString(1, sizePrice);
            pstmt.setString(2, customizations);
            pstmt.setString(3, itemCode);

            int itemsAffected = pstmt.executeUpdate();
            if(itemsAffected > 0){
                System.out.println("Item modified successfully");
            }else{
                System.out.println("Item with itemCode " + itemCode + " was not modified");
            }

        }catch(SQLException e){
            System.out.println("Error modifying menu item" + e.getMessage());
        }
    }

    public static void sqlDeleteMenuItem(String itemCode) {
        String deleteItem = "DELETE FROM menu WHERE ItemCode = ?";

        try(Connection connection = SQLConnection.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(deleteItem)){

            pstmt.setString(1, itemCode);
            int itemsAffected = pstmt.executeUpdate();
            if(itemsAffected > 0){
                System.out.println("Item deleted successfully");
            }else{
                System.out.println("Item not found");
            }
        }catch(SQLException e){
            System.out.println("Error deleting menu item" + e.getMessage());
        }
    }

    public static List<Item> sqlFindMenuItemsByCategory(String category) {
        List<Item> items = new ArrayList<>();
        String findItem = "SELECT * FROM menu WHERE Category = ? COLLATE NOCASE";

        try(Connection connection = SQLConnection.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(findItem)){

            pstmt.setString(1, category);
            ResultSet result = pstmt.executeQuery();
            while(result.next()){
                items.add(new ProductToSell(
                        result.getString("itemCode"),
                        result.getString("Name"),
                        result.getString("ItemType"),
                        result.getString("Category"),
                        result.getString("SizePrice"),
                        result.getString("Customizations")));
            }
        }catch(SQLException e){
            System.out.println("Error finding menu item" + e.getMessage());
        }

        return items;
    }

    public static List<Item> sqlFindMenuItemsByItemType(String itemType){
        List<Item> items = new ArrayList<>();
        String findItem = "SELECT * FROM menu WHERE ItemType = ? COLLATE NOCASE";

        try(Connection connection = SQLConnection.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(findItem)){

            pstmt.setString(1, itemType);
            ResultSet result = pstmt.executeQuery();
            while(result.next()){
                items.add(new ProductToSell(
                        result.getString("itemCode"),
                        result.getString("Name"),
                        result.getString("ItemType"),
                        result.getString("Category"),
                        result.getString("SizePrice"),
                        result.getString("Customizations")));
            }
        }catch(SQLException e){
            System.out.println("Error finding menu item" + e.getMessage());
        }

        return items;
    }
}
