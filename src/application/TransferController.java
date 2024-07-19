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

public class TransferController {

	@FXML
	private Button SubmitBtn;

	@FXML
	private TextField tfAmount;

	@FXML
	private TextField tfFromAccount;

	@FXML
	private TextField tfToAccount;

	// variables for database connection
	Connection Con = null;
	PreparedStatement pst = null;
	ResultSet Rs = null;
	Statement St = null;
	
	@FXML
    private void clearTransferForm() {
    	tfFromAccount.setText("");
    	tfToAccount.setText("");
    	tfAmount.setText("");
    }

	@FXML
	void submitBtnClicked(ActionEvent event) {
		// verify from account is a checking account
		// verify sufficient funds in from account
		// deduct amount from 'FromAccount' and add to 'ToAccount'; update balances
		// record transaction
		// a transfer to a line of credit will reduce the balance
		if (tfAmount.getText().isEmpty() || tfFromAccount.getText().isEmpty() || tfToAccount.getText().isEmpty()) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Information missing");
			alert.setContentText("Account Numbers and Amount need to be entered.");
			alert.showAndWait();
		} // text fields not empty
		else {

			try {
				// verify from account is a checking account

				String Query = "SELECT * FROM account WHERE account_id=? AND account_type_id = 1";
				int fromAccountID = Integer.parseInt(tfFromAccount.getText());
				int toAccountID = Integer.parseInt(tfToAccount.getText());
				double amount = Double.parseDouble(tfAmount.getText());
				// connect to database
				Con = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm6070_db", "root", "");
				pst = Con.prepareStatement(Query);
				pst.setInt(1, fromAccountID);
				Rs = pst.executeQuery();

				if (Rs.next()) {
					// transfer is from a checking account

					double balance = Rs.getDouble("balance");
					int clientCode = Rs.getInt(2);
					// find out what type of account is toAccount
					String QueryAccount = "SELECT * FROM account WHERE account_id = ?";
					pst = Con.prepareStatement(QueryAccount);
					pst.setInt(1, toAccountID);
					Rs = pst.executeQuery();

					if (Rs.next()) {
						int typeID = Rs.getInt(3);
						if (typeID == 4) {
							// toAccount is a line of credit account. A transfer to this account will reduce
							// the balance.
							// verify sufficient funds in from account
							if (balance >= amount) {
								String updateQuery1 = "UPDATE account SET balance = balance - ? WHERE account_id = ?";
								// line of credit account balance is reduced when a transfer is received
								String updateQuery2 = "UPDATE account SET balance = balance - ? WHERE account_id = ?";
								String insertQuery1 = "INSERT INTO transaction (account_id, trans_type_id, amount) VALUES (?, 3, ?)";
								String insertQuery2 = "INSERT INTO transaction (account_id, trans_type_id, amount) VALUES (?, 3, ?)";

								pst = Con.prepareStatement(updateQuery1);
								pst.setDouble(1, amount);
								pst.setInt(2, fromAccountID);
								pst.executeUpdate();

								pst = Con.prepareStatement(updateQuery2);
								pst.setDouble(1, amount);
								pst.setInt(2, toAccountID);
								pst.executeUpdate();

								pst = Con.prepareStatement(insertQuery1);
								pst.setInt(1, fromAccountID);
								pst.setDouble(2, amount);
								pst.executeUpdate();

								pst = Con.prepareStatement(insertQuery2);
								pst.setInt(1, toAccountID);
								pst.setDouble(2, amount);
								pst.executeUpdate();

								JOptionPane.showMessageDialog(null,
										"Transfer has been made to line of credit account.");
							} else {
								JOptionPane.showMessageDialog(null, "Insufficient funds in checking account.");
							}
						} else
						// toAccount is not a line of credit account
						// verify sufficient funds in from account
						if (balance >= amount) {
							String updateQuery1 = "UPDATE account SET balance = balance - ? WHERE account_id = ?";
							String updateQuery2 = "UPDATE account SET balance = balance + ? WHERE account_id = ?";
							String insertQuery1 = "INSERT INTO transaction (account_id, trans_type_id, amount) VALUES (?, 3, ?)";
							String insertQuery2 = "INSERT INTO transaction (account_id, trans_type_id, amount) VALUES (?, 3, ?)";

							pst = Con.prepareStatement(updateQuery1);
							pst.setDouble(1, amount);
							pst.setInt(2, fromAccountID);
							pst.executeUpdate();

							pst = Con.prepareStatement(updateQuery2);
							pst.setDouble(1, amount);
							pst.setInt(2, toAccountID);
							pst.executeUpdate();

							pst = Con.prepareStatement(insertQuery1);
							pst.setInt(1, fromAccountID);
							pst.setDouble(2, amount);
							pst.executeUpdate();

							pst = Con.prepareStatement(insertQuery2);
							pst.setInt(1, toAccountID);
							pst.setDouble(2, amount);
							pst.executeUpdate();

							JOptionPane.showMessageDialog(null, "Transfer has been made.");
						} else {// check for line of credit account
							String locQuery = "SELECT * FROM account WHERE client_code = ? AND account_type_id = 4";
							pst = Con.prepareStatement(locQuery);
							pst.setInt(1, clientCode);
							Rs = pst.executeQuery();
							double difference = amount - balance;

							if (Rs.next()) {
								// Client has line of credit account
								int locAccount = Rs.getInt(1);
								// Empty checking account and add difference to line of credit account
								String updateQuery1 = "UPDATE account SET balance = 0 WHERE account_id = ?";
								String updateQuery2 = "UPDATE account SET balance = balance + ? WHERE account_id = ?";
								String updateQuery3 = "UPDATE account SET balance = balance + ? WHERE account_id = ?";

								pst = Con.prepareStatement(updateQuery1);
								pst.setInt(1, fromAccountID);
								pst.executeUpdate();

								pst = Con.prepareStatement(updateQuery2);
								pst.setDouble(1, difference);
								pst.setInt(2, locAccount);
								pst.executeUpdate();

								pst = Con.prepareStatement(updateQuery3);
								pst.setDouble(1, amount);
								pst.setInt(2, toAccountID);
								pst.executeUpdate();

								// Add transactions to transaction table
								String insertQuery1 = "INSERT INTO transaction (account_id, trans_type_id, amount) VALUES (?, 3, ?)";
								String insertQuery2 = "INSERT INTO transaction (account_id, trans_type_id, amount) VALUES (?, 3, ?)";
								String insertQuery3 = "INSERT INTO transaction (account_id, trans_type_id, amount) VALUES (?, 3, ?)";
								// from account withdrawal
								pst = Con.prepareStatement(insertQuery1);
								pst.setInt(1, fromAccountID);
								pst.setDouble(2, balance);
								pst.executeUpdate();
								// line of credit increase
								pst = Con.prepareStatement(insertQuery2);
								pst.setInt(1, locAccount);
								pst.setDouble(2, difference);
								pst.executeUpdate();
								// to account deposit
								pst = Con.prepareStatement(insertQuery3);
								pst.setInt(1, toAccountID);
								pst.setDouble(2, amount);
								pst.executeUpdate();

								JOptionPane.showMessageDialog(null,
										"Transfer of $" + String.format("%.2f", balance) + " has been made from checking account. $"
												+ String.format("%.2f", difference) + " has been added to line of credit account.\n" + "$"
												+ String.format("%.2f", amount) + " has been deposited to " + toAccountID);
							} else {
								// insufficient funds in from account
								JOptionPane.showMessageDialog(null, "Insufficient funds in checking account.");
							}
						}
					} // end if loop to check toAccount
				} else {
					JOptionPane.showMessageDialog(null, "Transfer must be from a checking account.");
				}
				Rs.close();
				pst.close();
				Con.close();
				clearTransferForm();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, e);
			}
		} // first if/else loop close
	}// method close

}// class close
