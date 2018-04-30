package SchedulingApp.view_controller;

import SchedulingApp.SchedulingApp;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 *
 * @author Thomas Loyd
 */

public class AddCustomerController {
    // Creates buttons to save entered data or close the window
    @FXML
    private Button save, cancel;
    
    // Creates TextFields to provide user input
    @FXML
    private TextField nameField, addressField, addressTwoField,
                      cityField, zipField, phoneField;
    
    // Creates a ComboBox and HashMap to select a country
    @FXML ComboBox<String> countrySelector = new ComboBox();
    HashMap<String, Integer> countryMap = new HashMap();
    
    // Creates RadioButtons to set whether the customer is active or inactive
    @FXML 
    private RadioButton active, inactive;
    
    // Creates a boolean flag to determine if data is being modified or entered
    private static boolean mod = false;
    // Creates an id integer to determine which customer is being modified
    private static int id = 0;
            
    /* Creates a BiFunction lambda to handle errors, with one input being the body text 
       and the other input being the title text, returning an alert to the calling method */
    private static final BiFunction<String, String, Alert> ERROR = (s1, s2) -> {
        Alert a = new Alert(AlertType.ERROR);
        a.initStyle(StageStyle.UTILITY);
        a.setContentText(s1);
        a.setTitle(s2);
        return a;
    };
    
    // Creates a Predicate lambda to test if values are empty
    private static final Predicate<String> EMPTY = (s) -> {
        boolean flag = false;
        
        if(s == null || s.trim().isEmpty())
            flag = true;
        
        return flag;
    };
    
    // Sets mod to true if data is being modified and assigns id the value of the customerId to be modified
    public void setMod(int i){
        mod = true;
        id = i;
    }
    
    // Puts data into the TextFields if modifying customer data instead of adding it
    private final Consumer<ArrayList> MODIFY = (l) -> {
        nameField.setText(l.get(0).toString());
        addressField.setText(l.get(1).toString());
        addressTwoField.setText(l.get(2).toString());
        countrySelector.getSelectionModel().select(l.get(3).toString());
        countrySelector.setDisable(true);
        cityField.setText(l.get(4).toString());
        zipField.setText(l.get(5).toString());
        phoneField.setText(l.get(6).toString());
        // Sets the selected RadioButton depending on if the user is active or inactive
        if(l.get(7).equals(1))
            active.setSelected(true);
        else
            inactive.setSelected(true);
    };
    
    // Sets the RadioButtons so that only one can be active and adds items to the country ComboBox
    public void initialize(){
        // Sets the default RadioButton to active
        active.setSelected(true);
        // Adds the countries in the database to the ComboBox
        try(Statement s = SchedulingApp.getConn().createStatement()){
            ObservableList<String> country = FXCollections.observableArrayList();
            ResultSet rs = s.executeQuery("SELECT country, countryId from country WHERE countryId > 0;");
                
            while(rs.next()){
                country.add(rs.getString("country"));
                countryMap.put(rs.getString("country"), rs.getInt("countryId"));
            }
            rs.close();
            s.close();
            SchedulingApp.getConn().close();
            countrySelector.getItems().addAll(country);
        }catch(SQLException | ClassNotFoundException e){e.printStackTrace();}
        
        // Determines if the user is modifying a customer record or not
        if(mod){
            // Collects the current customer information and sends it to the modify lambda
            MODIFY.accept(MainScreenController.getCustomerData(id));
        }
    }
    
