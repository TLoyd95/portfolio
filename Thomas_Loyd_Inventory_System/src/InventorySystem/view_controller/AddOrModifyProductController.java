/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InventorySystem.view_controller;

import InventorySystem.InventoryManagementSystem;
import javafx.fxml.*;
import javafx.stage.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.*;
import InventorySystem.model.Product;
import InventorySystem.model.Part;
import javafx.collections.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.Optional;

/**
 * FXML Controller class
 *
 * @author Thomas Loyd
 */
public class AddOrModifyProductController{

    // Creates an InventoryManagementSystem object for management purposes
    private InventoryManagementSystem ims = new InventoryManagementSystem();
    
    // Creates ObservableList objects to populate the TableView objects
    private static ObservableList<Part> associatedParts = 
            FXCollections.observableArrayList();
    private static ObservableList<Part> unassociatedParts = 
            FXCollections.observableArrayList();

    // Creates buttons for the user interface
    @FXML
    private Button add, delete, save, cancel, search;
    
    // Creates labels for identification purposes
    @FXML
    public static Label title;
    @FXML
    private Label id, name, inventory, price, max, min;
    
    // Creates TextFields for input purposes
    @FXML
    private TextField idInput, nameInput, inventoryInput, priceInput, 
                     maxInput, minInput, searchInput;
    
    // Creates TableViews for the parts used by the product
    @FXML
    private TableView<Part> newPartsTable, usedPartsTable;
    
    // Creates the TableColumns for the newPartsTable
    @FXML
    private TableColumn newPartID, newPartName, newPartInventory, newPartPrice;
    
    // Creates the TableColumns for the usedPartsTable
    @FXML
    private TableColumn usedPartID, usedPartName, 
                       usedPartInventory, usedPartPrice;
    
    // Creates a border and anchor pane for organizational purposes
    @FXML
    private BorderPane bp;
    @FXML
    private AnchorPane ap;
    
    // Creates VBox objects for organization
    @FXML
    private VBox tableBox, fieldBox;
    
    // Creates HBox objects for organization
    @FXML
    private HBox header, idBox, nameBox, inventoyBox, 
                 priceBox, minMaxBox, footer;
    
    /* Creates a stage for display, a product to add to the inventory, and a 
       boolean flag to determine if the product was successfully created  */
    private Stage dialogStage;
    private Product product;
    private boolean okClicked = false;
    
    // A blank constructor for utility purposes
    public AddOrModifyProductController(){
    }
    
    /* Upon initialization of the controller, the product is collected from the 
       main screen and if an existing prpduct has been sent, it's input is put into the TextField objects */
    public void initialize(){
        // Collects the product from the main screen if there is one
        product = MainScreenController.product;
        
        if(product.getProductID() != 0){
        // Executes if sent a product that isn't new
            idInput.setText("" + product.getProductID());
            nameInput.setText(product.getName());
            minInput.setText("" + product.getMin());
            maxInput.setText("" + product.getMax());
            inventoryInput.setText("" + product.getInStock());
            priceInput.setText("" + product.getPrice());
            associatedParts.setAll(product.getAssociatedParts());
            ims.getInventory().removeProduct(product.getProductID() - 1);
        }
    }
    
    // Closes the AddOrModifyProduct window
    @FXML
    private void onCancel(){
        if(confirmCancel()){
            if(product.getProductID() != 0)
                ims.getInventory().addProduct(product);
            dialogStage.close();
        }
    }
    
