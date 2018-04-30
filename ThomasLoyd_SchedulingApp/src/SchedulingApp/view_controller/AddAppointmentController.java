package SchedulingApp.view_controller;

import SchedulingApp.SchedulingApp;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.HashMap;

/**
 *
 * @author tommy
 */
public class AddAppointmentController{
    
    // Creates save and cancel Buttons
    @FXML
    private Button save, cancel;
    
    // Creates location and url TextFields
    @FXML
    private TextField locationField;
    @FXML
    private TextField urlField;
    
    // Creates a description TextArea
    @FXML  private TextArea description;
    
    // Creates a date DatePicker
    @FXML private DatePicker date;
    
    // Creates type, customer, contact, start, and end String ComboBoxes
    @FXML private ComboBox<String> type, customer, contact, start, end;
    
    // Creates a HashMap to hold customerIds using the customer names as keys
    private HashMap<String, Integer> customerMap = new HashMap();
    
    // Creates a boolean to determine if the appointment is being modified
    private static boolean mod = false;
    // Creates an int to hold the appointmentId if an appointment is being modified
    private static int id = 0;
    
    // Sets mod to true and sets id equal to the appointmentId of the appointment to be modified
    public void setMod(int i){
        mod = true;
        id = i;
    }
    
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
        // Creates a flag to return
        boolean flag = false;
        
        // Sets the flag to true if the entered information is empty
        if(s == null || s.trim().isEmpty())
            flag = true;
        
