package SchedulingApp.view_controller;

import SchedulingApp.SchedulingApp;
import SchedulingApp.model.*;
import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.collections.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.function.BiConsumer;


/**
 *
 * @author Thomas Loyd
 */
public class MainScreenController {
    // Creates button objects
    @FXML
    private Button add, addA,modify, modifyA, delete, details, cancel, logout, aLogout, rLogout, exit, aExit, rExit, next, previous;
    
    // Creates TableView objects to present information to the user
    @FXML private TableView<Customer> customerTable;
    @FXML private TableView<Appointment> appointmentTable;
    @FXML private TableView<Report> reportTable;
    
    // Creates TableColumn objects for the customerTable
    @FXML
    private TableColumn<Customer, Integer> idColumn;
    @FXML
    private TableColumn<Customer, String>nameColumn, addressColumn, phoneColumn;
    
    // Creates TableColumn objects for the reportTable
    @FXML
    private TableColumn<Report, Integer> reportIdColumn;
    @FXML
    private TableColumn<Report, String> reportNameColumn, contentColumn;
    
    // Creates TableColumn objects for the appointmentTable
    @FXML
    private TableColumn<Appointment, String> customerColumn, typeColumn, startColumn, endColumn, contactColumn;

    // Creates RadioButton objects for navigation
    @FXML
    private RadioButton weekly, monthly, appointmentTypes, consultantSchedules, inactiveCustomers;
    
    // An integer to keep track of the timeframe the user wishes to view for the appointment calendar
    private int time = 0;
   
    /* A BiConsumer lambda expression to handle Alerts generated by the MainScreenController
       taking two strings as input, one for the body text and one for title text. */
    private static final BiConsumer<String, String> ALERT = (s1, s2) -> {
        // Creates the Alert object and sets it's parameters
        Alert a = new Alert(AlertType.CONFIRMATION);
        a.initStyle(StageStyle.UTILITY);
        a.setContentText(s1);
        a.setTitle(s2);
        // Shows the Alert window and waits for it to close before resuming the program
        a.showAndWait();
    };
    
    // Updates the information in all the tables when information is added
    private final Runnable FILL = () ->{
        fillAppointmentTable();
        fillReportTable();
        fillCustomerTable();
    };
    
    // Moves the timeframe of the calendar back a week when previous is pressed
    @FXML
    private void onPrevious(){
        // Modifies the timefram
        time--;
        // Resests the TableView
        fillAppointmentTable();
    }
    
    // Moves the timeframe of the calendar forward a week when next is pressed
    @FXML
    private void onNext(){
        time++;
        fillAppointmentTable();
    }
    
    // Resets the timeframe for the calendar when the RadioButton is changed
    @FXML
    private void onChange(){
        time = 0;
        fillAppointmentTable();
    }
    
    // Provides a reminder if one is needed
    private void reminder(){
        // Runs through the appointments to search for the closest appointment
        for(int i = 0; i < appointmentTable.getItems().size(); i++){
            // Gets the first appointment on the table, the closest appointment yet to occur
            Appointment a = appointmentTable.getItems().get(i);
            // Sets the format for the two Timestamps so that they're easier to compare
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm");
            StringBuilder date = new StringBuilder(a.getStart().substring(0, 10));
            StringBuilder timeA = new StringBuilder(a.getStart().substring(13, 18));
        
            // Gets the current time and the time of next appointment
            LocalDateTime dateTime = LocalDateTime.of(LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd")), 
                                                  LocalTime.parse(timeA, DateTimeFormatter.ofPattern("HH:mm")));
            Timestamp now = new Timestamp(System.currentTimeMillis());
            Timestamp start = Timestamp.valueOf(dateTime);
            // Gets the time until the next appointment
            long l = now.toInstant().until(start.toInstant(), (TemporalUnit) ChronoUnit.MINUTES);
            // Tests if the next appointmen is in less than or equal to 15 minutes away
            if(l <= 15 && l >= 0){
                ALERT.accept("In " + l + " minutes you have a meeting with " + a.getCustomerName() + ".", "Appointment Alert");
                // Breaks out of the loop
                break;
            }
            // Breaks the loop if the time until appointments is longer than 15 minutes
            if(l > 15)
                break;
        }
    }
    
