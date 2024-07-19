package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JOptionPane;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

public class AddMoneyController {

    @FXML
    private Button SubmitBtn;

    @FXML
    private TextField tfAmount;

  //variables for database connection
    Connection Con = null;
    PreparedStatement pst = null;
    ResultSet Rs = null;
    Statement St = null;
    
    @FXML
    private void clearAddMoneyForm() {
    	tfAmount.setText("");
    }
    
    @FXML
    void submitBtnClicked(ActionEvent event) {
    	if (tfAmount.getText().isEmpty()) {
    		Alert alert = new Alert(AlertType.INFORMATION);
    		alert.setTitle("Information missing");
    		alert.setContentText("Amount to add to ATM needs to be entered");
    		alert.showAndWait();
    } else {
    	try {
    		//add tfAmount to balance of ATM - need variable for amount
    		//Select query to find current balance of atm
    		//variable Integer for current balance
    		
    		int amount = Integer.parseInt(tfAmount.getText());
    		String Query1 = "SELECT atm_balance FROM atmbalance ORDER BY atmbalance_id DESC LIMIT 1";
    		//Connect to database
    		Con = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm6070_db", "root", "");
    		St = Con.createStatement();
    		Rs = St.executeQuery(Query1);
    		
    		int currentBalance = Rs.getInt("atm_balance");
    		JOptionPane.showMessageDialog(null, "Current balance in ATM is: $" + currentBalance);

    		
    		//Check that current balance is less than $20000 and amount to be deposited added to current balance is <= $20000
    		if (currentBalance < 20000 && (currentBalance + amount)<= 20000) {
    			//Deposit amount to atm
    			String insertQuery = "INSERT INTO atmbalance (atm_balance) VALUES (?)";

    			pst = Con.prepareStatement(insertQuery);
    			pst.setInt(1, amount + currentBalance);
    			pst.executeUpdate();
    			currentBalance = currentBalance + amount;
    			JOptionPane.showMessageDialog(null, "Amount has been deposited to ATM. Current Balance is $" + currentBalance);
    			
    			
    		} else {
    			JOptionPane.showMessageDialog(null, "ATM cannot contain more than $20 000.");
    		}
    		pst.close();
			St.close();
			Con.close();
			clearAddMoneyForm();
    	} catch (Exception e) {
    		JOptionPane.showMessageDialog(null, e);
        }
    }
}
}
