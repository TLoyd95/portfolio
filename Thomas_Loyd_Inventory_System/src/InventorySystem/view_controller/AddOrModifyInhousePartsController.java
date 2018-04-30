/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InventorySystem.view_controller;

import InventorySystem.InventoryManagementSystem;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.stage.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import java.io.*;
import InventorySystem.model.InhousePart;
import java.util.Optional;

/**
 * FXML Controller class
 *
 * @author Thomas Loyd
 */
public class AddOrModifyInhousePartsController{
    
    // Creates an InventoryManagementSystem object for management purposes
    private InventoryManagementSystem ims = new InventoryManagementSystem();
    
    // Creates a ToggleGroup for navigation purposes
    @FXML
    final private ToggleGroup toggle = new ToggleGroup();
    
    // Creates the buttons to save changes or cancel
    @FXML
    private Button save, cancel;
    
    // Creates labels for identification purposes
    @FXML
    public static Label title;
    @FXML
    private Label id, name, inventory, price, max, min, machineID;
    
    // Creates TextFields for input
    @FXML
    private TextField idInput, inventoryInput, nameInput, priceInput, 
                      minInput, maxInput, machineIDInput;
    
    // Creates RadioButtons for navigation
    @FXML
    private RadioButton inHouse, outSourced;
    
    // Creates a border and anchor pane for organizational purposes
    @FXML
    private BorderPane bp;
    @FXML
    private AnchorPane ap;
    
    // Creates a VBox object for organization
    @FXML
    private VBox organizer;
    
    // Creates HBox objects for organization
    @FXML
    private HBox header, idBox, nameBox, inventoryBox, priceBox, 
                        minMaxBox, machineIDBox, footer;
    
    /* Creates a stage for display, a part to add to the inventory, and a 
       boolean flag to determine if the part was successfully created  */
    private Stage dialogStage = new Stage();
    private InhousePart part = new InhousePart();
    private boolean okClicked = false;
    
    // An empty constructor for utility purposes
    public AddOrModifyInhousePartsController(){
        
    }
    
    /* Upon initialization of the controller, the part is collected from the 
       main screen and if an existing part has been sent, it's input is put into the TextField objects */
    public void initialize(){
        part = MainScreenController.inPart;
        
        if(part.getPartID() != 0){
        // Executes if sent a part that isn't new
            idInput.setText("" + part.getPartID());
            nameInput.setText(part.getName());
            minInput.setText("" + part.getMin());
            maxInput.setText("" + part.getMax());
            inventoryInput.setText("" + part.getInStock());
            machineIDInput.setText("" + part.getMachineID());
            priceInput.setText("" + part.getPrice());
            ims.getInventory().removePart(part.getPartID() - 1);
        }
    }
    
    // Allows the window to be seperate from the MainScreen
    public void setDialogStage(Stage dialogStage){
        this.dialogStage = dialogStage;
    }
    
    // Returns if the part is successfully saved or not
    public boolean isOkClicked(){
        return okClicked;
    }
    
    // Saves the part to the inventory
    @FXML
    private void onSave(){
        // Tests if the input is valid
        if(isValid()){
            // Sets the part's parameters with the validated input
            if(part.getPartID() == 0)
                part.setPartID(ims.getInventory().getPartsSize() + 1);
            part.setInStock(Integer.parseInt(inventoryInput.getText()));
            part.setMin(Integer.parseInt(minInput.getText()));
            part.setMax(Integer.parseInt(maxInput.getText()));
            part.setMachineID(Integer.parseInt(machineIDInput.getText()));
            part.setPrice(Double.parseDouble(priceInput.getText()));
            part.setName(nameInput.getText());
        
            // Adds the part to the inventory and sets okClicked to true
            ims.getInventory().addPart(part);
            okClicked = true;
            
            // Closes the add part window
            dialogStage.close();
        }
        else
            // Prints in the console window if the input isn't valid
            System.out.println("Input is invalid");
    }
    