    // Retrieves customer information if customer data is being modified
    public static ArrayList getCustomerData(int i){
        // Creates an ArrayList to hold information
        ArrayList l = new ArrayList();
        
        try{
            // Gets the information about the selected customer from the datbase
            Statement s = SchedulingApp.getConn().createStatement();
            ResultSet rs = s.executeQuery("SELECT CU.customerName, CU.active, A.address, A.address2, A.postalCode, A.phone, CI.city, CI.countryId " +
                                          "FROM customer CU JOIN address A ON CU.addressId = A.addressId " +
                                          "JOIN city CI ON A.cityId = CI.cityId " +
                                          "WHERE customerId = " + i + ";");
                
            while(rs.next()){
                ResultSet rs2 = SchedulingApp.getConn().createStatement().executeQuery("SELECT country FROM country WHERE countryID = " + 
                                                                                       rs.getInt("countryId") + ";");
                
                // Adds the necessary customer information to the ArrayList
                while(rs2.next()){
                    l.add(rs.getString("customerName"));
                    l.add(rs.getString("address"));
                    l.add(rs.getString("address2")); 
                    l.add(rs2.getString("country"));
                    l.add(rs.getString("city")); 
                    l.add(rs.getString("postalCode"));
                    l.add(rs.getString("phone"));
                    l.add(rs.getInt("active"));
                }
            }
            // Closes the SQL resources after they've been used
            rs.close();
            s.close();
            SchedulingApp.getConn().close();
        }catch(ClassNotFoundException | SQLException e){SchedulingApp.popup("net"); e.printStackTrace();}
        
        // Returns the ArrayList containing the customer information
        return l;
    }
    
    // Retrieves appointment information if an appointment is being modified
    public static ArrayList getAppointmentData(int i){
        ArrayList l = new ArrayList();
        
        try(Statement s = SchedulingApp.getConn().createStatement()){
            ResultSet rs = s.executeQuery("SELECT C.customerName, A.location, A.title, A.description, A.contact, A.url, A.start, A.end " +
                                          "FROM customer C JOIN appointment A ON A.customerId = C.customerId " +
                                          "WHERE appointmentId = " + i + ";");
            
            while(rs.next()){
                l.add(rs.getString("customerName"));
                l.add(rs.getString("location"));
                l.add(rs.getString("title"));
                l.add(rs.getString("description"));
                l.add(rs.getString("contact"));
                l.add(rs.getString("url"));
                l.add(rs.getTimestamp("start"));
                l.add(rs.getTimestamp("end"));
            }
            
            rs.close();
            s.close();
            SchedulingApp.getConn().close();
        }catch(ClassNotFoundException | SQLException e){SchedulingApp.popup("net"); e.printStackTrace();}
        
        return l;
    } 
    
