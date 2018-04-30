package SchedulingApp.model;

/**
 *
 * @author Thomas Loyd
 */
public class Customer {
    // Default values for the Customer class
    private int customerId = 0;
    private String customerName = "";
    private String address = "";
    private String phone = "";
    
    // Getter and setter methods for customerId
    public int getCustomerId(){
        return customerId;
    }
    public void setCustomerId(int customerId){
        this.customerId = customerId;
    }
    
    // Getter and setter methods for customerName
    public String getCustomerName(){
        return customerName;
    }
    public void setCustomerName(String customerName){
        this.customerName = customerName;
    }
    
    // Getter and setter methods for address
    public String getAddress(){
        return address;
    }
    public void setAddress(String address){
        this.address = address;
    }
    
    // Getter and setter methods for phone
    public String getPhone(){
        return phone;
    }
    public void setPhone(String phone){
        this.phone = phone;
    }
}
