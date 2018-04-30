/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InventorySystem.model;

/**
 *
 * @author Thomas Loyd
 */
public abstract class Part implements Comparable<Part>{
    // Declare part variables
    protected int partID;
    protected String name;
    protected double price;
    protected int inStock;
    protected int min;
    protected int max;
    
    // Allows Part objects to be sorted
    @Override
    public abstract int compareTo(Part part);
    
    // Declare abstract getter and setter methods for the name variable
    public abstract void setName(String name);
    public abstract String getName();
    
    // Declare abstract getter and setter methods for the price variable
    public abstract void setPrice(double price);
    public abstract double getPrice();
    
    // Declare abstract getter and setter methods for the inStock variable
    public abstract void setInStock(int inStock);
    public abstract int getInStock();
    
    // Declare abstract getter and setter methods for the min variable
    public abstract void setMin(int min);
    public abstract int getMin();
    
    // Declare abstract getter and setter methods for the max variable
    public abstract void setMax(int Max);
    public abstract int getMax();
    
    // Declare abstract getter and setter methods for the partID variable
    public abstract void setPartID(int partID);
    public abstract int getPartID();
}
