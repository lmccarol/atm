package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

public class TransactionListController {

    @FXML
    private Button SubmitBtn;

    @FXML
    private TextField tfAccountNum;
    
  //variables for database connection
    Connection Con = null;
    PreparedStatement pst = null;
    ResultSet Rs = null;
    Statement St = null;
    
    @FXML
    private void clearTransactionList() {
    	tfAccountNum.setText("");
    	
    }

    @FXML
    void submitBtnClicked(ActionEvent event) {
    	if (tfAccountNum.getText().isEmpty()) {
    		Alert alert = new Alert(AlertType.INFORMATION);
    		alert.setTitle("Information missing");
    		alert.setContentText("Account Number needs to be entered");
    		alert.showAndWait();
    	} else {
    		try {
    			String Query = "SELECT transaction_id, trans_type_id, amount FROM transaction WHERE account_id = ?";
            		// connect to database
            		Con = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm6070_db", "root", "");
            		pst = Con.prepareStatement(Query);
            		pst.setInt(1, Integer.parseInt(tfAccountNum.getText()));
            		Rs = pst.executeQuery();
            		
            		List<String> transactions = new ArrayList<String>();
            		while (Rs.next()) {
            			// Retrieve transaction details
                        int transactionId = Rs.getInt("transaction_id");
                        int transactionType = Rs.getInt("trans_type_id");
                        int amount = Rs.getInt("amount");
                        String transType = "";
                        
                        switch (transactionType) {
                        case 1: transType = "deposit";
                        	break;
                        case 2: transType = "withdrawal";
                        	break;
                        case 3: transType = "transfer";
                        	break;
                        case 4: transType = "bill payment";
                        	break;
                        default: break;
                        }
                        
                     // Construct a string with transaction details
                        String transaction = "Transaction ID: " + transactionId + 
                                             ", Type: " + transType + 
                                             ", Amount: $" + amount;
                        
                        transactions.add(transaction);
            			
            		}
            		// List transactions
                    StringBuilder transactionInfo = new StringBuilder("Transactions:\n");
                    for (String transaction : transactions) {
                        transactionInfo.append(transaction).append("\n");
                    }
            		JOptionPane.showMessageDialog(null, transactionInfo.toString());
            		Rs.close();
            		pst.close();
            		Con.close();
            		clearTransactionList();
    		} catch (Exception e) {
        		JOptionPane.showMessageDialog(null, e);
            }
    }
    }
}
