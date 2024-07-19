package application;
import java.sql.DriverManager;

import javax.swing.JOptionPane;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class TransactionController {

    @FXML
    private Button AccountBalance;

    @FXML
    private Button BillPaymentBtn;

    @FXML
    private Button DepositBtn;

    @FXML
    private Button TransferBtn;

    @FXML
    private Button WithdrawalBtn;

    @FXML
    void accountBalanceClicked(ActionEvent event) {
    	try {
    		Stage stage = new Stage();
			Parent root =  FXMLLoader.load(getClass().getResource("AccountBalance.fxml"));
			Scene scene = new Scene(root); // attach scene graph to scene
		      stage.setTitle("Account Balance"); // displayed in window's title bar
		      stage.setScene(scene); // attach scene to stage
		      stage.show(); // display the stage
    	} catch (Exception e) {
    		JOptionPane.showMessageDialog(null, e);
    	}
    }

    @FXML
    void billPaymentBtnClicked(ActionEvent event) {
    	try {
    		Stage stage = new Stage();
			Parent root =  FXMLLoader.load(getClass().getResource("BillPayment.fxml"));
			Scene scene = new Scene(root); // attach scene graph to scene
		      stage.setTitle("Bill Payment"); // displayed in window's title bar
		      stage.setScene(scene); // attach scene to stage
		      stage.show(); // display the stage
    	} catch (Exception e) {
    		JOptionPane.showMessageDialog(null, e);
    	}
    }

    @FXML
    void depositBtnClicked(ActionEvent event) {
    	try {
    		Stage stage = new Stage();
			Parent root =  FXMLLoader.load(getClass().getResource("Deposit.fxml"));
			Scene scene = new Scene(root); // attach scene graph to scene
		      stage.setTitle("Deposit"); // displayed in window's title bar
		      stage.setScene(scene); // attach scene to stage
		      stage.show(); // display the stage
    	} catch (Exception e) {
    		JOptionPane.showMessageDialog(null, e);
    	}
    }

    @FXML
    void transferBtnClicked(ActionEvent event) {
    	try {
    		Stage stage = new Stage();
			Parent root =  FXMLLoader.load(getClass().getResource("Transfer.fxml"));
			Scene scene = new Scene(root); // attach scene graph to scene
		      stage.setTitle("Transfer"); // displayed in window's title bar
		      stage.setScene(scene); // attach scene to stage
		      stage.show(); // display the stage
    	} catch (Exception e) {
    		JOptionPane.showMessageDialog(null, e);
    	}
    }

    @FXML
    void withdrawalBtnClicked(ActionEvent event) {
    	try {
    		Stage stage = new Stage();
			Parent root =  FXMLLoader.load(getClass().getResource("Withdrawal.fxml"));
			Scene scene = new Scene(root); // attach scene graph to scene
		      stage.setTitle("Withdrawal"); // displayed in window's title bar
		      stage.setScene(scene); // attach scene to stage
		      stage.show(); // display the stage
    	} catch (Exception e) {
    		JOptionPane.showMessageDialog(null, e);
    	}
    }

}
