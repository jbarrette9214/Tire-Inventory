package com.barrette.tireinventory.DesktopApp.controllers;


import com.barrette.tireinventory.DesktopApp.App;
import com.barrette.tireinventory.DesktopApp.models.*;

import java.awt.Event;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;


public class AddTireViewController {
	@FXML private TextField brandField, modelField;
	@FXML private ComboBox<String> widthBox, aspectBox, rimSizeBox, typeBox, newUsedBox, qtyBox;
	
	
	/**
	 * initializes all the values of the combo boxes
	 */
	@FXML public void initialize() {
		brandField.setText("");
		modelField.setText("");
		
		String[] widths = {"175","185", "195", "205", "215", "225", "235", "245", "255", "265", "275", "285", "295",
				"305", "315", "325"};
		
		widthBox.getItems().setAll(widths);
		
		String[] aspect = {"40", "45", "50", "55", "60", "65", "70", "75", "80", "85" };
		aspectBox.getItems().setAll(aspect);
		
		String[] rim = { "13", "14", "15", "16", "17", "18", "19", "20", "21", "22"};
		rimSizeBox.getItems().setAll(rim);
		
		String[] types = {"All-Season", "Light Truck", "Snow", "Performance"};
		typeBox.getItems().setAll(types);
		typeBox.getSelectionModel().selectFirst();
		
		String[] newUsed = {"New", "Used"};
		newUsedBox.getItems().setAll(newUsed);
		newUsedBox.getSelectionModel().selectFirst();
		
		
		String[] qty = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
		qtyBox.getItems().setAll(qty);
		

	}
	
	
	/**
	 * executes when the save button is clicked, will check and make sure that the proper stuff is filled in
	 * then will save the new tire to the database
	 * @param ae
	 */
	@FXML protected void saveButtonHandler(ActionEvent ae) {
		Tire temp = new Tire();
		
		
		if(brandField.getText().isEmpty() || modelField.getText().isEmpty() || 
				widthBox.getSelectionModel().getSelectedIndex() == -1 || aspectBox.getSelectionModel().getSelectedIndex() == -1 || 
				rimSizeBox.getSelectionModel().getSelectedIndex() == -1|| qtyBox.getSelectionModel().getSelectedIndex() == -1) {
					Alert alert = new Alert(AlertType.ERROR, "One or more fields are incomplete", ButtonType.OK);
					alert.showAndWait();
					return;
		}
		
		temp.setBrand(brandField.getText());
		if(newUsedBox.getSelectionModel().getSelectedItem().equals("Used")) {
			temp.setTireModel(modelField.getText() + "*");
			temp.setIsNew(false);
		} else {
			temp.setTireModel(modelField.getText());
			temp.setIsNew(true);
		}
		temp.setWidth(Integer.parseInt(widthBox.getValue()));
		temp.setAspectRatio(Integer.parseInt(aspectBox.getValue()));
		temp.setRimSize(Integer.parseInt(rimSizeBox.getValue()));
		temp.setQuantity(Integer.parseInt(qtyBox.getValue()));
		
		int id = App.dao.checkForTire(temp);
		if(id != -1) {
			//tire already in the database
			temp.setId(id);

			Alert alert = new Alert(AlertType.CONFIRMATION, "Tire already in database, updating quantity", ButtonType.OK);
			alert.showAndWait();

			App.dao.incrementQuantity(temp, Integer.parseInt(qtyBox.getValue()));
		} else {
			temp.setType(typeBox.getValue());
			App.dao.addTireToDatabase(temp);
		}

		Alert alert = new Alert(AlertType.CONFIRMATION, "Tires added to inventory", ButtonType.OK);
		alert.showAndWait();
		
		//clear all fields
		brandField.setText("");
		modelField.setText("");
		
		reset();
	}
	
	/**
	 * returns to the last tab that was being used
	 * @param ae
	 */
	@FXML protected void cancelButtonHandler(ActionEvent ae) {
		mainController.backTab();
	}
	
	@FXML protected void inAndOutHandler(ActionEvent ae) {
		Tire temp = new Tire();
		
		
		if(brandField.getText().isEmpty() || modelField.getText().isEmpty() || 
				widthBox.getSelectionModel().getSelectedIndex() == -1 || aspectBox.getSelectionModel().getSelectedIndex() == -1 || 
				rimSizeBox.getSelectionModel().getSelectedIndex() == -1|| qtyBox.getSelectionModel().getSelectedIndex() == -1) {
					Alert alert = new Alert(AlertType.ERROR, "One or more fields are incomplete", ButtonType.OK);
					alert.showAndWait();
					return;
		}
		
		temp.setBrand(brandField.getText());
		if(newUsedBox.getSelectionModel().getSelectedItem().equals("Used")) {
			temp.setTireModel(modelField.getText() + "*");
			temp.setIsNew(false);
		} else {
			temp.setTireModel(modelField.getText());
			temp.setIsNew(true);
		}
		temp.setWidth(Integer.parseInt(widthBox.getValue()));
		temp.setAspectRatio(Integer.parseInt(aspectBox.getValue()));
		temp.setRimSize(Integer.parseInt(rimSizeBox.getValue()));
		temp.setQuantity(Integer.parseInt(qtyBox.getValue()));
		
		int id = App.dao.checkForTire(temp);
		if(id != -1) {
			//tire already in the database
			temp.setId(id);

//			Alert alert = new Alert(AlertType.CONFIRMATION, "Tire already in database, updating quantity", ButtonType.OK);
//			alert.showAndWait();

			App.dao.addToSalesOnly(temp, Integer.parseInt(qtyBox.getValue()));
			
//			App.dao.incrementQuantity(temp, Integer.parseInt(qtyBox.getValue()));
//			App.dao.decrementQuantity(temp, Integer.parseInt(qtyBox.getValue()));
		} else {
			temp.setType(typeBox.getValue());
			App.dao.addTireToDatabase(temp);
			App.dao.decrementQuantity(temp, Integer.parseInt(qtyBox.getValue()));
		}
		
		reset();
	}
	
	public void reset() {
		qtyBox.getItems().clear();
		widthBox.getItems().clear();
		aspectBox.getItems().clear();
		rimSizeBox.getItems().clear();
		
		initialize();
	}
	
}
