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

public class AccountBalanceController {

	@FXML
	private Button SubmitBtn;

	@FXML
	private TextField tfAccount;

	// variables for database connection
	Connection Con = null;
	PreparedStatement pst = null;
	ResultSet Rs = null;
	
	@FXML
    private void clearBalanceForm() {
    	tfAccount.setText("");
	}

	@FXML
	void submitBtnClicked(ActionEvent event) {
		if (tfAccount.getText().isEmpty()) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Information missing");
			alert.setContentText("Account Number needs to be entered.");
			alert.showAndWait();
		} else {
			// show account balance
			try {
				String Query = "SELECT balance FROM account WHERE account_id=?";
				int accountID = Integer.parseInt(tfAccount.getText());
				// connect to database
				Con = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm6070_db", "root", "");
				pst = Con.prepareStatement(Query);
				pst.setInt(1, accountID);
				Rs = pst.executeQuery();

				if (Rs.next()) {

					double balance = Rs.getDouble(1);
					JOptionPane.showMessageDialog(null, "Account balance is: $" + balance);
				} else {
					JOptionPane.showMessageDialog(null, "Invalid account number.");

				}
				pst.close();
				Con.close();
				clearBalanceForm();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, e);
			}
		}
	}
}
