/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InventorySystem.view_controller;

import InventorySystem.model.*;
import InventorySystem.InventoryManagementSystem;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.control.Alert.*;
import javafx.collections.FXCollections;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.Optional;

/**
 * FXML Controller class
 *
 * @author Thomas Loyd
 */
public class MainScreenController {

    // Creates an InventoryManagementSystem object to control information
    private InventoryManagementSystem ims;
    
    // Creates public static parts and products for modification purposes
    public static InhousePart inPart = new InhousePart();
    public static OutsourcedPart outPart = new OutsourcedPart();
    public static Product product = new Product();
    
    // Creates the Button objects for the parts table
    @FXML private Button partAdd, partDelete, partModify, partSearch;
    
    // Creates the Button objects for the products table
    @FXML private Button productAdd, productDelete, productModify, productSearch;
    
    // Creates the exit Button object
    @FXML private Button exit;
    
    // Creates a Label object for navigation purposes
    @FXML private Text title;
    
    // Creates the TextField objects for searching purposes
    @FXML private TextField partField, productField;
    
    // Creates the TableView objects for the user interface
    @FXML private TableView<Part> partTable;
    @FXML private TableView<Product> productTable;
    
    // Creates the columns for partTable
    @FXML private TableColumn<Part, Integer> partID;
    @FXML private TableColumn<Part, String> partName;
    @FXML private TableColumn<Part, Integer> partInventory; 
    @FXML private TableColumn<Part, Double> partPrice;
    
    // Creates the columns for productTable
    @FXML private TableColumn<Product, Integer> productID;
    @FXML private TableColumn<Product, String> productName;
    @FXML private TableColumn<Product, Integer> productInventory; 
    @FXML private TableColumn<Product, Double> productPrice;
    
    // Creates a border and anchor pane for organizational purposes
    @FXML private BorderPane bp;
    @FXML private AnchorPane ap;
    
    // Creates VBox objects for organizational purposes
    @FXML
    private VBox partBox, productBox;
    
    // Creates HBox objects for organizational purposes
    @FXML
    private HBox partButtonBox, partSearchBox, 
                 productButtonBox, productSearchBox;

    // Allows the user to exit the application by pressing the exit button
    @FXML
    private void exitApplication(ActionEvent ae){
        // Determines if the user really wants to exit the application
        if(confirmExit())
            System.exit(0);
    }
    
    // Asks the user if they're sure they want to exit the application
    private boolean confirmExit(){
        // Creates the alert window
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setContentText("Are you sure you want to exit the application?");
        
        // Creates the buttons for navigation
        ButtonType yes = new ButtonType("Yes");
        ButtonType no = new ButtonType("No");
        
        // Displays the window to the user
        alert.getButtonTypes().setAll(yes, no);
        
        // Gets the results of the window
        Optional<ButtonType> result = alert.showAndWait();
        
        // Sends the results to the caling method
        return result.get() == yes;
    }
    
    // Allows the user to open the AddPart dialog by pressing the add button under the part table
    @FXML
    private void onAddPart(){
        // Shows the AddPart dialog
        ims.showAddPartDialog();
        
        // Resets the TableView and Part objects
        partTable.setItems(ims.getParts());
        inPart = new InhousePart();
        outPart = new OutsourcedPart();
    }
    
    
    
    // Opens the ModifyPart dialog and sends the selected part to the necessary controller
    @FXML
    private void onModPart(){
        // Determines whether the Part being sent is an InhousePart or an OutsourcedPart
        if(partTable.getSelectionModel().getSelectedItem() instanceof InhousePart){
            // Casts the retrieved part to an InhousePart
            inPart = (InhousePart) partTable.getSelectionModel().getSelectedItem();
            
            // Displays the ModifyInhousePart dialog
            ims.showModInPartDialog();
            
            // Resets the TableView and the InhousePart
            partTable.setItems(ims.getParts());
            inPart = new InhousePart();
        }
        else if(partTable.getSelectionModel().getSelectedItem() instanceof OutsourcedPart){
            // Casts the part to an OutsourcedPart
            outPart = (OutsourcedPart) partTable.getSelectionModel().getSelectedItem();
            
            ims.showModOutPartDialog();
            
            // Resets the TableView and the OutsourcedPart
            partTable.setItems(ims.getParts());
            outPart = new OutsourcedPart();
        }
        // Executes if a part isn't selected
        else
            System.out.println("Please select a part to modify");
        
        // Resets the Part TableView
        for(int i = 0; i < ims.getInventory().getPartsSize(); i++){
            ims.getParts().setAll(ims.getInventory().lookupPart(i));
        }
    }
    
