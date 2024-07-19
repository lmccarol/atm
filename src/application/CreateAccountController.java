package application;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class CreateAccountController {
	
	@FXML
	ObservableList<String> accountTypeList = FXCollections.observableArrayList("checking", "savings", "mortgage", "line of credit");


    @FXML
    private ChoiceBox cbAccountType;

    @FXML
    private TextField tfClientCode;
    
    Integer accountType;
  //variables for database connection
    Connection Con = null;
    PreparedStatement pst = null;
    ResultSet Rs = null;
    
    @FXML
    void submitBtnClicked(ActionEvent event) {
    	if (tfClientCode.getText().isEmpty()) {
    		JOptionPane.showMessageDialog(null, "Please enter Client Code.");
    	} else {
    		try {
    			//Verify that client has a checking account
    			if (cbAccountType.getValue() != "checking") {
    				String Query = "SELECT * FROM account WHERE client_code LIKE ? AND account_type_id = 1";
    				// connect to database
    				Con = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm6070_db", "root", "");
    				pst = Con.prepareStatement(Query);
    				pst.setString(1, "%" + tfClientCode.getText()+ "%");
    				Rs = pst.executeQuery();
    				if (Rs.next()) {
    					//Client has a checking account
    					//create new account
    					String accountTypeValue = (String) cbAccountType.getValue();
    					switch (accountTypeValue) {
    					case "checking": accountType = 1;
    						break;
    					case "savings": accountType = 2;
    						break;
    					case "mortgage": accountType = 3;
    						break;
    					case "line of credit": accountType = 4;
    						break;
    					default:
    						break;
    					}
    					String insertQuery = "INSERT INTO account (client_code, account_type_id, balance) VALUES (?, ?, 0)";
    					pst = Con.prepareStatement(insertQuery);
    					pst.setInt(1, Integer.parseInt(tfClientCode.getText()));
    					pst.setInt(2, accountType);
    					pst.executeUpdate();
    					
    					JOptionPane.showMessageDialog(null, "Account has been created.");
    				} else {
    					JOptionPane.showMessageDialog(null, "Client must have a checking account.");
    				}
    				} else {
    					//cbAccountType.getValue() is checking
    					//create checking account
    					
    					String insertQuery = "INSERT INTO account (client_code, account_type_id, balance) VALUES (?, 1, 0)";
    					Con = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm6070_db", "root", "");
    					pst = Con.prepareStatement(insertQuery);
    					pst.setInt(1, Integer.parseInt(tfClientCode.getText()));
    					pst.executeUpdate();
    					
    					JOptionPane.showMessageDialog(null, "Account has been created.");
    				}
    			pst.close();
        		Con.close();
    		} catch (Exception e) {
    			JOptionPane.showMessageDialog(null, e);
    		}
    	}
    }
    
    public void initialize() {
    	cbAccountType.setItems(accountTypeList);
       //Add listener
    	//String accountType = cbAccountType.getSelectionModel.getSelectedItem()
    	cbAccountType.getSelectionModel().getSelectedItem();
    	cbAccountType.getValue();
     }
}
