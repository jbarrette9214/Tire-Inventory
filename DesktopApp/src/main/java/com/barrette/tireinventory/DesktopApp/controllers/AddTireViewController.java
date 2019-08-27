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
	
	@FXML public void initialize() {
/*
		barcodeField.setText("");
		//listener to check if barcode is already entered in the database and update the fields accordingly
		barcodeField.focusedProperty().addListener((ov, oldv, newV) -> {
			if(!newV) {
				//check the database and check if the barcode exists
				RestServices rest = new RestServices();
				Tire temp = rest.findByBarcode(barcodeField.getText());
				if(temp != null) {
					brandField.setText(temp.getBrand());
					modelField.setText(temp.getTireModel());
					widthBox.getItems().clear();
					widthBox.setValue(Integer.toString(temp.getWidth()));
					aspectBox.getItems().clear();
					aspectBox.setValue(Integer.toString(temp.getAspectRatio()));
					rimSizeBox.getItems().clear();
					rimSizeBox.setValue(Integer.toString(temp.getRimSize()));
					typeBox.getItems().clear();
					typeBox.setValue(temp.getType());
					qtyBox.requestFocus();
				}
	
			}
		});
		
*/		
		brandField.setText("");
		modelField.setText("");
		
		String[] widths = {"<175","185", "195", "205", "215", "225", "235", "245", "255", "265", "275", "285", ">295"};
		
		widthBox.getItems().setAll(widths);
		
		String[] aspect = {"40", "45", "50", "55", "60", "65", "70", "75", "80", "85" };
		aspectBox.getItems().setAll(aspect);
		
		String[] rim = {"13", "14", "15", "16", "17", "18", "19", "20", "21", "22"};
		rimSizeBox.getItems().setAll(rim);
		
		String[] types = {"All-Season", "Light Truck", "Snow", "Performance"};
		typeBox.getItems().setAll(types);
		
		String[] newUsed = {"New", "Used"};
		newUsedBox.getItems().setAll(newUsed);
		newUsedBox.getSelectionModel().selectFirst();
		
		
		String[] qty = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
		qtyBox.getItems().setAll(qty);

	}
	
	@FXML protected void saveButtonHandler(ActionEvent ae) {
		Tire temp = new Tire();
		
		temp.setBrand(brandField.getText());
		temp.setTireModel(modelField.getText());
		temp.setWidth(Integer.parseInt(widthBox.getValue()));
		temp.setAspectRatio(Integer.parseInt(aspectBox.getValue()));
		temp.setRimSize(Integer.parseInt(rimSizeBox.getValue()));
		
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
		
/*		RestServices rest = new RestServices();
		
		Tire retrieved = rest.findByBarcode(barcodeField.getText());
		
		String message = "";
		
		if(retrieved !=  null) {
			
			//already in the inventory
			int newQty;
			newQty = retrieved.getQuantity() + temp.getQuantity();
			message = rest.addTiresToInventory(retrieved, newQty);
			
		} else {
			temp.setBarcode(barcodeField.getText());

			temp.setBrand(brandField.getText());
			temp.setTireModel(modelField.getText());
			temp.setWidth(Integer.parseInt(widthBox.getValue()));
			temp.setAspectRatio(Integer.parseInt(aspectBox.getValue()));
			temp.setRimSize(Integer.parseInt(rimSizeBox.getValue()));
			temp.setType(typeBox.getValue());
			temp.setQuantity(Integer.parseInt(qtyBox.getValue()));

			
			message = rest.addNewTire(temp);
		}
		
		
/*		boolean found = false;
		int newQty = 0;
		for(Tire t : retrieved) {
			if(t.toString().equals(temp.toString())) {
				//tire already in database
				newQty = t.getQuantity() + temp.getQuantity();
				temp = t;
				temp.setQuantity(newQty);
				found = true;
				break;
			}
		}
		
		if(found == true && newQty != 0) {
			//was already in the database, so update
			rest.addTiresToInventory(temp, newQty);
		} else {
			//not already in database, so create new
			rest.addNewTire(temp);
		}      
		
		String message = rest.addNewTire(temp);
		
		
		
		Alert alert = new Alert(AlertType.NONE, message, ButtonType.OK);
		alert.showAndWait();*/
	}
	
	@FXML protected void cancelButtonHandler(ActionEvent ae) {
		mainController.backTab();
	}
	
	public void reset() {
		initialize();
	}
	
}
