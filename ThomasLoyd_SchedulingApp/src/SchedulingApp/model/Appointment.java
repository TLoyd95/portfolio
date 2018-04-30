package SchedulingApp.model;

/**
 *
 * @author Thomas Loyd
 */
public class Appointment {
    // Default values for the Appointment class
    private int appointmentId = 0;
    private String customerName = "";
    private String type = "";
    private String start = "";
    private String end = "";
    private String contact = "";
    
    // Getter and setter methods for appointmentId
    public int getAppointmentId(){
        return appointmentId;
    }
    public void setAppointmentId(int appointmentId){
        this.appointmentId = appointmentId;
    }
    
    // Getter and setter methods for customerName
    public String getCustomerName(){
        return customerName;
    }
    public void setCustomerName(String customerName){
        this.customerName = customerName;
    }
    
    // Getter and setter methods for type
    public String getType(){
        return type;
    }
    public void setType(String type){
        this.type = type;
    }
    
    // Getter and setter methods for start
    public String getStart(){
        return start;
    }
    public void setStart(String start){
        this.start = start;
    }
    
    // Getter and setter methods for end
    public String getEnd(){
        return end;
    }
    public void setEnd(String end){
        this.end = end;
    }
    
    // Getter and setter methods for contact
    public String getContact(){
        return contact;
    }
    public void setContact(String contact){
        this.contact = contact;
    }
}