    // Initializes the prepared statements and fills the table with data from the database
    public void initialize(){
        // Lets the customerTable know what data to display
        idColumn.setCellValueFactory(new PropertyValueFactory("customerId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory("customerName"));
        addressColumn.setCellValueFactory(new PropertyValueFactory("address"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory("phone"));
        
        // Lets the appointmentTable know what data to display
        customerColumn.setCellValueFactory(new PropertyValueFactory("customerName"));
        typeColumn.setCellValueFactory(new PropertyValueFactory("type"));
        startColumn.setCellValueFactory(new PropertyValueFactory("start"));
        endColumn.setCellValueFactory(new PropertyValueFactory("end"));
        contactColumn.setCellValueFactory(new PropertyValueFactory("contact"));
        
        // Lets the reportTable know what data to display
        reportNameColumn.setCellValueFactory(new PropertyValueFactory("name"));
        reportIdColumn.setCellValueFactory(new PropertyValueFactory("id"));
        contentColumn.setCellValueFactory(new PropertyValueFactory("content"));
        
        // Sets the default selected RadioButtons
        weekly.setSelected(true);
        appointmentTypes.setSelected(true);
        
        // Fills the tables and reminds the user of upcoming appointments
        FILL.run();
        if(appointmentTable.getItems().size() > 0)
         reminder();
    }
    
    // Fills the report table with information based on the selected RadioButton
    @FXML
    private void fillReportTable(){
        // Creates an ObservableList to hold database information
        ObservableList<Report> reports = FXCollections.observableArrayList();
        
        // Runs if the appointmentTypes RadioButton is selected
        if(appointmentTypes.isSelected()){
            // Renames the reportName and content TableColumns to fit the generated report
            reportNameColumn.setText("Appointment Type");
            contentColumn.setText("Appointments this Month");
            
            // Retrieves the needed information from the database
            try(Statement s = SchedulingApp.getConn().createStatement()){
                // Retrives the reports for the next month
                ResultSet rs = s.executeQuery("SELECT title FROM appointment WHERE appointmentId > 0 AND end < (NOW() + INTERVAL 1 MONTH);");
                // Creates counts to determine how many of each report type there are
                int count1 = 0;
                int count2 = 0;
                int count3 = 0;
                // Counts the reports of each type
                while(rs.next()){
                    if(rs.getString("title").equals("CONSULTATION"))
                        count1++;
                    if(rs.getString("title").equals("BUSINESS MEETING"))
                        count2++;
                    if(rs.getString("title").equals("FINANCING"))
                        count3++;
                }
                rs.close();
                s.close();
                SchedulingApp.getConn().close();
                
                // Creates the rows for the reportTable
                Report r1 = new Report(1, "CONSULTATION", "" + count1);
                Report r2 = new Report(2, "BUSINESS MEETING", "" + count2);
                Report r3 = new Report(3, "FINANCING", "" + count3);
                
                // Adds the rows to the table
                reports.addAll(r1, r2, r3);
            }catch(ClassNotFoundException | SQLException e){e.printStackTrace();}
        }
        // Runs if the consultantSchedules RadioButton is selected
        if(consultantSchedules.isSelected()){
            reportNameColumn.setText("Consultant Name");
            contentColumn.setText("Appointment at ");
            
            try(Statement s = SchedulingApp.getConn().createStatement()){
                // Gets the users and their scheduled appointment start times
                ResultSet rs = s.executeQuery("SELECT U.userId, U.userName, A.start FROM user U JOIN appointment A ON UPPER(U.userName) = A.createdBy " +
                                              "WHERE MONTH(A.start) = MONTH(NOW()) AND U.userId > 0;");
                
                while(rs.next()){
                    // Converts the stored UTC start Timestamp into the local timezone before displaying it
                    ZonedDateTime zStart = rs.getTimestamp("start").toLocalDateTime().atZone(ZoneId.of("UTC"));
                    LocalDateTime localStart = LocalDateTime.ofInstant(zStart.toInstant(), ZoneOffset.systemDefault());
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd : hh:mma");
                    
                    Report r = new Report(rs.getInt("userId"), rs.getString("userName"), dtf.format(localStart).toString());
                    
                    reports.add(r);
                }
            }catch(ClassNotFoundException | SQLException e){e.printStackTrace();}
        }
        // Runs if the inactiveCustomers RadioButton is selected
        if(inactiveCustomers.isSelected()){
            reportNameColumn.setText("Customer Name");
            contentColumn.setText("Inactive Since (Year-Month-Day)");
            
            try(Statement s = SchedulingApp.getConn().createStatement()){
                // Collects the inactive customers
                ResultSet rs = s.executeQuery("SELECT customerId, customerName, lastUpdate FROM customer WHERE active = 0;");
                while(rs.next()){
                    // Gets the date the customer was last updated
                    LocalDateTime update = LocalDateTime.ofInstant(rs.getTimestamp("lastUpdate").toInstant(), ZoneId.systemDefault());
                    Report r = new Report(rs.getInt("customerId"), rs.getString("customerName"), 
                                          DateTimeFormatter.ofPattern("yyyy-MM-dd").format(update).toString());
                    
                    reports.add(r);
                }
                rs.close();
                s.close();
                SchedulingApp.getConn().close();
            }catch(ClassNotFoundException | SQLException e){e.printStackTrace();}
        }
        
        // Fills the reportTable with the data entered into the reports ObservableList
        reportTable.setItems(reports);
    }
    
    // Fills the appointmentTable with information from the database
    @FXML
    private void fillAppointmentTable(){
        ObservableList<Appointment> appointmentData = FXCollections.observableArrayList();
        
        try{
            // Creates a statement to retrieve information from the database and a ResultSet to hold the data
            Statement s = SchedulingApp.getConn().createStatement();
            ResultSet appointments = null;
            // Executes if the weekly RadioButton is selected
            if(weekly.isSelected()){
                appointments = s.executeQuery("SELECT A.appointmentId, C.customerName, A.title, A.start, A.end, A.contact " +
                                              "FROM customer C JOIN appointment A ON C.customerId = A.customerId " +
                                              "WHERE WEEK(A.start) = WEEK(NOW() + INTERVAL " + time + " WEEK) AND A.createdBy = '" + SchedulingApp.getUser() + "' ORDER BY A.start;");
            }
            // Executes if the monthly RadioButton is selected
            else if(monthly.isSelected()){
                appointments = s.executeQuery("SELECT A.appointmentId, C.customerName, A.title, A.start, A.end, A.contact " +
                                              "FROM customer C JOIN appointment A ON C.customerId = A.customerId " +
                                              "WHERE MONTH(A.start) = MONTH(NOW() + INTERVAL " + time + " WEEK) AND A.createdBy = '" + SchedulingApp.getUser() + "' ORDER BY A.start;");
            }
            while(appointments.next()){
                Appointment a = new Appointment();
                
                // Converts the stored UTC Timestamp into the local timezone before displaying it
                ZonedDateTime zStart = appointments.getTimestamp("start").toLocalDateTime().atZone(ZoneId.of("UTC"));
                ZonedDateTime zEnd = appointments.getTimestamp("end").toLocalDateTime().atZone(ZoneId.of("UTC"));
                LocalDateTime localStart = LocalDateTime.ofInstant(zStart.toInstant(), ZoneOffset.systemDefault());
                LocalDateTime localEnd = LocalDateTime.ofInstant(zEnd.toInstant(), ZoneOffset.systemDefault());
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd : hh:mma");
                
                // Stores the appointment information into an Appointment object
                a.setAppointmentId(appointments.getInt("appointmentId"));
                a.setCustomerName(appointments.getString("customerName"));
                a.setType(appointments.getString("title"));
                a.setStart(dtf.format(localStart));
                a.setEnd(dtf.format(localEnd));
                a.setContact(appointments.getString("contact"));
                    
                appointmentData.add(a);
            }

            appointments.close();
            s.close();
            SchedulingApp.getConn().close();
        }catch(SQLException | ClassNotFoundException e){e.printStackTrace(); SchedulingApp.popup("net");}
        
        appointmentTable.setItems(appointmentData);
    }
    
    // Fills the customerTable with information from the database
    private void fillCustomerTable(){
        ObservableList<Customer> customerData = FXCollections.observableArrayList();
        
        try{
            Statement s = SchedulingApp.getConn().createStatement();
            ResultSet customers = s.executeQuery("SELECT C.customerId, C.customerName, A.address, A.phone " +
                                                 "FROM customer C JOIN address A ON C.addressId = A.addressId " +
                                                 "WHERE C.customerId > 0 ORDER BY C.customerId;");
            
            while(customers.next()){
                Customer c = new Customer();
                
                c.setCustomerId(customers.getInt("customerId"));
                c.setCustomerName(customers.getString("customerName"));
                c.setAddress(customers.getString("address"));
                c.setPhone(customers.getString("phone"));
                customerData.add(c);
            }
            
            customers.close();
            s.close();
            SchedulingApp.getConn().close();
        }catch(SQLException | ClassNotFoundException e){SchedulingApp.popup("net"); e.printStackTrace();}
        
        customerTable.setItems(customerData);
    }
    
    // Executes when the customerTable's add button is pressed
    @FXML
    private void onAdd(){
        try{
            // Creates a new window for the AddCustomer screen
            AnchorPane root = new AnchorPane();
            Stage stage = new Stage();
            stage.setTitle("Add Customer");
            Scene scene = new Scene(root);
            stage.setScene(scene);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SchedulingApp.class.getResource("view_controller/AddCustomer.fxml"));
            
            // Sets the controller for the login screen
            AddCustomerController controller = new AddCustomerController();
        
            root.getChildren().setAll((AnchorPane) loader.load());
            
            // Displays the AddCustomer screen
            stage.showAndWait();
            FILL.run();
        }catch(IOException e){e.printStackTrace();}
    }
    
    // Executes when the customerTable's modify button is pressed
    @FXML
    private void onModify(){
        // Executes if nothing from the customerTable is selected
        if(customerTable.getSelectionModel().getSelectedItem() == null)
            return ;
        // Executes if something was selected from the customerTable
        try{
            AnchorPane root = new AnchorPane();
            Stage stage = new Stage();
            stage.setTitle("Modify Customer");
            Scene scene = new Scene(root);
            stage.setScene(scene);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SchedulingApp.class.getResource("view_controller/AddCustomer.fxml"));
            
            AddCustomerController controller = new AddCustomerController();
            // Sends the id value of the customer to be modified
            controller.setMod(customerTable.getSelectionModel().getSelectedItem().getCustomerId());
            
            root.getChildren().setAll((AnchorPane) loader.load());
            
            stage.showAndWait();
            
            FILL.run();
        }catch(IOException e){e.printStackTrace();}
    }
    
    // Executes when the customerTable's delete button is pressed
    @FXML
    private void onDelete(){
        try(Statement s = SchedulingApp.getConn().createStatement()){
                // Deletes the selected customer from the database
                s.executeUpdate("DELETE FROM customer WHERE customerId = " + 
                                customerTable.getSelectionModel().getSelectedItem().getCustomerId() + ";");
                
                s.close();
                SchedulingApp.getConn().close();
                
                FILL.run();
        }catch(SQLException | ClassNotFoundException e){e.printStackTrace();}
    }
    
    // Executes when the appointmentTable's add button, addA, is pressed
    @FXML
    private void onAddA(){
        try{
            AnchorPane root = new AnchorPane();
            Stage stage = new Stage();
            stage.setTitle("Add Appointment");
            Scene scene = new Scene(root);
            stage.setScene(scene);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SchedulingApp.class.getResource("view_controller/AddAppointment.fxml"));
            
            AddAppointmentController controller = new AddAppointmentController();
        
            root.getChildren().setAll((AnchorPane) loader.load());
            
            stage.showAndWait();
            
            FILL.run();
        }catch(IOException e){e.printStackTrace();}
    }
    
