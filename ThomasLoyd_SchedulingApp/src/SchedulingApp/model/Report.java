package SchedulingApp.model;

/**
 *
 * @author Thomas Loyd
 */
public class Report {
    // Default values for the Report class
    private int id = 0;
    private String name = "";
    private String content = "";
    
    // A constructor to create a Report object
    public Report(int id, String name, String content){
        this.id = id;
        this.name = name;
        this.content = content;
    }
    
    // Getter and setter  methods for id
    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
    
    // Getter and setter methods for name
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    
    // Getter and setter methods for content
    public String getContent(){
        return content;
    }
    public void setContent(String content){
        this.content = content;
    }
}
