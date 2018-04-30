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
public class Product implements Comparable<Product>{
    // Declares the variables for the Product class
    private ArrayList<Part> associatedParts = new ArrayList<>();
    private int productID;
    private String name;
    private double price;
    private int inStock;
    private int min;
    private int max;
    
    @Override
    public int compareTo(Product product){
        int compare = (product.getProductID());
        
        return this.productID - compare;
    }
    
    public ArrayList<Part> getAssociatedParts(){
        return associatedParts;
    }
    
    // Declare getter and setter methods for the name variable
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }
    
    // Declare getter and setter methods for the price variable
    public void setPrice(double price){
        this.price = price;
    }
    public double getPrice(){
        return price;
    }
    
    // Declare getter and setter methods for the inStock variable
    public void setInStock(int inStock){
        this.inStock = inStock;
    }
    public int getInStock(){
        return inStock;
    }
    
    // Declare getter and setter methods for the min variable
    public void setMin(int min){
        this.min = min;
    }
    public int getMin(){
        return min;
    }
    
    // Declare getter and setter methods for the max variable
    public void setMax(int max){
        this.max = max;
    }
    public int getMax(){
        return max;
    }
    
    // Adds Part objects to the associatedPArts array list
    public void addAssociatedPart(Part part){
        associatedParts.add(part);
    }
    // Returns true if Part object is successfully removed from associatedParts, flase if unsuccessfull
    public boolean removeAssociatedPart(int i){
        if(i < 0 || i >= associatedParts.size())
            return false;
        else {
            associatedParts.remove(i);
            return true;
        }
    }
    // Returns the Part at index i if index i is in bounds, otherwise returns null
    public Part lookupAssociatedPart(int i){
        if(i < 0 || i >= associatedParts.size())
            return null;
        else
            return associatedParts.get(i);
    }
    
    // Declare getter and setter methods for the productID variable
    public void setProductID(int productID){
        this.productID = productID;
    }
    public int getProductID(){
        return productID;
    }
}
