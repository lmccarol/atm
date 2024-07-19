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

public class BillPaymentController {

	@FXML
	private Button SubmitBtn;

	@FXML
	private TextField tfAccount;

	@FXML
	private TextField tfBillPaymentAmount;

	// variables for database connection
	Connection Con = null;
	PreparedStatement pst = null;
	ResultSet Rs = null;
	Statement St = null;

	@FXML
	void submitBtnClicked(ActionEvent event) {
		// Verify account is a checking account
		// deduct amount of payment from account
		// include fee of $1.25
		// record transaction
		if (tfAccount.getText().isEmpty() || tfBillPaymentAmount.getText().isEmpty()) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Information missing");
			alert.setContentText("Account Number and Amount need to be entered.");
			alert.showAndWait();
		} else {
			try {
				int accountID = Integer.parseInt(tfAccount.getText());
				double payment = Double.parseDouble(tfBillPaymentAmount.getText());
				String Query = "SELECT * FROM account WHERE account_id = ? AND account_type_id = 1";

				// connect to database
				Con = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm6070_db", "root", "");
				pst = Con.prepareStatement(Query);
				pst.setInt(1, accountID);
				Rs = pst.executeQuery();

				if (Rs.next()) {
					// check balance of account is sufficient for bill payment
					int clientCode = Rs.getInt(2);
					double balance = Rs.getDouble(4);

					if (balance >= payment + 1.25) {
						String updateQuery = "UPDATE account SET balance = (balance - ? - 1.25) WHERE account_id = ?";
						String insertQuery = "INSERT INTO transaction (account_id, trans_type_id, amount) VALUES (?, 4, ?)";
						pst = Con.prepareStatement(updateQuery);
						pst.setDouble(1, payment);
						pst.setInt(2, accountID);
						pst.executeUpdate();

						pst = Con.prepareStatement(insertQuery);
						pst.setInt(1, accountID);
						pst.setDouble(2, -(payment + 1.25));
						pst.executeUpdate();

						JOptionPane.showMessageDialog(null, "Payment has been made.");
					} else {
						// balance of account is insufficient; increase line of credit account or send
						// error message
						double difference = payment + 1.25 - balance;
						// Check if Client has line of credit account
						String Query2 = "SELECT * FROM account WHERE client_code = ? AND account_type_id = 4";
						pst = Con.prepareStatement(Query2);
						pst.setInt(1, clientCode);
						Rs = pst.executeQuery();
						if (Rs.next()) {
							// Client has line of credit account
							int locAccount = Rs.getInt(1);
							// Empty checking account and add difference to line of credit account
							String updateQuery1 = "UPDATE account SET balance = 0 WHERE account_id = ?";
							String updateQuery2 = "UPDATE account SET balance = balance + ? WHERE client_code = ? AND account_type_id = 4";

							pst = Con.prepareStatement(updateQuery1);
							pst.setInt(1, accountID);
							pst.executeUpdate();

							pst = Con.prepareStatement(updateQuery2);
							pst.setDouble(1, difference);
							pst.setInt(2, clientCode);
							pst.executeUpdate();

							// Add transactions to transaction table
							String insertQuery1 = "INSERT INTO transaction (account_id, trans_type_id, amount) VALUES (?, 4, ?)";
							String insertQuery2 = "INSERT INTO transaction (account_id, trans_type_id, amount) VALUES (?, 4, ?)";
							pst = Con.prepareStatement(insertQuery1);
							pst.setInt(1, accountID);
							pst.setDouble(2, -balance);
							pst.executeUpdate();

							pst = Con.prepareStatement(insertQuery2);
							pst.setInt(1, locAccount);
							pst.setDouble(2, difference);
							pst.executeUpdate();

							JOptionPane.showMessageDialog(null, "Partial Payment of $" + String.format("%.2f", balance)
									+ " has been made from checking account. $" + String.format("%.2f", difference)
									+ " has been added to line of credit account.");
						} else {
							JOptionPane.showMessageDialog(null, "Insufficient funds for payment.");
						}
					}

				} else {
					JOptionPane.showMessageDialog(null, "Account must be a checking account for bill payments.");
				}
				Rs.close();
				pst.close();
				Con.close();

			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, e);
			}
		}
	}
}
