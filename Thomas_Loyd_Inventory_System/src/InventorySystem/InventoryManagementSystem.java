

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Thomas Loyd
 */
package InventorySystem;

import java.io.IOException;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import InventorySystem.model.*;
import InventorySystem.view_controller.*;

public class InventoryManagementSystem extends Application{
    // Creates an Inventory object to hold the parts and products
    private static Inventory inventory = new Inventory();
    
    // Creates a Stage and an AnchorPane for display and navigation purposes
    private Stage mainStage = new Stage();
    private AnchorPane originPane = new AnchorPane();
    
    // Creates ObservableLists for the products and parts in the inventory
    private static ObservableList<Part> observableParts = 
            FXCollections.observableArrayList();
    private static ObservableList<Product> observableProducts = 
            FXCollections.observableArrayList();
    
    // An empty constructor for utility purposes
    public InventoryManagementSystem(){
        
    }
    
    // A getter method for the observableParts list
    public ObservableList<Part> getParts(){
        observableParts = FXCollections.observableArrayList();
        for(int i = 0; i < inventory.getPartsSize(); i++){
            observableParts.add(inventory.lookupPart(i));
        }
        return observableParts;
    }
    
    // A getter method for the observableProducts list
    public ObservableList<Product> getProducts(){
        observableProducts = FXCollections.observableArrayList();
        for(int i = 0; i < inventory.getProductsSize(); i++){
            observableProducts.add(inventory.lookupProduct(i));
        }
        return observableProducts;
    }
    
    // A getter method for the mainStage
    public Stage getMainStage(){
        return mainStage;
    }
    
    // A getter method for the inventory
    public Inventory getInventory(){
        return inventory;
    }
    
    // The start method to execute the application
    @Override
    public void start(Stage mainStage){
        // Sets the applications mainStage
        this.mainStage = mainStage;
        this.mainStage.setTitle("Inventory Management System");
        
        // Displays the application window
        Scene scene = new Scene(originPane);
        mainStage.setScene(scene);
        mainStage.show();
        showMainScreen();
    }
    
    // Shows the main screen of the application
    public void showMainScreen(){
        try{
            // Loads the needed FXML document
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(InventoryManagementSystem.class.getResource("view_controller/MainScreen.fxml"));

            // Uses the FXML document to populate the AnchorPane
            originPane.getChildren().setAll((AnchorPane) loader.load());
            
            // Sets the controller for the main screen and sends it this class for management
            MainScreenController controller = loader.getController();
            controller.setMainApp(this);
        }catch(IOException ioe){ioe.printStackTrace();}
    }
    
    // Shows the add part dialog screen
    public boolean showAddPartDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(InventoryManagementSystem.class.getResource("view_controller/AddOrModifyInhouseParts.fxml"));
            
            // Creates a new window to display the add part dialog as a pop-up
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add Part");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(mainStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            
            AddOrModifyInhousePartsController controller = (AddOrModifyInhousePartsController)loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setMainApp(this);
            
            dialogStage.showAndWait();

            // Returns whether the addition was successful or not
            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Shows the modify inhouse part dialog
    public boolean showModInPartDialog() {
        if(MainScreenController.inPart != null){
            // Executes if the part to be modified isn't null
            try {
               FXMLLoader loader = new FXMLLoader();
                loader.setLocation(InventoryManagementSystem.class.getResource("view_controller/AddOrModifyInhouseParts.fxml"));
                AnchorPane page = (AnchorPane) loader.load();
                
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Modify Part");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                dialogStage.initOwner(mainStage);
                Scene scene = new Scene(page);
                dialogStage.setScene(scene);
                
                AddOrModifyInhousePartsController controller = loader.getController();
                controller.setDialogStage(dialogStage);
                controller.setMainApp(this);
                
                dialogStage.showAndWait();
                
                for(int i = 0; i < inventory.getPartsSize(); i++){
                    observableParts.set(i, inventory.lookupPart(i));
                }
                
                return controller.isOkClicked();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        else
            return false;
    }
    
    // Shows the modify an outsourced part dialog
    public boolean showModOutPartDialog() {
        if(MainScreenController.outPart != null){
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(InventoryManagementSystem.class.getResource("view_controller/AddOrModifyOutsourcedParts.fxml"));
                AnchorPane page = (AnchorPane) loader.load();

                Stage dialogStage = new Stage();
                dialogStage.setTitle("Modify Part");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                dialogStage.initOwner(mainStage);
                Scene scene = new Scene(page);
                dialogStage.setScene(scene);

                AddOrModifyOutsourcedPartsController controller = loader.getController();
                controller.setDialogStage(dialogStage);
                controller.setMainApp(this);
                
                dialogStage.showAndWait();
                
                for(int i = 0; i < inventory.getPartsSize(); i++){
                    observableParts.set(i, inventory.lookupPart(i));
                }

                return controller.isOkClicked();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        else
            return false;
    }
    
    // Displays the add product dialog
    public boolean showAddProductDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(InventoryManagementSystem.class.getResource("view_controller/AddOrModifyProduct.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add Product");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(mainStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            AddOrModifyProductController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setMainApp(this);
            dialogStage.showAndWait();
            
            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Shows the modify a product dialog
    public boolean showModProductDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(InventoryManagementSystem.class.getResource("view_controller/AddOrModifyProduct.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Modify Product");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(mainStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            AddOrModifyProductController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setMainApp(this);
            
            
            dialogStage.showAndWait();

            for(int i = 0; i < inventory.getProductsSize(); i++){
                observableProducts.set(i, inventory.lookupProduct(i));
            }

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Launches the application
    public static void main(String args[]){
        // Fills the ObservableList objects
        for(int i = 0; i < inventory.getPartsSize(); i++){
            observableParts.add(inventory.lookupPart(i));
            observableProducts.add(inventory.lookupProduct(i));
        }
        launch(args);
    }
}
