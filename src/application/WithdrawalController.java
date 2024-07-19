package application;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

public class WithdrawalController {

    @FXML
    private Button SubmitBtn;

    @FXML
    private TextField tfAccount;

    @FXML
    private TextField tfWithdrawalAmount;
    
  //variables for database connection
    Connection Con = null;
    PreparedStatement pst = null;
    ResultSet Rs = null;
    Statement St = null;
    
    @FXML
    private void clearWithdrawalForm() {
    	tfAccount.setText("");
    	tfWithdrawalAmount.setText("");
    }

    @FXML
    void submitBtnClicked(ActionEvent event) {
    	if (tfWithdrawalAmount.getText().isEmpty() || tfAccount.getText().isEmpty()) {
    		Alert alert = new Alert(AlertType.INFORMATION);
    		alert.setTitle("Information missing");
    		alert.setContentText("Account Number and Amount need to be entered");
    		alert.showAndWait();
    }//text fields not empty
    	else {
       	try {
       		int accountID = Integer.parseInt(tfAccount.getText());
       		int amount = Integer.parseInt(tfWithdrawalAmount.getText());
       	//select checking or savings account
        	String Query = "SELECT * FROM account WHERE account_id=? AND (account_type_id = 1 OR account_type_id = 2)";
            		// connect to database
            		Con = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm6070_db", "root", "");
            		pst = Con.prepareStatement(Query);
            		pst.setInt(1, accountID);
            		Rs = pst.executeQuery();
            		
            		if (Rs.next()) {
            			//account is valid
            			double balance = Rs.getDouble("balance");
                		int clientCode = Rs.getInt(2);
            			//verify enough funds in ATM
            			String atmQuery = "SELECT atm_balance FROM atmbalance ORDER BY atmbalance_id DESC LIMIT 1";
            			St = Con.createStatement();
                		Rs = St.executeQuery(atmQuery);
                		if (Rs.next()) {
                			int currentBalance = Rs.getInt("atm_balance");
                		
                		if (currentBalance < amount) {
                			JOptionPane.showMessageDialog(null, "Insufficient funds in ATM for withdrawal.");
                		} else
            			//verify amount of withdrawal is a multiple of $10 and max is $1000
            			
            			if (amount > 1000 || amount % 10 != 0) {
        					JOptionPane.showMessageDialog(null, "Amount of withdrawal must be less than or equal to $1000 and it must be a multiple of $10.");
			           				
        					} else if (balance >= amount){
        						//verify sufficient funds in account
        						String updateQuery = "UPDATE account SET balance = balance - ? WHERE account_id = ?";
        	    				String insertQuery = "INSERT INTO transaction (account_id, trans_type_id, amount) VALUES (?, 2, ?)";
        	    				String insertATMQuery = "INSERT INTO atmbalance (atm_balance) VALUES (?) ";
        	    				pst = Con.prepareStatement(updateQuery);
        	    				pst.setDouble(1, amount);
        						pst.setInt(2, accountID);
        						pst.executeUpdate();
        						
        						pst = Con.prepareStatement(insertQuery);
        						pst.setInt(1, accountID);
        						pst.setInt(2,  -amount);
        						pst.executeUpdate();
        						
        						pst = Con.prepareStatement(insertATMQuery);
        						pst.setInt(1, currentBalance - amount);
        						pst.executeUpdate();
        						  						
        						JOptionPane.showMessageDialog(null, "Withdrawal has been made from account.");
        					} else {//check for line of credit account
        						String locQuery = "SELECT * FROM account WHERE client_code = ? AND account_type_id = 4";
        						pst = Con.prepareStatement(locQuery);
        	    				pst.setInt(1, clientCode);
        	    				Rs = pst.executeQuery();
        	    				Double difference = amount - balance;
        	    				if (Rs.next()) {
        	    					//Client has line of credit account
        	    					//Empty checking/savings account and add difference to line of credit account
        	    					String updateQuery1 = "UPDATE account SET balance = 0 WHERE account_id = ?";
        	    					String updateQuery2 = "UPDATE account SET balance = balance + ? WHERE client_code = ? AND account_type_id = 4";
        	    					String insertATMQuery = "INSERT INTO atmbalance (atm_balance) VALUES (?) ";
        	    					
        	    					pst = Con.prepareStatement(updateQuery1);
        	        				pst.setInt(1, accountID);
        	        				pst.executeUpdate();
        	        				
        	        				pst = Con.prepareStatement(updateQuery2);
        	        				pst.setDouble(1, difference);
        	        				pst.setInt(2, clientCode);
        	        				pst.executeUpdate();
        	        				
        	        				pst = Con.prepareStatement(insertATMQuery);
            						pst.setInt(1, currentBalance - amount);
            						pst.executeUpdate();
        	        				
        	        				//Add transactions to transaction table
        	        				String insertQuery1 = "INSERT INTO transaction (account_id, trans_type_id, amount) VALUES (?, 2, ?)";
        	        				String insertQuery2 = "INSERT INTO transaction (account_id, trans_type_id, amount) VALUES (?, 2, ?)";
        	        				
        	        				pst = Con.prepareStatement(insertQuery1);
        	        				pst.setInt(1, accountID);
        	        				pst.setDouble(2, -balance);
        	        				pst.executeUpdate();
        	        				
        	        				pst = Con.prepareStatement(insertQuery2);
        	        				pst.setInt(1, accountID);
        	        				pst.setDouble(2, difference);
        	        				pst.executeUpdate();
        	        				
        	        				JOptionPane.showMessageDialog(null, "Withdrawal of $" + String.format("%.2f", balance) + " has been made from account. $" + String.format("%.2f", difference) + " has been added to line of credit account.");
        	    				} else {
        						JOptionPane.showMessageDialog(null, "Insufficient funds in the account.");
        					}
        				}
                		} else {
                			JOptionPane.showMessageDialog(null, "ATM error.");
                		}
            		} else {
            			JOptionPane.showMessageDialog(null, "Account must be a checking or savings account.");
            		}
            		Rs.close();
                	pst.close();
            		Con.close();
            		clearWithdrawalForm();
    		
    	} catch (Exception e) {
        		JOptionPane.showMessageDialog(null, e);
    	}
    	}//first if/else loop close
    }//method close
    
}//class close