    // Allows the user to search the part table for a specific part
    @FXML
    private void onSearchPart(){
        // Determines if the search field is empty, and if so reset the table view
        if(partField.getText().isEmpty() || partField.getText() == null || partField.getText().trim().equals(""))
            partTable.setItems(ims.getParts());
        // Executes if the search field isn't empty
        else{
            try{
                // Executes if the search parameters are numerical
                int search = Integer.parseInt(partField.getText());
                
                // Creates a new ObservableList to hold the searched for Parts
                ObservableList<Part> parts = FXCollections.observableArrayList();
                
                // Loops through the Parts list to find Parts that match the search parameters
                for(int i = 0; i < ims.getInventory().getPartsSize(); i++){
                    Part p = ims.getInventory().lookupPart(i);
                    if((p.getPartID() + "").contains(partField.getText().trim())){
                        // Adds found parts to the list
                        parts.add(p);
                    }
                }
                
                // Sets the TableView to use the searched for parts
                partTable.setItems(parts);
            }catch(NumberFormatException e){
                // Executes if the search parameters are alphabetical
                ObservableList<Part> parts = FXCollections.observableArrayList();
                
                // Loops through the Parts list to find Parts that match the search parameters
                for(int i = 0; i < ims.getInventory().getPartsSize(); i++){
                    Part p = ims.getInventory().lookupPart(i);
                    if((p.getPartID() + "").contains(partField.getText().trim())){
                        // Adds found parts to the list
                        parts.add(p);
                    }
                }
                
                partTable.setItems(parts);
            }
        }
    }
    
    // Allows the user to delete selected parts
    @FXML
    private void onDeletePart(){
        if(confirmPartDelete()){
            // Determines if there are parts to delete
            if(ims.getInventory().getPartsSize() > 0){
                // Determines if the part is associated with a product
                if(ims.getInventory().getProductsSize() != 0){
                    if(testPart()){
                        /* Executes if the part is associated with a product.
                           Removes the parts from the inventory and then resets the ObservableList and TableView */
                        ims.getInventory().removePart(partTable.getSelectionModel().getSelectedItem().getPartID() - 1);
                    }
                }
                else{
                    // Executes if the part is not associated with a product.
                    ims.getInventory().removePart(partTable.getSelectionModel().getSelectedItem().getPartID() - 1);
                }
            }
        }
        partTable.setItems(ims.getParts());
    }
    
    // Has the user confirm whether they want to delete a part or not
    private boolean confirmPartDelete(){
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setContentText("Are you sure you want to delete the selected part?");
        
        ButtonType yes = new ButtonType("Yes");
        ButtonType no = new ButtonType("No");
        
        alert.getButtonTypes().setAll(yes, no);
        
        Optional<ButtonType> result = alert.showAndWait();
        
        return result.get() == yes;
    }
    
    private boolean testPart(){
        Part part = partTable.getSelectionModel().getSelectedItem();
        for(int i = 0; i < ims.getInventory().getProductsSize(); i++){
            Product p = ims.getInventory().lookupProduct(i);
            if(p.lookupAssociatedPart(i).getPartID() == part.getPartID()){
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Product Error");
                alert.setHeaderText("Part is associated with a product");
                alert.setContentText("Parts can't be deleted if they're associated with a product");
                alert.showAndWait();
                return false;
            }
        }
        return true;
    }
    