    // A method to test if the input is valid or not
    private boolean isValid() throws NullPointerException{
        //  Try-catch statement tests if numerical input is entered correctly
        try{
            // Tests if nothing is entered
            if(inventoryInput.getText() == null || minInput.getText() == null ||
                    maxInput.getText() == null || priceInput.getText() == null ||
                    nameInput.getText() == null || machineIDInput.getText() == null){
                sendAlert("part");
                return false;
            }
            // Tests if the text fields are empty
            else if(inventoryInput.getText().isEmpty() || minInput.getText().isEmpty() ||
               maxInput.getText().isEmpty() || priceInput.getText().isEmpty() ||
               nameInput.getText().isEmpty() || machineIDInput.getText().isEmpty()){
                sendAlert("part");
                return false;
            }
            // Tests if only blank space was entered
            else if(inventoryInput.getText().trim().equals("") || minInput.getText().trim().equals("") ||
                    maxInput.getText().trim().equals("") || priceInput.getText().trim().equals("") ||
                    nameInput.getText().trim().equals("") || machineIDInput.getText().trim().equals("")){
                sendAlert("part");
                return false;
            }
            else{
                // Tests if numerical fields hae numerical input
                int tempInventory = Integer.parseInt(inventoryInput.getText().trim());
                int tempMin = Integer.parseInt(minInput.getText().trim());
                int tempMax = Integer.parseInt(maxInput.getText().trim());
                Double.parseDouble(priceInput.getText().trim());
                Integer.parseInt(machineIDInput.getText().trim());
                
                // Tests if the inventory is less than the minimum or greater than the maximum
                if(tempInventory > tempMax || tempInventory < tempMin){
                    sendAlert("inventory");
                    return false;
                }
                // Tests if the minimum is greater than the maximum
                if(tempMin > tempMax){
                    sendAlert("minmax");
                    return false;
                }
                
                // Returns true if everything checks out
                return true;
            }
        }catch(NumberFormatException nfe){
            // Executes if the input isn't valid
            sendAlert("error");
            return false;
        }
    }

    // Handles error alerts
    private void sendAlert(String error){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        // Error for inventory
        if(error.equals("inventory")){
            alert.setTitle("Inventory Error");
            alert.setHeaderText("Error with inventory");
            alert.setContentText("Inventory must be equal to or greater than min\n" +
                                 "and must be less than or equal to max");
        }
        // Error for minimum and maximum inventory
        else if(error.equals("minmax")){
            alert.setTitle("MinMax Error");
            alert.setHeaderText("Error with min/max");
            alert.setContentText("Min must be less than or equal to max");
        }
        // Error for empty values
        else if(error.equals("part")){
            alert.setTitle("Inventory Error");
            alert.setHeaderText("Error with part");
            alert.setContentText("Part must have a name, price, and inventory");
        }
        // General error
        else{
            alert.setTitle("error");
            alert.setHeaderText("Error with input");
            alert.setContentText("Input is invalid!");
        }
        // Displays the error until the user acknowledges it
        alert.showAndWait();
    }
    
    // Closes the window if the cancel button is clocked
    @FXML
    private void onCancel() {
        if(confirmCancel()){
            if(part.getPartID() != 0)
                ims.getInventory().addPart(part);
            dialogStage.close();
        }
    }
    
    // Asks the user if they're sure they want to cancel adding a part
    private boolean confirmCancel(){
        // Creates the alert window
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setContentText("Are you sure you would like to cancel?");
        
        // Creates yes and no buttons
        ButtonType yes = new ButtonType("Yes");
        ButtonType no = new ButtonType("No");
        
        // Gets the user's response and returns true if yes false if no
        alert.getButtonTypes().setAll(yes, no);
        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == yes;
    }
    
    // Changes the screen to the outsourced Parts screen when the radio button is changed
    @FXML
    private void onRadioButton(){
        try{
            // Creates a loader to manage the fxml document and loads the fxml document
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(InventoryManagementSystem.class.getResource("view_controller/AddOrModifyOutsourcedParts.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Creates a controller for the AddOrModifyInhouseParts screen
            AddOrModifyOutsourcedPartsController controller = (AddOrModifyOutsourcedPartsController)loader.getController();
            
            // Changes the screen to the AddOrModifyInhouseParts screen
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            controller.setDialogStage(dialogStage);
            controller.setMainApp(ims);
        }catch(IOException e){e.printStackTrace();}
    }
    
    // Sets the InventoryManagementSystem class to manage the results
    public void setMainApp(InventoryManagementSystem ims){
        this.ims = ims;
    }
}
