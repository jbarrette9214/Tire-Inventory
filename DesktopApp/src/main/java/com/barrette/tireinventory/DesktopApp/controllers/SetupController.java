package com.barrette.tireinventory.DesktopApp.controllers;

import com.barrette.tireinventory.Models.SetupData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class SetupController {
	SetupData setupData;
	
	@FXML ToggleGroup addressGroup;
	@FXML RadioButton address1, address2;
	
	@FXML TextField userNameText, ipAddress;
	
	@FXML PasswordField pass1Field, pass2Field;
	
	@FXML Label passMatch;
	

	
	
	@FXML public void initialize() {
		address1.setSelected(true);

		
		setupData = new SetupData();
		
		if(setupData.getUserName() != null) {
			userNameText.setText(setupData.getUserName());
			pass1Field.setText(setupData.getPassword().toString());
			pass2Field.setText("********");
			if(setupData.getServerAddress() != "localhost") {
				address2.setSelected(true);
				ipAddress.setEditable(true);
				ipAddress.setText(setupData.getServerAddress());
			} 
		}
		
	}
	
	/**
	 * Handler for the save Button, will check all information and call SetupData.save() to write
	 * all information to the file
	 * @param ae
	 */
	@FXML protected void saveButtonHandler(ActionEvent ae) {
		setupData.setUserName(userNameText.getText());
		boolean matches = false;
		if(pass1Field.getText().equals(pass2Field.getText())) {
			matches = true;
			setupData.setPassword(pass1Field.getText().toCharArray());
		} else {
			matches = false;
			passMatch.setVisible(true);
		}
		
		
		if(address1.isSelected()) {
			setupData.setServerAddress("localhost");
		} else {
			setupData.setServerAddress(ipAddress.getText());
		}
		
		if(setupData.getUserName() != null && setupData.getPassword() != null && matches == true && 
				setupData.getServerAddress() != null) {
			setupData.saveToFile();	
		}
		
	}
	
	/**
	 * calls the initialize function to reset all the fields
	 * @param ae
	 */
	@FXML protected void resetButtonHandler(ActionEvent ae) {
		initialize();
	}
	
	public void resetSetup() {
		initialize();
	}
	

}