    // Allows the user to search for products
    @FXML
    private void onSearchProduct(){
        // Tests if the search field is empty, and if so reset the TableView
        if(productField.getText().isEmpty() || productField.getText() == null || productField.getText().trim().equals(""))
            productTable.setItems(ims.getProducts());
        else{
            // Executes if the search parameters aren't empty
            try{
                // Executes if the search parameter is numerical
                int search = Integer.parseInt(productField.getText());
                
                // Creates an ObservableList to hold the searched for values
                ObservableList<Product> products = FXCollections.observableArrayList();
                
                // Tests if there are any products that match the search parameters
                for(int i = 0; i < ims.getInventory().getProductsSize(); i++){
                    Product p = ims.getInventory().lookupProduct(i);
                    if((p.getProductID() + "").contains(productField.getText().trim())){
                        // Adds products that match the search parameters
                        products.add(p);
                    }
                }
                
                // Sets the TableView to use the searched for products
                productTable.setItems(products);
            }catch(NumberFormatException e){
                // Executes if the search parameters are alphabetical
                ObservableList<Product> products = FXCollections.observableArrayList();
                
                for(int i = 0; i < ims.getInventory().getProductsSize(); i++){
                    Product p = ims.getInventory().lookupProduct(i);
                    if((p.getName().toUpperCase() + "").contains(productField.getText().trim().toUpperCase())){
                        products.add(p);
                    }
                }
                
                productTable.setItems(products);
            }
        }
    }
    
    // Allows the user to add a product
    @FXML
    private void onAddProduct(){
        // Displays the AddProduct dialog
        ims.showAddProductDialog();
        // Resets the static product and the product TableView
        productTable.setItems(ims.getProducts());
        product = new Product();
    }
    
    // Allows the user to modify a product
    @FXML
    private void onModProduct(){
        // Gets the selected product
        product = productTable.getSelectionModel().getSelectedItem();
        // Displays the ModifyProduct dialog
        ims.showModProductDialog();
        // Resets the static product, the inventory's product list, and the product TableView
        product = new Product();
        for(int i = 0; i < ims.getInventory().getProductsSize(); i++){
            ims.getProducts().set(i, ims.getInventory().lookupProduct(i));
        }
        productTable.setItems(ims.getProducts());
    }
    
    @FXML void onDeleteProduct(){
        if(confirmProductDelete()){
            if(ims.getInventory().getProductsSize() > 0){
                ims.getInventory().removeProduct(productTable.getSelectionModel().getSelectedItem().getProductID() - 1);
                for(int i = 0; i < ims.getInventory().getProductsSize(); i++){
                    ims.getProducts().set(i, ims.getInventory().lookupProduct(i));
                }
                productTable.setItems(ims.getProducts());
            }
        }
    }
    
    // Has the user confirm whether they want to delete a product or not
    private boolean confirmProductDelete(){
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setContentText("This product has one or more parts assigned to it.\n" +
                             "Are you sure you want to delete it?");
        
        ButtonType yes = new ButtonType("Yes");
        ButtonType no = new ButtonType("No");
        
        alert.getButtonTypes().setAll(yes, no);
        
        Optional<ButtonType> result = alert.showAndWait();
        
        return result.get() == yes;
    }
    
    // Sets the InventoryManagementSystem this controller uses for utility purposes
    public void setMainApp(InventoryManagementSystem ims){
        // Sets the controller's InventoryManagementSystem to the sent InventoryManagementSystem
        this.ims = ims;
        
        // Fills the TableViews
        partTable.setItems(ims.getParts());
        productTable.setItems(ims.getProducts());
        
        // Lets the partTable's columns know what data to display
        partID.setCellValueFactory(new PropertyValueFactory<>("partID"));
        partName.setCellValueFactory(new PropertyValueFactory<>("name"));
        partInventory.setCellValueFactory(new PropertyValueFactory<>("inStock"));
        partPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        
        // Lets the productTable's columns know what data to display
        productID.setCellValueFactory(new PropertyValueFactory<>("productID"));
        productName.setCellValueFactory(new PropertyValueFactory<>("name"));
        productInventory.setCellValueFactory(new PropertyValueFactory<>("inStock"));
        productPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
    }
}
