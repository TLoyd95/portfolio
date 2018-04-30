package SchedulingApp;

import SchedulingApp.view_controller.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.BiFunction;
import java.util.Locale;

/**
 *
 * @author Thomas Loyd
 */
public class SchedulingApp extends Application{
    
    // Creates a stage and pane for display and navigation purposes
    private static Stage mainStage;
    private static final AnchorPane ORIGIN = new AnchorPane();
    
    // Determines the users location
    private final static Locale L = Locale.getDefault();
    
    // Stores the current user's username for auditing purposes
    private static String user = "";
    
    /* Creates a BiFunction lambda to handle errors, with one input being the body text 
       and the other input being the title text, returning an alert to the calling method */
    private static final BiFunction<String, String, Alert> ERROR = (s1, s2) -> {
        Alert a = new Alert(AlertType.ERROR);
        a.initStyle(StageStyle.UTILITY);
        a.setContentText(s1);
        a.setTitle(s2);
        return a;
    };
    
    // Allows application windows to get current Locale
    public static Locale getLocale(){
        return L;
    }
    
    // Getter and setter methods for the user value
    public static String getUser(){
        return user;
    }
    public static void setUser(String u){
        user = u;
    }
    
    // Allows other pages of the program to access the ORIGIN AnchorPane
    public static AnchorPane getOrigin(){
        return ORIGIN;
    }
    
    // Starts the application
    @Override
    public void start(Stage mainStage) throws Exception {
        // Sets the application's main stage
        this.mainStage = mainStage;
        this.mainStage.setTitle("Scheduling Application");
        
        // Desplays the application window
        Scene scene = new Scene(ORIGIN);
        mainStage.setScene(scene);
        mainStage.show();
        showLoginScreen();
    }

    // Displays the login screen on startup
    public static void showLoginScreen() throws ClassNotFoundException{
        // Runs if the primary language in the users location is chinese
        if (L.getLanguage().equals("zh")){
            try{
                // Loads the login FXML document
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(SchedulingApp.class.getResource("view_controller/Login_China.fxml"));
            
                // Sets the controller for the login screen
                LoginController controller = new LoginController();
            
                // Uses the FXML document to populate originPane
                ORIGIN.getChildren().setAll((AnchorPane) loader.load());
            }catch(IOException ioe){ioe.printStackTrace();}
        }
        // Runs when any other language is detected
        else{
            try{
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(SchedulingApp.class.getResource("view_controller/Login.fxml"));
            
                LoginController controller = new LoginController();
            
                ORIGIN.getChildren().setAll((AnchorPane) loader.load());
            }catch(IOException ioe){ioe.printStackTrace();}
        }
    }
    
    // Runs the application
    public static void main(String[] args) throws ClassNotFoundException{
        launch(args);
    }
    
    // Allows application windows to access the database connection
    public static Connection getConn() throws ClassNotFoundException{
        // Holds the information needed to access the database
        String dbName = "U04ITG";
        String dbUrl = "jdbc:mysql://52.206.157.109/" + dbName;
        String username = "U04ITG";
        String password = "53688249366";
        try {
            // Accesses the MySQL driver and uses the database login information as the parameters
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection(dbUrl,username,password);
            //System.out.println("Connected to database : " + db);
        } catch (SQLException sqle){popup("net"); sqle.printStackTrace(); return null;}
    }
    
    // Creates the text to be used for the error messages
    public static void popup(String s){
        // Runs if the connection with the MySQL database is compromised
        if(s.equals("net")){
           if(L.getLanguage().equals("zh"))
               ERROR.apply("无法建立网络连接。", "错误").showAndWait();
            else
               ERROR.apply("Couldn't establish network connection.", "Error").showAndWait();
        }
        
        // Popups from the LoginController class
        
        // Runs if the user value is blank
        if(s.equals("user")){
            if(L.getLanguage().equals("zh"))
                ERROR.apply("请输入一个值转换为用户名。", "错误").showAndWait();
            else
                ERROR.apply("Please enter a value into username.", "Error").showAndWait();
        }
        // Runs if the password value is blank
        if(s.equals("password")){
            if(L.getLanguage().equals("zh"))
                ERROR.apply("请输入一个值转换为密码。", "错误").showAndWait();
            else
                ERROR.apply("Please enter a value into password.", "Error").showAndWait();
        }
        // Runs if both the user and password values are blank
        if(s.equals("both")){
            if(L.getLanguage().equals("zh"))
                ERROR.apply("请输入一个值，将用户名和密码。", "错误").showAndWait();
            else
                ERROR.apply("Please enter a value into username and password.", "Error").showAndWait();
        }
        // Runs if the username and password don't match
        if(s.equals("fail")){
            if(L.getLanguage().equals("zh"))
                ERROR.apply("用户名和密码不匹配。", "错误").showAndWait();
            else
                ERROR.apply("Username and password do not match.", "Error").showAndWait();
        }
        // Runs if the user successfully logs in
        if(s.equals("success")){
            Alert a;
            
            if(L.getLanguage().equals("zh"))
                a = ERROR.apply("欢迎您的到来, " + user, "登录成功");
            else
                a = ERROR.apply("Welcome, " + user, "Login Successful");
            
            a.setAlertType(AlertType.CONFIRMATION);
            a.showAndWait();
        }
    }
}
