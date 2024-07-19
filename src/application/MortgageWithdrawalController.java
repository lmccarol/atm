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

public class MortgageWithdrawalController {

    @FXML
    private Button SubmitBtn;

    @FXML
    private TextField tfAmount;

    @FXML
    private TextField tfMortgageAccount;
    
  //variables for database connection
    Connection Con = null;
    PreparedStatement pst = null;
    ResultSet Rs = null;
    Statement St = null;
    int rslt;
    
    @FXML
    private void clearMortgageWithdrawalForm() {
    	tfMortgageAccount.setText("");
    	tfAmount.setText("");
    }

    @FXML
    void submitBtnClicked(ActionEvent event) {
    	if (tfAmount.getText().isEmpty() || tfMortgageAccount.getText().isEmpty()) {
    		Alert alert = new Alert(AlertType.INFORMATION);
    		alert.setTitle("Information missing");
    		alert.setContentText("Mortgage Account and Amount need to be entered");
    		alert.showAndWait();
    }//text fields not empty
    	else {
    		try {
    			//Verify that account is a mortgage account (account_type_id = 3)
    			int accountID = Integer.parseInt(tfMortgageAccount.getText());
    			double amount = Double.parseDouble(tfAmount.getText());
    			String Query = "SELECT * FROM account WHERE account_id = ? AND account_type_id = 3";
    			// connect to database
    			Con = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm6070_db", "root", "");
    			pst = Con.prepareStatement(Query);
    			pst.setInt(1, accountID);
    			Rs = pst.executeQuery();
		
    			if (Rs.next()) {//mortgage account
    				//check balance of mortgage account; if insufficient, line of credit is increased or transaction cancelled
    				double balance = Rs.getDouble("balance");
    				int clientCode = Rs.getInt(2);
    				if (balance > amount) {
    					String updateQuery = "UPDATE account SET balance = balance - ?  WHERE account_id = ?" ;
    					pst = Con.prepareStatement(updateQuery);
    					pst.setDouble(1, amount);
    					pst.setInt(2, accountID);
    					pst.executeUpdate();// reduce mortgage balance
    					
    					//add transaction to transaction table
    					String insertQuery1 = "INSERT INTO transaction (account_id, trans_type_id, amount) VALUES (?, 2, ?)";
						
						//from account withdrawal
						pst = Con.prepareStatement(insertQuery1);
						pst.setInt(1, accountID);
						pst.setDouble(2, -amount);
						pst.executeUpdate();
    					
    					JOptionPane.showMessageDialog(null, "Amount has been withdrawn from mortgage account.");
    				} else {
    					//Insufficient funds -check for line of credit account
    					String locQuery = "SELECT * FROM account WHERE client_code = ? AND account_type_id = 4";
    					pst = Con.prepareStatement(locQuery);
    					pst.setInt(1, clientCode);
    					Rs = pst.executeQuery();
				
    					double difference = amount - balance;
    					if (Rs.next()) {
    						//Client has line of credit account
    						int locAccount = Rs.getInt(1);
    						//Empty mortgage account and add difference to line of credit account
    						String updateQuery1 = "UPDATE account SET balance = 0 WHERE account_id = ?";
    						String updateQuery2 = "UPDATE account SET balance = balance + ? WHERE account_id = ?";
					
    						pst = Con.prepareStatement(updateQuery1);
    						pst.setInt(1, accountID);
    						pst.executeUpdate();
    				
    						pst = Con.prepareStatement(updateQuery2);
    						pst.setDouble(1, difference);
    						pst.setInt(2, locAccount);
    						pst.executeUpdate();
    				
    						//Add transactions to transaction table
    						String insertQuery1 = "INSERT INTO transaction (account_id, trans_type_id, amount) VALUES (?, 2, ?)";
    						String insertQuery2 = "INSERT INTO transaction (account_id, trans_type_id, amount) VALUES (?, 2, ?)";
 
    						//from account withdrawal
    						pst = Con.prepareStatement(insertQuery1);
    						pst.setInt(1, accountID);
    						pst.setDouble(2, -balance);
    						pst.executeUpdate();
    						//line of credit increase
    						pst = Con.prepareStatement(insertQuery2);
    						pst.setInt(1, locAccount);
    						pst.setDouble(2, difference);
    						pst.executeUpdate();
    				
    						JOptionPane.showMessageDialog(null, "Withdrawal of $" + balance + " has been made from mortgage account. $" + difference + " has been added to line of credit account.");
    					
    				} else {//no line of credit account
						JOptionPane.showMessageDialog(null, "Insufficient funds in mortgage account.");
					}
    				}
    			}//end of mortgage account verification
    			else {
    				JOptionPane.showMessageDialog(null, "Account must be a mortgage account.");
    			}
    			Rs.close();
        		pst.close();
        		Con.close();
        		clearMortgageWithdrawalForm();
    		
    	} catch (Exception e) {
    		JOptionPane.showMessageDialog(null, e);
    	}//end of catch
    	}//first if/else loop close
    }//method submitBtnClicked close
}//class closed
       

