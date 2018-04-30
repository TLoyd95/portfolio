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
public class OutsourcedPart extends Part{
    public String companyName;
    
    // Allows OutsourcedPart objects to be sorted
    @Override
    public int compareTo(Part part){
        int compare = ((Part)part).getPartID();
        return this.partID - compare;
    }
    
    // Overrides the abstract getter and setter methods for the name variable in the Part class
    @Override
    public void setName(String name){
        this.name = name;
    }
    @Override
    public String getName(){
        return name;
    }
    
    // Overrides the abstract getter and setter methods for the price variable in the Part class
    @Override
    public void setPrice(double price){
        this.price = price;
    }
    @Override
    public double getPrice(){
        return price;
    }
    
    // Overrides the abstract getter and setter methods for the inStock variable in the Part class
    @Override
    public void setInStock(int inStock){
        this.inStock = inStock;
    }
    @Override
    public int getInStock(){
        return inStock;
    }
    
    // Overrides the abstract getter and setter methods for the min variable in the Part class
    @Override
    public void setMin(int min){
        this.min = min;
    }
    @Override
    public int getMin(){
        return min;
    }
    
    // Overrides the abstract getter and setter methods for the max variable in the Part class
    @Override
    public void setMax(int max){
        this.max = max;
    }
    @Override
    public int getMax(){
        return max;
    }
    
    // Overrides the abstract getter and setter methods for the partID variable in the Part class
    @Override
    public void setPartID(int partID){
        this.partID = partID;
    }
    @Override
    public int getPartID(){
        return partID;
    }
    
    // Declares getter and setter methods for the companyName variable
    public void setCompanyName(String companyName){
        this.companyName = companyName;
    }
    public String getCompanyName(){
        return companyName;
    }
}