    // Executes when the save button is pressed
    @FXML
    private void onSave(){
        // Determines if the entered information can be safely entered into the database
        if(verify()){
            try{
                // Creates a statement to add to the database and to get data from the database
                Statement cu = SchedulingApp.getConn().createStatement();
                Statement ci = SchedulingApp.getConn().createStatement();
                Statement a = SchedulingApp.getConn().createStatement();
                
                // Creates ints to hold the necessary ID values to enter a customer into the database
                int cityId = 0, addressId = 0;
                int countryId = countryMap.get(countrySelector.getSelectionModel().getSelectedItem());
               
                // Boolean flags to determine if data was added to the database
                boolean newAddress = false, newCity = false, newCustomer = false;
                
                /* Creates String objects to hold entered information and make all
                   input uppercase to allow for easier tracking */
                String name = nameField.getText().toUpperCase(), address = addressField.getText().toUpperCase(),
                       address2 = addressTwoField.getText().toUpperCase(), country = countrySelector.getSelectionModel().getSelectedItem(),
                       city = cityField.getText().toUpperCase(), postalCode = zipField.getText().toUpperCase(),
                       phone = phoneField.getText().toUpperCase(), user = SchedulingApp.getUser().toUpperCase();
                
                // Sets the customer to active unless inactive is selected
                int active = 1;
                if(inactive.isSelected())
                    active = 0;
                
                // Tests whether the city is already present in the database
                ResultSet cities = ci.executeQuery("SELECT cityId, countryId FROM city WHERE city = '" + city + "' AND countryId = " + 
                                                   countryId + ";");
                
                // Tests if any cities in the database match the entered city
                if(!cities.next())
                    newCity = true;
                // Executes if the city already exits in the database and collects the cityId
                else
                    cityId = cities.getInt("cityId");
                cities.close();
                
                // Tests whether the address is already present in the database
                ResultSet addresses = a.executeQuery("SELECT addressId FROM address WHERE address = '" + 
                                                     address + "' AND address2 = '" + address2 + "'AND cityId >= " + cityId + ";");
                
                if(!addresses.next())
                    newAddress = true;
                else
                    addressId = addresses.getInt("addressId");
                addresses.close();
                
                // Tests whether the customer is already present in the database
                ResultSet customers = cu.executeQuery("SELECT customerId FROM customer WHERE customerName = '" + 
                                                     name + "' AND addressId >= " + addressId + ";");
                
                if(id != 0 || !customers.next())
                    newCustomer = true;
                customers.close();
                
                // Sends an error if all the entered information matches a record in the database
                if(newAddress == false && newCity == false && newCustomer == false)
                    popup("exists", "");
                else{
                    // Runs if a new city was entered
                    if(newCity){
                        // Updates the database with a new city if one was entered
                        ci.executeUpdate("INSERT INTO city (city, countryId, createDate, createdBy, lastUpdate, lastUpdateBy) " +
                                         "VALUES('" + city + "', " + countryId + 
                                         ", UTC_TIMESTAMP(), '" + user + 
                                         "', UTC_TIMESTAMP(), '" + user + "');");
                        ci.close();
                    
                        // Gets the assigned cityId after inserting the new city
                        Statement s = SchedulingApp.getConn().createStatement();
                        ResultSet rs = s.executeQuery("SELECT cityId FROM city WHERE city = '" + city + "';");
                    
                        // Assigns the cityId using the assigned cityId from the database
                        while(rs.next()){
                            cityId = rs.getInt("cityId");
                        }
                        rs.close();
                        s.close();
                    }
                    // Runs if a new address was entered
                    if(newAddress){
                        a.executeUpdate("INSERT INTO address (address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdateBy) " +
                                        "VALUES ('" + address+ "', '" + address2 + "', " + cityId + 
                                        ", '" + postalCode + "', '" + phone + "', UTC_TIMESTAMP(), '" + user + 
                                        "', UTC_TIMESTAMP(), '" + user + "');");
                    
                        Statement s = SchedulingApp.getConn().createStatement();
                        ResultSet rs = s.executeQuery("SELECT addressId FROM address WHERE address = '" + address +
                                           "' AND address2 = '" + address2 + "';");
                    
                        while(rs.next()){
                            addressId = rs.getInt("addressId");
                        }
                        rs.close();
                        s.close();
                    }
                    // Runs if existing customer data was modified
                    if(mod){
                        cu.executeUpdate("UPDATE customer SET customerName = '" + name +
                                         "', addressId = " + addressId +
                                         ", active = " + active + ", lastUpdate = UTC_TIMESTAMP(), lastUpdateBy = '" +
                                         user + "' WHERE customerId = " + id + ";");
                        newCustomer = false;
                        cu.close();
                    }
                    // Runs if a new customer was added
                    if(newCustomer){
                        cu.executeUpdate("INSERT INTO customer (customerName, addressId, active, createDate, createdBy, lastUpdate, lastUpdateBy) " +
                                         "VALUES ('" + name+ "', " + addressId + ", " + active + 
                                         ", UTC_TIMESTAMP(), '" + user + 
                                         "', UTC_TIMESTAMP(), '" + user + "');"); 
                        cu.close();
                    }
                    // Closes the window after inserting the data
                    onCancel();
                }
                SchedulingApp.getConn().close();
            }catch(SQLException | ClassNotFoundException e){SchedulingApp.popup("net"); e.printStackTrace();}
        }
    }
    
