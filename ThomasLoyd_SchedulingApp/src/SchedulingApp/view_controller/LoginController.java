package SchedulingApp.view_controller;

import SchedulingApp.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import java.io.*;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.HashMap;

/**
 *
 * @author Thomas Loyd
 */
public class LoginController{
    // Sets the current Locale equal to the Locale created by the main application
    private Locale l = SchedulingApp.getLocale();
    
    // Creates login and exit Buttons
    @FXML
    private Button login, exit;
    
    // Creates username and password user input TextFields
    @FXML
    private TextField userField, passField;
    
    // Exits the application
    @FXML
    private void onExit() {
        Platform.exit();
    }
    
    // Executes when the login Button is pressed
    @FXML
    private void onLogin() throws ClassNotFoundException{
        // Tests if both the user and password fields are empty
        if((userField.getText() == null || userField.getText().trim().isEmpty()) &&
           (passField.getText() == null || passField.getText().trim().isEmpty())){
            // Sends the error code to the error handling method
            SchedulingApp.popup("both");
            // Exits the method by returning void
            return ;
        }
        // Tests if either the user field or password field are empty and responds accordingly
        if(userField.getText() == null || userField.getText().trim().isEmpty()){
            SchedulingApp.popup("user");
            return ;
        }
        if(passField.getText() == null || passField.getText().trim().isEmpty()){
            SchedulingApp.popup("password");
            return ;
        }
        
        // Creates a default boolean value to prevent unauthorized entry
        boolean flag = false;
        
        // Creates a HashMap to hold user names and passwords
        HashMap<String, String> users = new HashMap();
        
        // Collects the user names and passwords from the database
        try{
            // Creates a Statement object to retrieve information from the database
            Statement getUsers = SchedulingApp.getConn().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, 
                                                      ResultSet.CLOSE_CURSORS_AT_COMMIT);
            // Creates A ResultSet containing all of the usernames and passwords in the database
            ResultSet rs = getUsers.executeQuery("Select userName, password FROM user");
            
            // Stores the usernames and passwords into the users HashMap
            while(rs.next()){
                users.put(rs.getString("userName"), rs.getString("password"));
            }
            
            // Closes the SQL resources after they've been used
            rs.close();
            getUsers.close();
            SchedulingApp.getConn().close();
        }catch(SQLException sqle){SchedulingApp.popup("net"); sqle.printStackTrace();};
        
        // Gets the entered username and password
        String username = userField.getText();
        String password = passField.getText();
        
        // Tests if the provided username and password match any users
        if(users.containsKey(username))
            if(users.get(username).equals(password))
                flag = true;
        
        // Uses a try-catch statement to handle if the user entered the correct login information
        try{
            if(users.containsKey(username)){
                // Runs if the username and password provided match a username and password combination stored in the database
                if(users.get(username).equals(password))
                    success();
                else
                    throw new Exception();
            }
            else
                throw new Exception();
        }catch(Exception e){SchedulingApp.popup("log");}
    }
    
    // Runs if the login attempt was successful
    private void success() throws ClassNotFoundException{
        // Sets the current user and lets the user know they've successfully signed in
        SchedulingApp.setUser(userField.getText());
        SchedulingApp.popup("success");
        
        // Creates a log of the login
        try{
            // Creates a File object for the log file
            File dir = new File("log");
            if(!dir.exists())
                dir.mkdir();
            File log = new File(dir, "log.txt");
            
            // Creates an ArrayList to hold the existing logs
            ArrayList<String> logs = new ArrayList();
            
            // Creates the log file if it doesn't exists
            if(!log.exists())
                log.createNewFile();
            else{
                // Retrieves information from the log file if it does exists
                FileReader fr = new FileReader(log);
                BufferedReader br = new BufferedReader(fr);
                String line = "";
                while((line = br.readLine()) != null){
                    logs.add(line);
                }
                // Closes the reader objects
                br.close();
                fr.close();
            }
            
            // Stores the current login information into the log file
            logs.add(SchedulingApp.getUser() + " : " + new SimpleDateFormat("yyyy-MM-dd : hh:mm:ss").format(new Date(System.currentTimeMillis())) + "\n");
            
            // Writes the user and timestamp into the log file
            FileWriter fw = new FileWriter(log);
            BufferedWriter bw = new BufferedWriter(fw);
            for(String s: logs){
                bw.write(s);
                bw.newLine();
            }
            // Closes the writer objects
            bw.close();
            fw.close();
        }catch(IOException ioe){ioe.printStackTrace();}
        
        // Loads the login FXML document
        try{
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(SchedulingApp.class.getResource("view_controller/MainScreen.fxml"));
            
                // Sets the controller for the login screen
                MainScreenController controller = new MainScreenController();
            
                // Uses the FXML document to populate originPane
                SchedulingApp.getOrigin().getChildren().setAll((AnchorPane) loader.load());
        }catch(IOException ioe){ioe.printStackTrace();}
    }
}