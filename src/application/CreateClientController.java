package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.JOptionPane;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

public class CreateClientController {

    @FXML
    private Button SubmitBtn;

    @FXML
    private TextField tfClientName;

    @FXML
    private TextField tfEmail;

    @FXML
    private TextField tfPIN;

    @FXML
    private TextField tfPhone;
    
  //variables for database connection
    Connection Con = null;
    PreparedStatement pst = null;
    ResultSet Rs = null;
    
    @FXML
    private void clearClientForm() {
    	tfClientName.setText("");
    	tfEmail.setText("");
    	tfPhone.setText("");
    	tfPIN.setText("");
    }

    @FXML
    void submitBtnClicked(ActionEvent event) {
    	if (tfClientName.getText().isEmpty() || tfPhone.getText().isEmpty() || tfEmail.getText().isEmpty() || tfPIN.getText().isEmpty()) {
    		Alert alert = new Alert(AlertType.INFORMATION);
    		alert.setTitle("Information missing");
    		alert.setContentText("Please enter missing information.");
    		alert.showAndWait();
    	} else {
    	try {
    	Con = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm6070_db", "root", "");
    	String insertQuery = "INSERT INTO client (full_name, phone, email, pin) VALUES (?,?,?,?)";
    	pst = Con.prepareStatement(insertQuery);
    		pst.setString(1, tfClientName.getText());
    		pst.setString(2, tfPhone.getText());
    		pst.setString(3, tfEmail.getText());
    		pst.setInt(4, Integer.parseInt(tfPIN.getText()));
    		pst.executeUpdate();
    		
    		JOptionPane.showMessageDialog(null, "Client has been added to database.");
    		pst.close();
    	 	Con.close();
    	 	clearClientForm();
    	}catch (Exception e) {
    		JOptionPane.showMessageDialog(null, e);
    		}
    	}
    }
}