    // Executes when the appointmentTable's modify button, modifyA, is pressed
    @FXML
    private void onModifyA(){
        if(appointmentTable.getSelectionModel().getSelectedItem() == null)
            return ;
        
        try{
            AnchorPane root = new AnchorPane();
            Stage stage = new Stage();
            stage.setTitle("Modify Appointment");
            Scene scene = new Scene(root);
            stage.setScene(scene);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SchedulingApp.class.getResource("view_controller/AddAppointment.fxml"));
            
            AddAppointmentController controller = new AddAppointmentController();
            controller.setMod(appointmentTable.getSelectionModel().getSelectedItem().getAppointmentId());
            
            root.getChildren().setAll((AnchorPane) loader.load());
            
            stage.showAndWait();
            
            FILL.run();
        }catch(IOException e){e.printStackTrace();}
    }
    
    // Executes when the appointmentTable's details button is pressed
    @FXML
    private void onDetails(){
        // Executes if there is no selected appointment
        if(appointmentTable.getSelectionModel().getSelectedItem() == null)
            return ;
        
        // Creates a string to hold information about the appointment
        String info = "";
        
        try{
            Statement s = SchedulingApp.getConn().createStatement();
            ResultSet rs = s.executeQuery("SELECT C.customerName, A.title, A.description, A.location, A.contact, A.url, A.start, A.end " +
                                          "FROM customer C JOIN appointment A ON C.customerId = A.customerId " +
                                          "WHERE A.appointmentId = " + appointmentTable.getSelectionModel().getSelectedItem().getAppointmentId() + ";");
            
            // Retrieves the information from the ResultSet and puts it into a readable form for the user
            while(rs.next()){
                info = ("Customer Name: " + rs.getString("customerName") +
                        "\nAppointment Type: " + rs.getString("title") +
                        "\nDescription: " + rs.getString("description") +
                        "\nLocation: " + rs.getString("location") +
                        "\nContact Method: " + rs.getString("contact") +
                        "\nURL: " + rs.getString("url") +
                        "\nStart Date/Time: " + rs.getTimestamp("start") +
                        "\nEnd Date/Time: " + rs.getTimestamp("end"));
                
            }
            
            rs.close();
            s.close();
            SchedulingApp.getConn().close();
            
            ALERT.accept(info, "Appointment Details");
        }catch(ClassNotFoundException | SQLException e){e.printStackTrace();}
    }
    
    // Executes when the appointmentTable's delete button, deleteA, is pressed
    @FXML
    private void onCancel(){
        if(appointmentTable.getSelectionModel().getSelectedItem() == null)
            return ;
        
        try{
            Statement s = SchedulingApp.getConn().createStatement();
            s.executeUpdate("DELETE FROM appointment WHERE appointmentId = " + 
                            appointmentTable.getSelectionModel().getSelectedItem().getAppointmentId() + ";");
            
            s.close();
            SchedulingApp.getConn().close();
            
            FILL.run();
        }catch(ClassNotFoundException | SQLException e){e.printStackTrace();}
    }
    
    // Executes when the logout button is pressed
    @FXML
    private void onLogout() throws ClassNotFoundException{
        // Empties the user variable
        SchedulingApp.setUser("");
        // Returns the user to the login screen
        SchedulingApp.showLoginScreen();
    }
    
    // Executes when the exit button is pressed
    @FXML
    private void onExit(){
        // Exits the program completely
        Platform.exit();
    }
}
