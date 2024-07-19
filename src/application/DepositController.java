package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.JOptionPane;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class DepositController {

    @FXML
    private Button SubmitBtn;

    @FXML
    private TextField tfAccountId;

    @FXML
    private TextField tfDepositAmount;

 // variables for database connection
 	Connection Con = null;
 	PreparedStatement pst = null;
 	ResultSet Rs = null;
 	
 	@FXML
    private void clearDepositForm() {
    	tfAccountId.setText("");
    	tfDepositAmount.setText("");
    }

    @FXML
    void submitBtnClicked(ActionEvent event) {
    	if (tfDepositAmount.getText().isEmpty() || tfAccountId.getText().isEmpty()) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Information missing");
			alert.setContentText("Account Number and Amount need to be entered");
			alert.showAndWait();
		} // text fields not empty
		else {
			try {
				double depositAmount = Double.parseDouble(tfDepositAmount.getText());
			 	int accountId = Integer.parseInt(tfAccountId.getText());
				String Query = "SELECT * FROM account WHERE account_id=?";
				// connect to database
				Con = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm6070_db", "root", "");
				pst = Con.prepareStatement(Query);
				pst.setInt(1, accountId);
				Rs = pst.executeQuery();
				if (Rs.next()) {
					// account is valid
					// verify that account is checking, savings or mortgage account
					int accountType = Rs.getInt(3);
					if (accountType == 1 || accountType == 2 || accountType == 3) {
						// account type is valid

						String Query1 = "UPDATE account SET balance = balance + ? WHERE account_id = ?";
						String Query2 = "INSERT INTO transaction (account_id, trans_type_id, amount) VALUES( ?, 1, ?)";

						pst = Con.prepareStatement(Query1);
						pst.setDouble(1, depositAmount);
						pst.setInt(2, accountId);
						pst.executeUpdate();// add deposit to account
						
						pst = Con.prepareStatement(Query2);
						pst.setInt(1, accountId);
						pst.setDouble(2, depositAmount);
						pst.executeUpdate();// record transaction

						JOptionPane.showMessageDialog(null, "Amount has been deposited.");
					} else {
						JOptionPane.showMessageDialog(null, "Account must be a checking, savings or mortgage account.");
					}

				} else {
					JOptionPane.showMessageDialog(null, "Invalid account number.");
				}
				pst.close();
				Con.close();
				clearDepositForm();

			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, e);
			}
		}// first if/else loop close
    }

}