    // Asks the user if they're sure they want to cancel adding a product
    private boolean confirmCancel(){
        // Creates the alert window
        Alert alert = new Alert(AlertType.CONFIRMATION);
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
    
    // Adds a selected part to the associatedPArts list
    @FXML
    private void onAdd(){
        /* Gets the part from the selectedTableView item and adds it to the associatedParts list 
           while removing it from the unassociated parts list */
        Part part = newPartsTable.getSelectionModel().getSelectedItem();
        product.addAssociatedPart(part);
        unassociatedParts.remove(part);
        // Corrects the TableView objects
        associatedParts.setAll(product.getAssociatedParts());
        usedPartsTable.setItems(associatedParts);
        newPartsTable.setItems(unassociatedParts);
    }
    
    // Removes a selected part from the associatedParts list
    @FXML
    private void onDelete(){
        if(confirmDelete()){
            Part part = usedPartsTable.getSelectionModel().getSelectedItem();
            // Looks for the selected part and removes it
            for(int i = 0; i < product.getAssociatedParts().size(); i++){
                if(part == product.lookupAssociatedPart(i))
                    product.removeAssociatedPart(i);
            }
            // Modifies the TableView objects to show the corrected lists
            unassociatedParts.add(part);
            associatedParts.setAll(product.getAssociatedParts());
            newPartsTable.setItems(unassociatedParts);
            usedPartsTable.setItems(associatedParts);
        }
    }
    
    // Asks the user if they're sure they want to remove an associated part
    private boolean confirmDelete(){
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setContentText("Are you sure you would like to remove associated part?");
        
        ButtonType yes = new ButtonType("Yes");
        ButtonType no = new ButtonType("No");
        
        alert.getButtonTypes().setAll(yes, no);
        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == yes;
    }
    
    // Searches the TableView objects for specific parts
    @FXML
    private void onSearch(){
        // Tests if input was provided, if not it defaults to the original TableViews
        if(searchInput.getText().isEmpty() || searchInput.getText() == null || searchInput.getText().trim().equals("")){
            newPartsTable.setItems(unassociatedParts);
            usedPartsTable.setItems(associatedParts);
        }
        // Determines whether the user is looking for an id or a name
        try{
            // Executes if the user is looking for an id
            int search = Integer.parseInt(searchInput.getText());
            
            // Creates lists to hold the searched for parts
            ObservableList<Part> newParts = FXCollections.observableArrayList();
            ObservableList<Part> usedParts = FXCollections.observableArrayList();
            
            // Tests if any parts in unassociatedParts match the search parameters and adds any parts that match
            for(Part p: unassociatedParts){
                if((p.getPartID() + "").contains(searchInput.getText().trim())){
                    newParts.add(p);
                }
            }
            
            // Tests if any parts in associatedParts match the search parameters and adds any parts that match
            for(Part p: associatedParts){
                if((p.getPartID() + "").contains(searchInput.getText().trim())){
                    usedParts.add(p);
                }
            }
            
            // Displays the results of the search
            newPartsTable.setItems(newParts);
            usedPartsTable.setItems(usedParts);
        }catch(NumberFormatException e){
            // Executes if the user is looking for a name
            ObservableList<Part> newParts = FXCollections.observableArrayList();
            ObservableList<Part> usedParts = FXCollections.observableArrayList();
                
            for(Part p: unassociatedParts){
                if((p.getName().toUpperCase() + "").contains(searchInput.getText().trim().toUpperCase())){
                    newParts.add(p);
                }
            }
            
            for(Part p: associatedParts){
                if((p.getName().toUpperCase() + "").contains(searchInput.getText().trim().toUpperCase())){
                    usedParts.add(p);
                }
            }
            
            newPartsTable.setItems(newParts);
            usedPartsTable.setItems(usedParts);
        }
    }
    
    // Saves the created product into the inventory
    @FXML
    private void onSave(){
        if(isValid()){
            // Sets the product's values if the input was validated
            if(product.getProductID() == 0)
                product.setProductID(ims.getInventory().getProductsSize() + 1);
            product.setName(nameInput.getText());
            product.setInStock(Integer.parseInt(inventoryInput.getText()));
            product.setPrice(Double.parseDouble(priceInput.getText()));
            product.setMin(Integer.parseInt(minInput.getText()));
            product.setMax(Integer.parseInt(maxInput.getText()));
            
            // Adds the product to inventory
            ims.getInventory().addProduct(product);
            okClicked = true;
            
            product = new Product();
            
            // Closes the AddOrModifyProducts window
            dialogStage.close();
        }
    }
    
    // Tests if the provided input is valid
    public boolean isValid(){
        try{
            // Tests if the product fields are empty, and warns the user values must be entered if they are empty
            if(nameInput.getText() == null || inventoryInput.getText() == null ||
               priceInput.getText() == null || minInput.getText() == null || 
               maxInput.getText() == null){
                sendAlert("product");
                return false;
            }
            else if(nameInput.getText().isEmpty() || inventoryInput.getText().isEmpty() ||
                    priceInput.getText().isEmpty() || minInput.getText().isEmpty() ||
                    maxInput.getText().isEmpty()){
                sendAlert("product");
                return false;
            }
            else if(nameInput.getText().trim().equals("") || inventoryInput.getText().trim().equals("") ||
                    priceInput.getText().trim().equals("") || minInput.getText().trim().equals("") ||
                    maxInput.getText().trim().equals("")){
                sendAlert("product");
                return false;
            }
            else{
                  // Tests if numerical input was provided for the numerical values, and assigns the values for testing
                  int tempInventory = Integer.parseInt(inventoryInput.getText());
                  double tempPrice = Double.parseDouble(priceInput.getText());
                  int tempMin = Integer.parseInt(minInput.getText());
                  int tempMax = Integer.parseInt(maxInput.getText());
                  
                  // A cost value to determine if the price is higher than the cost of the associated parts
                  int cost = 0;
                  
                  // Tests if the inventory is less than min or greater than max
                  if(tempInventory < tempMin || tempInventory > tempMax){
                      sendAlert("inventory");
                      return false;
                  }
                  
                  // Tests if min is greater than max
                  if(tempMin > tempMax){
                      sendAlert("minmax");
                      return false;
                  }
                  
                  // Tests if associated parts if empty, and if not adds the prices of the associated parts together for the cost
                  if(product.getAssociatedParts().size() == 0){
                      sendAlert("nopart");
                      return false;
                  }
                  else{
                      for(int i = 0; i < product.getAssociatedParts().size(); i++){
                        cost += product.lookupAssociatedPart(i).getPrice();
                      }
                  }
                  // Tests if the price is higher than the cost
                  if(tempPrice < cost){
                      sendAlert("price");
                      return false;
                  }
                  // Returns true if all the values check out
                  return true;
            }
        }catch(NumberFormatException e){
            // Provides an alert if the program was provided invalid input
            sendAlert("error");
            return false;
        }
    }
    
    // Handles error alerts
    private void sendAlert(String error){
        Alert alert = new Alert(AlertType.ERROR);
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
        // Error for no associated parts
        else if(error.equals("nopart")){
            alert.setTitle("Product Error");
            alert.setHeaderText("Error with product's parts");
            alert.setContentText("Product must have at least one part assigned to it");
        }
        // Error for cost being greater than price
        else if(error.equals("price")){
            alert.setTitle("Product Error");
            alert.setHeaderText("Error with product's price");
            alert.setContentText("Price of the product must be greater than the\n" + 
                                 "cost of the parts making it up");
        }
        // Error for empty values
        else if(error.equals("product")){
            alert.setTitle("Inventory Error");
            alert.setHeaderText("Error with product");
            alert.setContentText("Product must have a name, price, and inventory");
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
    
    // Sets the dialog stage
    public void setDialogStage(Stage dialogStage){
        this.dialogStage = dialogStage;
    }
    
    // Sends the okClicked variable
    public boolean isOkClicked(){
        return okClicked;
    }
    
    // Sets the InventoryManagementSystem class to manage the results
    public void setMainApp(InventoryManagementSystem ims){
        this.ims = ims;
        
        // Fills the TableViews
        if(product.getProductID() == 0){
            for(int i = 0; i < ims.getInventory().getPartsSize(); i++){
                unassociatedParts.add(ims.getInventory().lookupPart(i));
            }
        }
        associatedParts.setAll(product.getAssociatedParts());
        
        // Tests if modifying a product and removes the associated parts from the unassociated parts
        if(associatedParts.size() != 0){
            for(int i = 0; i < unassociatedParts.size(); i++){
                for(Part q: associatedParts){
                    if(q.getPartID() == unassociatedParts.get(i).getPartID())
                        unassociatedParts.remove(i);
                }
            }
        }
        
        // Displays the TableViews
        newPartsTable.setItems(unassociatedParts);
        usedPartsTable.setItems(associatedParts);
        
        // Lets the columns know what data to access
        newPartID.setCellValueFactory(new PropertyValueFactory<>("partID"));
        newPartInventory.setCellValueFactory(new PropertyValueFactory<>("inStock"));
        newPartName.setCellValueFactory(new PropertyValueFactory<>("name"));
        newPartPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        
        usedPartID.setCellValueFactory(new PropertyValueFactory<>("partID"));
        usedPartInventory.setCellValueFactory(new PropertyValueFactory<>("inStock"));
        usedPartName.setCellValueFactory(new PropertyValueFactory<>("name"));
        usedPartPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
    }
}