        // Returns the flag
        return flag;
    };
    
    // Creates a BiPredicate labda to test if the start and end times selected by the user conflict with another appointment
    private static final BiPredicate<ZonedDateTime, ZonedDateTime> OVERLAP = (st, et) -> {
        boolean flag = false;
        
        // Retrives appointments from the database that belong to the current user
        try(Statement s = SchedulingApp.getConn().createStatement()){
            ResultSet rs = s.executeQuery("SELECT * FROM appointment WHERE createdBy = '" + SchedulingApp.getUser() + "';");
            
            // Runs while there is information in the ResultSet
            while(rs.next()){
                // Skips the current iteration of the while loop if rs.next() returns the appointment being modified
                if(mod && rs.getInt("appointmentId") == id)
                    continue;
                
                // Sets the flag to true if the entered data conflicts with the data currently in the database
                if(st.toInstant().equals(rs.getTimestamp("start").toInstant()) || et.toInstant().equals(rs.getTimestamp("end").toInstant()) ||
                  (st.toInstant().isAfter(rs.getTimestamp("start").toInstant()) && st.toInstant().isBefore(rs.getTimestamp("end").toInstant())) ||
                  (et.toInstant().isAfter(rs.getTimestamp("start").toInstant()) && et.toInstant().isBefore(rs.getTimestamp("end").toInstant())))
                    flag = true;
            }
            
            // Closes the SQL resources after use
            rs.close();
            s.close();
            SchedulingApp.getConn().close();
        }catch(ClassNotFoundException | SQLException e){SchedulingApp.popup("net"); e.printStackTrace();}
        
        return flag;
    };
    
    // Creates an ArrayList Consumer to enter values if the user is modifying an appointment
    private final Consumer<ArrayList> MODIFY = (l) -> {
        // Enters the information from the ArrayList into the input fields
        customer.getSelectionModel().select(l.get(0).toString());
        locationField.setText(l.get(1).toString());
        type.getSelectionModel().select(l.get(2).toString());
        description.setText(l.get(3).toString());
        contact.getSelectionModel().select(l.get(4).toString());
        urlField.setText(l.get(5).toString());
        
        // Retrieves the appointment times
        Timestamp startTimestamp = (Timestamp) l.get(6);
        Timestamp endTimestamp = (Timestamp) l.get(7);
        
        // Converts the appointment times into the local time zone
        ZonedDateTime zStart = startTimestamp.toLocalDateTime().atZone(ZoneId.of("UTC"));
        ZonedDateTime zEnd = endTimestamp.toLocalDateTime().atZone(ZoneId.of("UTC"));
        LocalDateTime localStart = LocalDateTime.ofInstant(zStart.toInstant(), ZoneOffset.systemDefault());
        LocalDateTime localEnd = LocalDateTime.ofInstant(zEnd.toInstant(), ZoneOffset.systemDefault());
        
        /* Formats the appointment times to match the times stored in the start and end ComboBoxes
           and sets the start and end ComboBox values and the date DatePicker value */
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mma");
        start.getSelectionModel().select(localStart.toLocalTime().format(timeFormat).toUpperCase());
        end.getSelectionModel().select(localEnd.toLocalTime().format(timeFormat).toUpperCase());
        date.setValue(localStart.toLocalDate());
    };
    
    // Uses a boolean Supplier lambda expression to verify that the input is valid
    private final Supplier<Boolean> VERIFY = () -> {
        // Creates a flag to return
        boolean flag = false;
        // Creates a StringBuilder to catch empty fields
        StringBuilder error = new StringBuilder("");
        
        // Counts the number of errors present in the input
        int errorCount = 0;
        
        // Tests which fields are empty and adds to the StringBuilder which fields are empty that shouldn't be
        if(EMPTY.test(locationField.getText()))
            error.append("location, ");
        if(contact.getValue() == null || contact.getSelectionModel().isEmpty())
            error.append("contact method, ");
        else if (contact.getSelectionModel().getSelectedItem().equals("SCREEN SHARE") && (EMPTY.test(urlField.getText())))
            error.append("url (Because the contact method is Screen Share), ");
        if(start.getValue() == null || start.getSelectionModel().isEmpty())
            error.append("start time, ");
        if(end.getValue() == null || end.getSelectionModel().isEmpty())
            error.append("end time, ");
        if(date.getValue() == null)
            error.append("date, ");
            
        
        // Tests if any fields were empty
        if(error.length() > 1){
            error.deleteCharAt(error.length() - 2);
            popup("empty", error.toString());
            errorCount++;
        }
        
        // Resets the StringBuilder
        error = new StringBuilder("");
        
        // Tests if the url or location fields are too large for the database
        if(urlField.getText().length() > 255)
            error.append("url ");
        if(locationField.getText().length() > 255)
            error.append("and location");
        
        if(error.length() > 1){
            if(error.length() < 16 && error.length() > 4)
                error.delete(0, 3);
            popup("length", error.toString());
            errorCount++;
        }
        
        // Gets the appointment start and end times into a UTC timezone
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mma");
        LocalDateTime startTime = LocalDateTime.parse(date.getValue() + " " + start.getSelectionModel().getSelectedItem(), dtf);
        LocalDateTime endTime = LocalDateTime.parse(date.getValue() + " " + end.getSelectionModel().getSelectedItem(), dtf);
        
        ZonedDateTime utcStart =  startTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime utcEnd = endTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
        
        // Tests if the start time is later than or equal to the end time
        if(utcStart.isAfter(utcEnd) || utcStart.equals(utcEnd)){
            popup("time", "");
            errorCount++;
        }
        
        // Determines if the slected times overlap an existing appointment
        if(OVERLAP.test(utcStart, utcEnd)){
            popup("overlap", "");
            errorCount++;
        }
        
        // Determines if the entered date has already passed
        if(utcStart.toLocalDateTime().isBefore(LocalDateTime.now())){
            popup("before", "");
            errorCount++;
        }
        
        // Tests if the selected date is a weekend or not
        if(date.getValue().getDayOfWeek().equals(DayOfWeek.SATURDAY) || date.getValue().getDayOfWeek().equals(DayOfWeek.SUNDAY)){
            popup("weekend", "");
            errorCount++;
        }
        
        // Executes if no errors were detected
        if(errorCount == 0)
            flag = true;
        
        // Returns the flag object
        return flag;
    };
    
    // Runs when the AddAppointmentController class starts
    public void initialize(){
        // Fills the type ComboBox with the available appointment types
        type.getItems().addAll("CONSULTATION", "BUSINESS MEETING", "FINANCING");
        // Fills the contact ComboBox with the contact methods available
        contact.setItems(FXCollections.observableArrayList("PHONE", "IN-PERSON", "VIDEO CONFERENCE", "SCREEN SHARE"));
        // Fills the start and end ComboBoxes with time values representing operational hourse
        start.setItems(FXCollections.observableArrayList("09:00AM", "09:30AM", "10:00AM", "10:30AM", "11:00AM",
                                "11:30AM", "12:00PM", "12:30PM", "01:00PM", "01:30PM",
                                "02:00PM", "02:30PM", "03:00PM", "03:30PM", "04:00PM",
                                "04:30PM", "05:00PM"));
        end.setItems(start.getItems());
        
        // Adds the customers in the database to the ComboBox
        try(Statement s = SchedulingApp.getConn().createStatement()){
            ResultSet rs = s.executeQuery("SELECT customerName, customerId FROM customer WHERE customerId > 0 ORDER BY customerName;");
            ObservableList<String> customers = FXCollections.observableArrayList();
            while(rs.next()){
                customers.add(rs.getString("customerName"));
                customerMap.put(rs.getString("customerName"), rs.getInt("customerId"));
            }
            customer.setItems(customers);
            
            rs.close();
            s.close();
            SchedulingApp.getConn().close();
        }catch(SQLException | ClassNotFoundException e){e.printStackTrace();}
        
        // Determines if the user is modifying an appointment record or not
        if(mod){
            // Collects the current appointment information and sends it to the modify lambda
            MODIFY.accept(MainScreenController.getAppointmentData(id));
        }
    }
    
    // Executes if the save Button is pressed
    @FXML
    private void onSave(){
        // Verifies the input and the inserts the input into the database if the input is validated
        if(VERIFY.get()){
            try(Statement s = SchedulingApp.getConn().createStatement()){
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mma");
                // Gets the appointment time in the local time zone
                LocalDateTime startTime = LocalDateTime.parse(date.getValue() + " " + start.getSelectionModel().getSelectedItem(), dtf);
                LocalDateTime endTime = LocalDateTime.parse(date.getValue() + " " + end.getSelectionModel().getSelectedItem(), dtf);
                
                // Converts the appointment times into UTC Timezone
                ZonedDateTime utcStart =  startTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
                ZonedDateTime utcEnd = endTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
                
                // Enters the data into the database
                if(!mod)
                    s.executeUpdate("INSERT INTO appointment (customerId, title, description, location, contact, url, start, end, createDate, createdBy, lastUpdate, lastUpdateBy) " +
                                    "VALUES(" + customerMap.get(customer.getSelectionModel().getSelectedItem()) + ", '" + type.getSelectionModel().getSelectedItem() +
                                    "', '" + description.getText().toUpperCase() + "', '" + locationField.getText().toUpperCase() + "', '" + contact.getSelectionModel().getSelectedItem() +
                                    "', '" + urlField.getText().toUpperCase() + "', '" + Timestamp.valueOf(utcStart.toLocalDateTime()) + 
                                    "', '" + Timestamp.valueOf(utcEnd.toLocalDateTime()) + "', NOW(), '" + SchedulingApp.getUser() + 
                                    "', NOW(), '" + SchedulingApp.getUser() + "');");
                else
                    s.executeUpdate("UPDATE appointment SET customerid = " + customerMap.get(customer.getSelectionModel().getSelectedItem()) + ", title = '" + type.getSelectionModel().getSelectedItem() +
                                    "', description = '" + description.getText().toUpperCase() + "', location = '" + locationField.getText().toUpperCase() + "', contact = '" + contact.getSelectionModel().getSelectedItem() +
                                    "', url = '" + urlField.getText().toUpperCase() + "', start = '" + Timestamp.valueOf(utcStart.toLocalDateTime()) + 
                                    "', end = '" + Timestamp.valueOf(utcEnd.toLocalDateTime())  + "', lastUpdate = NOW(), lastUpdateBy = '" + SchedulingApp.getUser() + "' " +
                                    "WHERE appointmentId = " + id + ";");
                
                s.close();
                SchedulingApp.getConn().close();
            }catch(ClassNotFoundException | SQLException e){e.printStackTrace();}
            
            onCancel();
        }
    }
    
    // Closes the window, discarding all information in the process
    @FXML
    private void onCancel(){
        mod = false;
        id = 0;
        Stage s = (Stage) cancel.getScene().getWindow();
        s.close();
    }
    
    private void popup(String s1, String s2){
        if(s1.equals("overlap"))
            ERROR.apply("The entered appointment time overlaps with a pre-existing appointment.", "Appointment Overlap").showAndWait();
        if(s1.equals("time"))
            ERROR.apply("The start time of the appointment must be set before the end time of the appointment.", "Scheduling Error").showAndWait();
        if(s1.equals("length"))
            ERROR.apply("Please shorten the input in the " + s2 + "field(s).", "Input Size Error").showAndWait();
        if(s1.equals("empty"))
            ERROR.apply("Please enter information into the " + s2 + "field(s).", "Empty Field Error").showAndWait();
        if(s1.equals("before"))
            ERROR.apply("The entered date and time has already passed. Please enter a new date and time.", "Time has Passed Error").showAndWait();
        if(s1.equals("weekend"))
            ERROR.apply("The entered date is outside the company's working hours, \nMonday-Friday 9am to 5pm.", "Appointment Outside Business Hours").showAndWait();
    }
}