    // Closes the application, discarding all data entered
    @FXML
    private void onCancel(){
        mod = false;
        id = 0;
        Stage s = (Stage) cancel.getScene().getWindow();
        s.close();
    }
    
    // Verifies that the entered input is valid for the database
    private boolean verify(){
        // Creates a flag to alert calling methods to valid or invalid input
        boolean flag = false;
        
        // Creates an integer to track the number of errors and a StringBuilder to track what caused the error
        int errorCount = 0;
        StringBuilder s = new StringBuilder("");
        
        // Tests if any of the TextFields are empty or if the countrySelector ComboBox is blank
        if(EMPTY.test(nameField.getText()) && EMPTY.test(addressField.getText()) &&
           EMPTY.test(cityField.getText()) && EMPTY.test(zipField.getText()) && EMPTY.test(phoneField.getText()) &&
           (countrySelector.getValue() == null || countrySelector.getSelectionModel().isEmpty())){
            /* Determines which fields are empty and adds the field names to a string builder
               to alert users to which fields are empty */
            
            if(nameField.getText() == null || nameField.getText().trim().isEmpty())
                s.append("name, ");
            if(addressField.getText() == null || addressField.getText().trim().isEmpty())
                s.append("address, ");
            if(countrySelector.getValue() == null || countrySelector.getSelectionModel().isEmpty())
                s.append("country, ");
            if(cityField.getText() == null || cityField.getText().trim().isEmpty())
                s.append("city, ");
            if(zipField.getText() == null || zipField.getText().trim().isEmpty())
                s.append("postal code, ");
            if(phoneField.getText() == null || phoneField.getText().trim().isEmpty())
                s.append("phone number, ");
            
            // Removes the comma from the last string added
            s.deleteCharAt(s.length() - 2);
            
            // Sends the empty fields to an error handling method
            popup("empty", s.toString());
            
            errorCount++;
        }
        else{
            /* Creates a string builder and adds any field names that are larger
               than the maximum size of the database field */
            s = new StringBuilder("");
            
            if(nameField.getText().length() > 45)
                s.append("name, ");
            if(addressField.getText().length() > 50)
                s.append("address, ");
            if(addressTwoField.getText().length() > 50)
                s.append("address 2, ");
            if(cityField.getText().length() > 50)
                s.append("city, ");
            if(zipField.getText().length() > 10)
                s.append("postal code, ");
            if(phoneField.getText().length() > 20)
                s.append("phone number, ");
            
            // Runs the length error message if a field was added to the StringBuilder
            if(s.length() > 1){
                s.deleteCharAt(s.length() - 2);
                popup("length", s.toString());
                errorCount++;
            }
            
            s = new StringBuilder("");
            
            // Tests if the postal code input is numerical
            try{
                Integer.parseInt(zipField.getText());
            }catch(NumberFormatException e){
                s.append("postal code ");
            }
            // Tests if the phone number input is numerical
            try{
                Long.parseLong(phoneField.getText());
            }catch(NumberFormatException e){
                if(s.length() > 0)
                    // Executes if the postal code error was caught
                    s.append("and ");
                s.append("phone number ");
            }
            
            // Runs if postal code and/or phone number input isn't numerical
            if(s.length() > 0){
                popup("invalid", s.toString());
                errorCount++;
            }
        }
        // Executes if there were no detected errors
        if(errorCount == 0)
            flag = true;
        return flag;
    }
    
    // Handles errors based on a sent error code
    private void popup(String s1, String s2){
        // Runs if the input already exists in the database
        if(s1.equals("exists"))
            ERROR.apply("The entered data already exists in the database.", "Data already exists").showAndWait();
        // Runs if the input is too large for the database
        if(s1.equals("length"))
            ERROR.apply("Please shorten the input in the " + s2 + "field(s).", "Input size error").showAndWait();
        // Runs if mandatory input fields are empty
        if(s1.equals("empty"))
            ERROR.apply("Please enter information into the " + s2 + "field(s).", "Empty Field Error").showAndWait();
        if(s1.equals("invalid"))
            ERROR.apply("Please enter numerical data into the " + s2 + "field(s).", "Wrong Input Error").showAndWait();
    }
}
