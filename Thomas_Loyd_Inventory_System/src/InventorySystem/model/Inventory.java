/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InventorySystem.model;

import java.util.*;
/**
 *
 * @author Thomas Loyd
 */
public class Inventory {
    // Declares the array lists for the Inventory class
    private static ArrayList<Product> products = new ArrayList<>();
    private static ArrayList<Part> parts = new ArrayList<>();
    
    public int getProductsSize(){
        return products.size();
    }
    
    public int getPartsSize(){
        return parts.size();
    }
    
    // Adds Product objects to the products array list
    public void addProduct(Product product){
        products.add(product);
        Collections.sort(products);
    }
    // Returns true if Product object is successfully removed from products, flase if unsuccessfull
    public boolean removeProduct(int i){
        products.remove(i);
        Collections.sort(products);
        for (int j = 0; j < products.size(); j++) {
            updatePart(j);
        }
        return true;
    }
    // Returns the Product at index i if index i is in bounds, otherwise returns null
    public Product lookupProduct(int i){
            return products.get(i);
    }
    // Retrieves the Product at index i to be modifed and then replaces the original Product with the new one
    public void updateProduct(int i){
        Product p = lookupProduct(i);
        p.setProductID(i + 1);
        products.set(i, p);
    }
    
    // Adds Part objects to the parts array list
    public void addPart(Part part){
        parts.add(part);
        Collections.sort(parts);
    }
    // Returns true if Part object is successfully removed from parts, flase if unsuccessfull
    public boolean removePart(int i){
        parts.remove(i);
        Collections.sort(parts);
        for (int j = 0; j < parts.size(); j++) {
            updatePart(j);
        }
        return true;
    }
    // Returns the Part at index i if index i is in bounds, otherwise returns null
    public Part lookupPart(int i){
            return parts.get(i);
    }
    // Retrieves the Part at index i to be modifed and then replaces the original Part with the new one
    public void updatePart(int i){
        Part p = lookupPart(i);
        p.setPartID(i + 1);
        parts.set(i, p);
    }
}
