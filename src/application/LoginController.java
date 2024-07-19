package application;

import java.awt.HeadlessException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JOptionPane;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private RadioButton AdminRadioBtn;

    @FXML
    private RadioButton ClientRadioBtn;

    @FXML
    private Button LoginBtn;

    @FXML
    private TextField tfClientCode;

    @FXML
    private PasswordField tfPIN;

    @FXML
    private ToggleGroup userToggleGroup;

    //variables for database connection
    Connection Con = null;
    PreparedStatement pst = null;
    ResultSet Rs = null;
    Statement St = null;
    //Variable for login attempts
    int loginAttempts = 3;
    //variable for radioBtn selected
    Boolean adminBtnSelected = false;
    
    @FXML
    private void clearLogin() {
    	tfClientCode.setText("");
    	tfPIN.setText("");
    }
    
    @FXML
    void userRadioButtonSelected(ActionEvent event) {
      	    if (AdminRadioBtn.isSelected()) {
      	    	adminBtnSelected = true;
      	    } else if (ClientRadioBtn.isSelected()) {
      	    	adminBtnSelected = false;
      	    }
    }
    
    @FXML
    void loginButtonClicked(ActionEvent event) {
    	if (adminBtnSelected) {
    		if (tfClientCode.getText().isEmpty() || tfPIN.getText().isEmpty()) {
    			JOptionPane.showMessageDialog(null, "Please enter Admin Code and PIN.");
    		} else {
    		//verify code & password
    		String adminCode = "admin";
    		
    		String storedHashedPassword = "71999f0e710428a8802154cac7ca7f21ba11c769";
    		// User input password
            String userInputPassword = tfPIN.getText();
         // Hash the user's input password
            String hashedUserInput = hashPassword(userInputPassword);
         // Compare the hashed input with the stored hashed password
           
            if (adminCode.equals(tfClientCode.getText()) && hashedUserInput.equals(storedHashedPassword)) {
            	//loadAdminPage();
    			try {	
    			Stage stage = new Stage();
    			Parent root = FXMLLoader.load(getClass().getResource("AdminPage.fxml"));
    			Scene scene = new Scene(root);
    			stage.setTitle("Administration Page");
    			stage.setScene(scene);
    			stage.show();
    			} catch (IOException e) {
    			e.printStackTrace();
    			}
    		} else {
    			JOptionPane.showMessageDialog(null, "Admin code and/or PIN are incorrect.");
    		}
    	}    
    }else //ClientCodeBtn is clicked
    	if (tfClientCode.getText().isEmpty() || tfPIN.getText().isEmpty()) {
    		Alert alert = new Alert(AlertType.INFORMATION);
    		alert.setTitle("Information missing");
    		alert.setContentText("Client Code and PIN need to be entered");
    		alert.showAndWait();
    } else if (loginAttempts != 0) {
    	try {
    		String Query = "SELECT * FROM client WHERE client_code='"+tfClientCode.getText()+"' AND PIN="+tfPIN.getText()+"";
    		// connect to database
    		Con = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm6070_db", "root", "");
    		St = Con.createStatement();
    		Rs = St.executeQuery(Query);
    		if (Rs.next()) {
    			//transaction screen opens
    			Stage stage = new Stage();
    			Parent root =  FXMLLoader.load(getClass().getResource("Transaction.fxml"));
    			Scene scene = new Scene(root); // attach scene graph to scene
    		     stage.setTitle("Transaction"); // displayed in window's title bar
    		     stage.setScene(scene); // attach scene to stage
    		     stage.show(); // display the stage
    		}else {
    			JOptionPane.showMessageDialog(null, "Wrong Client Code or PIN");
    			loginAttempts--;
    		}
    		Rs.close();
    		St.close();
    		Con.close();
    		clearLogin();
	
    } catch (Exception e) {
		JOptionPane.showMessageDialog(null, e);
    }
    } else {
    	//Block client from system
    	JOptionPane.showMessageDialog(null, "Maximum number of attempts exceeded. Please contact your bank.");
    	//Refresh login page
    	clearLogin();
    	loginAttempts = 3;
    }
    }
    
    		
 @FXML      
private static String hashPassword(String password) {
    try {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] passwordBytes = password.getBytes();
        byte[] hashedBytes = md.digest(passwordBytes);

        // Convert the byte array to a hexadecimal string
        StringBuilder sb = new StringBuilder();
        for (byte b : hashedBytes) {
            sb.append(String.format("%02x", b));
        }
        //System.out.println(sb);
        return sb.toString();
        
    } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
        return null;
    }
}
}  	
    
   
    
    
    
    
   




