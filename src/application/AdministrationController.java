package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JOptionPane;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class AdministrationController {

    @FXML
    private Button AddMoneyBtn;

    @FXML
    private Button CloseATMBtn;

    @FXML
    private Button CreateAccountBtn;

    @FXML
    private Button CreateClientBtn;

    @FXML
    private Button IncreaseLOCBtn;

    @FXML
    private Button PayInterestBtn;

    @FXML
    private Button TransactionListBtn;

    @FXML
    private Button WithdrawBtn;
    
  //variables for database connection
    Connection Con = null;
    PreparedStatement pst = null;
    ResultSet Rs = null;
    int rslt;
    Statement St = null;

    @FXML
    void addMoneyBtnClicked(ActionEvent event) {
    	try {
    		Stage stage = new Stage();
			Parent root =  FXMLLoader.load(getClass().getResource("AddMoney.fxml"));
			Scene scene = new Scene(root); 
		      stage.setTitle("Deposit to ATM Form"); 
		      stage.setScene(scene); 
		      stage.show(); 
		} catch(Exception e) {
			e.printStackTrace();
		}
    }

    @FXML
    void closeATMBtnClicked(ActionEvent event) {
    	//closes ATM
    	Platform.exit();
    }

    @FXML
    void createAccountClicked(ActionEvent event) {
    	try {
    		Stage stage = new Stage();
			Parent root =  FXMLLoader.load(getClass().getResource("CreateAccount.fxml"));
			Scene scene = new Scene(root); 
		      stage.setTitle("Add Account Form"); 
		      stage.setScene(scene); 
		      stage.show(); 
		} catch(Exception e) {
			e.printStackTrace();
		}
    }

    @FXML
    void createClientBtnClicked(ActionEvent event) {
    	try {
    		Stage stage = new Stage();
			Parent root =  FXMLLoader.load(getClass().getResource("CreateClient.fxml"));
			Scene scene = new Scene(root); 
		      stage.setTitle("Add Client Form"); 
		      stage.setScene(scene); 
		      stage.show(); 
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
    

    @FXML
    void increaseLOCBtnClicked(ActionEvent event) {
    	try {
    		//Increase by 5% all line of credit accounts
    		String Query = "SELECT * FROM account WHERE account_type_id = 4";
    		// connect to database
    		Con = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm6070_db", "root", "");
    		St = Con.createStatement();
    		Rs = St.executeQuery(Query);
    		while (Rs.next()) {
    			String Query1 = "UPDATE account SET balance = balance + (balance*0.05)"; 
    			St = Con.createStatement();
        		rslt = St.executeUpdate(Query1);//add 5% to line of credit accounts
        		JOptionPane.showMessageDialog(null, "Line of Credit accounts have been increased by 5%.");
    		}
    		Rs.close();
    		St.close();
    		Con.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
    }

    @FXML
    void payInterestBtnClicked(ActionEvent event) {
    	try {
    		//Add 1% interest to all savings accounts
    		String Query = "SELECT * FROM account WHERE account_type_id = 2";
    		// connect to database
    		Con = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm6070_db", "root", "");
    		St = Con.createStatement();
    		Rs = St.executeQuery(Query);
    		while (Rs.next()) {
    			String Query1 = "UPDATE account SET balance = balance + (balance*0.01)"; 
    			St = Con.createStatement();
        		rslt = St.executeUpdate(Query1);//add interest to savings accounts
        		JOptionPane.showMessageDialog(null, "1% interest has been added to savings accounts.");
    		}
    		Rs.close();
    		St.close();
    		Con.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
    }

    @FXML
    void transactionListClicked(ActionEvent event) {
    	try {
    		Stage stage = new Stage();
			Parent root =  FXMLLoader.load(getClass().getResource("TransactionList.fxml"));
			Scene scene = new Scene(root); 
		      stage.setTitle("Transaction List"); 
		      stage.setScene(scene); 
		      stage.show(); 
		} catch(Exception e) {
			e.printStackTrace();
		}
    }

    @FXML
    void withdrawBtnClicked(ActionEvent event) {
    	//withdraw from mortgage account
    	try {
    		Stage stage = new Stage();
			Parent root =  FXMLLoader.load(getClass().getResource("WithdrawFromMortgage.fxml"));
			Scene scene = new Scene(root); 
		      stage.setTitle("Withdraw From Mortgage"); 
		      stage.setScene(scene); 
		      stage.show(); 
		} catch(Exception e) {
			e.printStackTrace();
		}
    }

}
